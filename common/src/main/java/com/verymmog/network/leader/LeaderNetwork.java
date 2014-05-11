package com.verymmog.network.leader;

import com.google.common.base.Function;
import com.google.common.cache.CacheLoader;
import com.verymmog.model.map.ClusterInterface;
import com.verymmog.model.map.MapInterface;
import com.verymmog.network.DataPlayer;
import com.verymmog.network.LeaderNetworkInterface;
import com.verymmog.network.leader.clientrepresentation.LeaderClient;
import com.verymmog.network.manager.CacheImplPositionManager;
import com.verymmog.network.manager.PositionManagerInterface;
import com.verymmog.network.manager.user.TokenManager;
import com.verymmog.network.message.event.ReceiveMessageEvent;
import com.verymmog.network.message.event.ReceiveMessageEventData;
import com.verymmog.network.message.listener.AutoregisterMessageReceiveListener;
import com.verymmog.network.message.listener.AutoregisterMessageTransformer;
import com.verymmog.network.message.listener.MessageTransformer;
import com.verymmog.network.message.messages.*;
import com.verymmog.nioengine.event.EventDispatcherInterface;
import com.verymmog.nioengine.event.EventListener;
import com.verymmog.nioengine.event.EventProvider;
import com.verymmog.nioengine.event.events.NewConnectionEvent;
import com.verymmog.nioengine.event.events.ReceiveEvent;
import com.verymmog.nioengine.event.events.data.DisconnectEventData;
import com.verymmog.nioengine.event.events.data.NewConnectionEventData;
import com.verymmog.nioengine.extension.BaseEngineExtension;
import com.verymmog.nioengine.listener.AutoregisterDisconnectListener;
import com.verymmog.nioengine.output.MulticastOutput;
import com.verymmog.nioengine.output.SerializableOutput;
import com.verymmog.util.ObjectSerializer;
import com.verymmog.util.Ports;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LeaderNetwork extends BaseEngineExtension implements LeaderNetworkInterface {

    private Lock lock = new ReentrantLock(true);
    private Map<String, LeaderClient> identifiedClients = new HashMap<>();
    private Map<SocketChannel, LeaderClient> clients = new HashMap<>();

    private Logger logger = Logger.getLogger(this.getClass());
    private ServerSocketChannel serverSocket;
    private SocketChannel managerChannel;
    private String id, token;
    private MapInterface map;
    private ClusterInterface cluster;
    private TokenManager tokenManager;
    private PositionManagerInterface positionManager;
    private Timer timer = new Timer();
    private MulticastOutput<byte[]> mout = new MulticastOutput<>();
    private SerializableOutput serializableOutput = new SerializableOutput(mout);

    public LeaderNetwork(SocketChannel managerChannel,
                         String id,
                         String token,
                         MapInterface map,
                         ClusterInterface cluster,
                         TokenManager tokenManager) {
        this.managerChannel = managerChannel;
        this.id = id;
        this.token = token;
        this.map = map;
        this.cluster = cluster;
        this.tokenManager = tokenManager;

        positionManager = new CacheImplPositionManager(CacheLoader.from(new Function<SocketChannel, DataPlayer>() {
            @Override
            public DataPlayer apply(SocketChannel channel) {
                LeaderClient cl = clients.get(channel);

                return new DataPlayer(cl.getId(), 0, 0, 0, 0);
            }
        }), new DistanceComparator(map.getHeight() / 2, 0));
    }

    @Override
    protected void doBoot() {
        super.doBoot();

        logger.trace("LeaderNetwork booted");

        getEngine().send(managerChannel,
                ObjectSerializer.toBytes(new LeaderClusterTokenIdentifyMessage(id,
                        token,
                        cluster.getId(),
                        Ports.LEADER_CONNECT_PLAYERS)));
    }

    /**
     * Transformation en message des données recues du Manager
     */
    private MessageTransformer messageTransformerFromManager = new MessageTransformer();
    private EventListener<ReceiveMessageEventData> receiveFromManager = new EventListener<ReceiveMessageEventData>() {
        @Override
        public void listen(ReceiveMessageEventData event) {
            MessageInterface message = event.getMessage();

            if (message instanceof AuthenticationDeniedMessage) {
                desinstall();
            } else if (message instanceof AuthenticationGrantedMessage) {
                logger.trace("Le manager a accepté notre connexion");
                try {
                    initializeServerSocket();
                } catch (IOException e) {
                    desinstall();
                }
            }
        }
    };

    /**
     * Initialise le socket pour la connexion des clients
     *
     * @throws IOException
     */
    private void initializeServerSocket() throws IOException {
        logger.trace("Initialisation du serverSocket pour la connexion des clients");

        serverSocket = ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        serverSocket.bind(new InetSocketAddress((InetAddress) null,
                Ports.LEADER_CONNECT_PLAYERS));

        getEngine().registerChannel(serverSocket, SelectionKey.OP_ACCEPT);

        getEngine().getEventDispatcher()
                   .register(serverSocket,
                           EventProvider.provider().get(NewConnectionEvent.class),
                           autoTransformerFromClients,
                           autoReceiveFromClients,
                           newConnectionListener,
                           autoregisterDisconnectClientsListener)
        ;

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (positionManager.all().size() != 0) {
                    SortedSet<DataPlayer> set = positionManager.all();
                    synchronized (set) {
                        serializableOutput.send(new ClientDataUpdateMessage(set));
                    }
                } else {
//                    logger.trace("Pas de joueur, on envoie pas de message d'update");
                }
            }
        }, new Date(), 100);
    }

    private AutoregisterMessageTransformer autoTransformerFromClients = new AutoregisterMessageTransformer(new MessageTransformer());
    private AutoregisterMessageReceiveListener autoReceiveFromClients = new AutoregisterMessageReceiveListener() {
        @Override
        protected void listen(ReceiveMessageEventData event) {
            MessageInterface message = event.getMessage();
            if (message instanceof TokenIdentifyMessage) {
                TokenIdentifyMessage m = (TokenIdentifyMessage) message;

                String id = m.getId();

                if (tokenManager.verify(id, m.getToken())) {
                    //Identification valide, deux cas possibles:
                    // deja présent: il est deja connecté => on deconnecte celui qui est deja connecté si le channel est différent
                    // pas présent: ok
                    SocketChannel channel = event.getChannel();
                    try {
                        lock.lock();

                        LeaderClient c = clients.get(channel);

                        if (c.isIdentified()) {
                            if (!c.getChannel().equals(channel)) {
                                //TODO: close le channel
                                System.out.println("Doublon identification");
                            } else {
                                //le message est un doublon, c'est une erreur du protocole
                            }
                        } else {
                            //Comportement par defaut
                            c.setId(id);
                            identifiedClients.put(id, c);

                            mout.addReceiver(c.getChannel(), getEngine().createOutput(c.getChannel()));
                        }

                    } finally {
                        lock.unlock();
                    }

                    event.getEngine().send(
                            event.getChannel(),
                            ObjectSerializer.toBytes(
                                    new AuthenticationGrantedMessage(id, m.getToken())
                            )
                    );

                    int rnd = (int) (Math.random() * 800);


                    event.getEngine()
                         .send(event.getChannel(), ObjectSerializer.toBytes(new SetBasePositionMessage(rnd, rnd)));
                }
            } else if (message instanceof UpdatePositionMessage) {
                UpdatePositionMessage m = (UpdatePositionMessage) message;
                LeaderClient cl = clients.get(event.getChannel());

                if (cl.isIdentified()) {
                    positionManager.updatePosition(event.getChannel(), m.getX(), m.getY(), m.getDx(), m.getDy());
                }
            }

        }
    };
    private AutoregisterDisconnectListener autoregisterDisconnectClientsListener = new AutoregisterDisconnectListener() {
        @Override
        protected void listen(DisconnectEventData event) {
            SelectableChannel channel = event.getChannel();

            if (channel instanceof SocketChannel) {
                logger.trace("Deconnexion d'un joueur");
                try {
                    lock.lock();

                    LeaderClient c = clients.remove(channel);

                    if (c != null && c.isIdentified()) {
                        logger.debug("Deconnexion de " + c.getId());
                        positionManager.forget(c.getChannel());
                        identifiedClients.remove(c.getId());
                        mout.removeReceiver(c.getChannel());
                    }

                } finally {
                    lock.unlock();
                }
            }
        }
    };


    private EventListener<NewConnectionEventData> newConnectionListener = new EventListener<NewConnectionEventData>() {
        @Override
        public void listen(NewConnectionEventData event) {
            //test peut-etre pas nécessaire
            if (event.getChannel() == serverSocket) {

                logger.trace("Connexion d'un joueur");
                SocketChannel newChannel = event.getNewChannel();
                try {
                    lock.lock();

                    if (!clients.containsKey(newChannel)) {
                        clients.put(newChannel, new LeaderClient(newChannel));
                    }

                } finally {
                    lock.unlock();
                }
            }
        }
    };

    @Override
    public void sendCluster(MessageInterface message) {
        for (SocketChannel chan : clients.keySet()) {
            byte[] data = ObjectSerializer.toBytes(message);
            getEngine().send(chan, data);
        }
    }

    @Override
    public void register(EventDispatcherInterface dispatcher) {
        dispatcher
                .register(managerChannel,
                        EventProvider.provider().get(ReceiveEvent.class),
                        messageTransformerFromManager)
                .register(managerChannel, EventProvider.provider().get(ReceiveMessageEvent.class), receiveFromManager)
        ;
    }

    @Override
    public void unregister(EventDispatcherInterface dispatcher) {
        dispatcher
                .unregister(messageTransformerFromManager, receiveFromManager,
                        autoReceiveFromClients, autoReceiveFromClients.getListener(),
                        autoTransformerFromClients, autoTransformerFromClients.getTransformer(),
                        newConnectionListener,
                        autoregisterDisconnectClientsListener, autoregisterDisconnectClientsListener.getListener())
        ;

    }
}
