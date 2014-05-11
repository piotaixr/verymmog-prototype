package com.verymmog.network.manager;

import com.google.common.collect.ImmutableList;
import com.verymmog.model.UserInterface;
import com.verymmog.model.map.ClusterInterface;
import com.verymmog.model.map.MapInterface;
import com.verymmog.network.manager.clusterselection.ClusterSelectionStrategy;
import com.verymmog.network.manager.user.TokenManager;
import com.verymmog.network.manager.user.UserAuthenticator;
import com.verymmog.network.manager.user.provider.UserProviderInterface;
import com.verymmog.network.message.event.ReceiveMessageEventData;
import com.verymmog.network.message.listener.AutoregisterMessageReceiveListener;
import com.verymmog.network.message.listener.AutoregisterMessageTransformer;
import com.verymmog.network.message.listener.MessageTransformer;
import com.verymmog.network.message.messages.*;
import com.verymmog.nioengine.event.EventDispatcherInterface;
import com.verymmog.nioengine.event.EventListener;
import com.verymmog.nioengine.event.EventProvider;
import com.verymmog.nioengine.event.events.NewConnectionEvent;
import com.verymmog.nioengine.event.events.data.DisconnectEventData;
import com.verymmog.nioengine.event.events.data.NewConnectionEventData;
import com.verymmog.nioengine.extension.BaseEngineExtension;
import com.verymmog.nioengine.listener.AutoregisterDisconnectListener;
import com.verymmog.util.Callback;
import com.verymmog.util.ObjectSerializer;
import com.verymmog.util.Ports;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ManagerExtension extends BaseEngineExtension {

    private Logger logger;
    private ServerSocketChannel serverSocketPlayers;
    private ServerSocketChannel serverSocketLeaders;
    private UserProviderInterface userProvider;
    private TokenManager tokenManager;
    private UserAuthenticator userAuthenticator;
    private MapInterface map;
    private LeaderAddressManagerInterface leaderAddressManager;
    private ClusterSelectionStrategy clusterSelectionStrategy;

    public ManagerExtension(ServerSocketChannel serverSocketPlayers,
                            ServerSocketChannel serverSocketLeaders,
                            UserProviderInterface userProvider,
                            TokenManager tokenManager,
                            UserAuthenticator userAuthenticator,
                            MapInterface map,
                            LeaderAddressManagerInterface leaderAddressManager,
                            ClusterSelectionStrategy clusterSelectionStrategy) {
        logger = Logger.getLogger(this.getClass());
        this.serverSocketPlayers = serverSocketPlayers;
        this.serverSocketLeaders = serverSocketLeaders;
        this.userProvider = userProvider;
        this.tokenManager = tokenManager;
        this.userAuthenticator = userAuthenticator;
        this.map = map;
        this.leaderAddressManager = leaderAddressManager;
        this.clusterSelectionStrategy = clusterSelectionStrategy;
    }

    /**
     * Listener pour la reception des messages des nouveaux clients qui se
     * connectent
     */
    private AutoregisterMessageReceiveListener autoregisterClientMessageReceiveListener = new AutoregisterMessageReceiveListener() {
        @Override
        protected void listen(final ReceiveMessageEventData event) {
            MessageInterface message = event.getMessage();

            if (message instanceof AuthenticationMessage) {
                logger.trace("Demande authentication client recue");
                AuthenticationMessage m = (AuthenticationMessage) message;

                UserInterface user = userProvider.getUser(m.getLogin());
                boolean authenticated = false;

                if (user != null) {
                    authenticated = userAuthenticator.authenticate(user, m.getPassword());
                }

                MessageInterface response;
                if (user != null && authenticated) {
                    response = new AuthenticationGrantedMessage(user.getLogin(), tokenManager.generate(user));
                } else {
                    response = new AuthenticationDeniedMessage();
                }

                event.getEngine().send(event.getChannel(), ObjectSerializer.toBytes(response));
                event.getEngine().send(event.getChannel(), ObjectSerializer.toBytes(new MapTransferMessage(map)));


                ImmutableList<ClusterInterface> externalClusters = map.getClusters()
                                                                      .getClustersAtLevel(map.getClusters()
                                                                                             .getMaxClusterLevel());
                ClusterInterface destination = clusterSelectionStrategy.select(externalClusters);

                leaderAddressManager.resolve(destination, new Callback<InetSocketAddress>() {
                    @Override
                    public void call(InetSocketAddress input) {
                        getEngine().send(event.getChannel(), ObjectSerializer.toBytes(new SetBaseLeaderMessage(input)));
                    }
                });
            }
        }
    };

    private AutoregisterMessageReceiveListener autoregisterLeaderMessageReceiveListener = new AutoregisterMessageReceiveListener() {
        @Override
        protected void listen(ReceiveMessageEventData event) {
            MessageInterface message = event.getMessage();
            if (message instanceof LeaderClusterTokenIdentifyMessage) {
                logger.trace("Demande authentication leader recue");
                LeaderClusterTokenIdentifyMessage m = (LeaderClusterTokenIdentifyMessage) message;

                if (tokenManager.verify(m.getId(), m.getToken())) {
                    //Identified
                    //TODO affecter un cluster
                    Socket s = event.getChannel().socket();
                    leaderAddressManager.register(
                            map.getClusters().getCluster(m.getClusterId()),
                            new InetSocketAddress(s.getInetAddress(), m.getPort())
                    );

                    event.getEngine()
                         .send(event.getChannel(),
                                 ObjectSerializer.toBytes(new AuthenticationGrantedMessage(m.getId(), m.getToken())));
                } else {
                    event.getEngine()
                         .send(event.getChannel(), ObjectSerializer.toBytes(new AuthenticationDeniedMessage()));
                }
            }
        }
    };

    /**
     * Listener pour construire les messages des nouveaux clients
     */
    private AutoregisterMessageTransformer autoregisterClientMessageTransformer = new AutoregisterMessageTransformer(new MessageTransformer());
    /**
     * Listener pour construire les messages des leaders
     */
    private AutoregisterMessageTransformer autoregisterLeaderMessageTransformer = new AutoregisterMessageTransformer(new MessageTransformer());

    /**
     * Listener pour autoregister de l'interet de deconnexion des nouveaux
     * clients
     */
    private AutoregisterDisconnectListener autoregisterDisconnectPlayers = new AutoregisterDisconnectListener() {
        @Override
        protected void listen(DisconnectEventData event) {
            logger.info("Deconnexion joueur");
        }
    };

    /**
     * Listener pour autoregister de l'interet de deconnexion des nouveaux
     * leaders
     */
    private AutoregisterDisconnectListener autoregisterDisconnectLeaders = new AutoregisterDisconnectListener() {
        @Override
        protected void listen(DisconnectEventData event) {
            if (event.getChannel() instanceof SocketChannel) {
                logger.info("Deconnexion leader");
                SocketChannel chan = (SocketChannel) event.getChannel();
                Socket s = chan.socket();
                leaderAddressManager.unregister(new InetSocketAddress(s.getInetAddress(), Ports.LEADER_CONNECT_PLAYERS));
            }
        }
    };

    private EventListener<NewConnectionEventData> connectLeaderListener = new EventListener<NewConnectionEventData>() {
        @Override
        public void listen(NewConnectionEventData event) {
            logger.trace("Connexion d'un leader " + event.getNewChannel().toString());
        }
    };

    @Override
    protected void doBoot() {
        getEngine().registerChannel(serverSocketPlayers, SelectionKey.OP_ACCEPT);
        getEngine().registerChannel(serverSocketLeaders, SelectionKey.OP_ACCEPT);

        super.doBoot();

        logger.trace("ManagerExtension fin boot");
    }

    @Override
    public void register(EventDispatcherInterface dispatcher) {
        super.register(dispatcher);

        dispatcher
                .register(serverSocketPlayers,
                        EventProvider.provider().get(NewConnectionEvent.class),
                        autoregisterDisconnectPlayers,
                        autoregisterClientMessageTransformer,
                        autoregisterClientMessageReceiveListener)
                .register(serverSocketLeaders,
                        EventProvider.provider().get(NewConnectionEvent.class),
                        autoregisterDisconnectLeaders,
                        autoregisterLeaderMessageTransformer,
                        autoregisterLeaderMessageReceiveListener,
                        connectLeaderListener);
    }

    @Override
    public void unregister(EventDispatcherInterface dispatcher) {
        super.unregister(dispatcher);

        dispatcher.unregister(
                //Clients
                autoregisterDisconnectPlayers, autoregisterDisconnectPlayers.getListener(),
                autoregisterClientMessageTransformer, autoregisterClientMessageTransformer.getTransformer(),
                autoregisterClientMessageReceiveListener, autoregisterClientMessageReceiveListener.getListener(),
                //leaders
                autoregisterDisconnectLeaders, autoregisterDisconnectLeaders.getListener(),
                autoregisterLeaderMessageTransformer, autoregisterLeaderMessageTransformer.getTransformer(),
                autoregisterLeaderMessageReceiveListener, autoregisterLeaderMessageReceiveListener.getListener()
        );
    }
}
