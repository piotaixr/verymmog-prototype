package com.verymmog.network.player;

import com.verymmog.model.PlayerInterface;
import com.verymmog.network.ClientNetworkInterface;
import com.verymmog.network.message.event.ReceiveMessageEvent;
import com.verymmog.network.message.event.ReceiveMessageEventData;
import com.verymmog.network.message.listener.MessageTransformer;
import com.verymmog.network.message.messages.*;
import com.verymmog.nioengine.event.EventDispatcherInterface;
import com.verymmog.nioengine.event.EventListener;
import com.verymmog.nioengine.event.EventProvider;
import com.verymmog.nioengine.event.events.DisconnectEvent;
import com.verymmog.nioengine.event.events.ReceiveEvent;
import com.verymmog.nioengine.event.events.data.DisconnectEventData;
import com.verymmog.nioengine.extension.BaseEngineExtension;
import com.verymmog.nioengine.output.MulticastOutput;
import com.verymmog.nioengine.output.OutputInterface;
import com.verymmog.nioengine.output.SerializableOutput;
import com.verymmog.util.ObjectSerializer;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.util.List;

public class PlayerNetwork extends BaseEngineExtension implements ClientNetworkInterface {

    private List<InetSocketAddress> nextLeaders;
    private Logger logger = Logger.getLogger(getClass());
    /**
     * Channel vers le leader
     */
    private SocketChannel channelLeader;
    /**
     * Identifiant du client. Est normalement egal a player#getName. Affecté
     * uniquement si le 'manager' a accepté l'identification du client.
     */
    private String clientId;
    /**
     * Multicast output pour notifier la position a tous les leaders auquels on est connectés
     */
    private MulticastOutput<byte[]> mout = new MulticastOutput<>();
    /**
     * Responsable de l'appel périodique de la fonction notifiant la mise à jour de la position au leader
     */
    private PositionUpdater updater = new PositionUpdater(new SerializableOutput(mout));
    private PlayerInterface player;
    /**
     * Etat de l'extension
     */
    private PlayerState state = PlayerState.CREATED;
    /**
     * Listener notifié a chaque message de mise à a jour des positions recues du leader
     */
    private ClientDataUpdaterInterface dataUpdater;
    /**
     * Jeton permettat d'identifier le joueur sans passer par le manager (censé être secret)
     */
    private String token;

    public PlayerNetwork(SocketChannel channelLeader,
                         ClientDataUpdaterInterface dataUpdater,
                         PlayerInterface player,
                         String token) {
        this.channelLeader = channelLeader;
        this.dataUpdater = dataUpdater;
        this.player = player;
        this.token = token;
    }


    /**
     * Listener pour gérer une déconnexion d'un leader
     */
    private EventListener<DisconnectEventData> disconnectListener = new EventListener<DisconnectEventData>() {
        @Override
        public void listen(DisconnectEventData event) {
            //TODO se connecter au leader suivant
            connectToNextLeader();
        }

        private void connectToNextLeader() {

        }
    };

    private EventListener<ReceiveMessageEventData> receiveMessageListener = new EventListener<ReceiveMessageEventData>() {
        @Override
        public void listen(ReceiveMessageEventData event) {
            MessageInterface mi = event.getMessage();

            if (mi instanceof AuthenticationGrantedMessage && state == PlayerState.WAITING_IDENTIFICATION_CONFIRMATION) {
                AuthenticationGrantedMessage message = (AuthenticationGrantedMessage) mi;

                clientId = message.getId();

                changeState(PlayerState.IDENTIFIED_WAITING_POSITION);
            } else if (mi instanceof SetBasePositionMessage && state == PlayerState.IDENTIFIED_WAITING_POSITION) {
                SetBasePositionMessage m = (SetBasePositionMessage) mi;
                player.updatePosition(m.getX(), m.getY());
                updater.start(player);

                OutputInterface<byte[]> leaderOutput = getEngine().createOutput(event.getChannel());
                mout.addReceiver(event.getChannel(), leaderOutput);

                changeState(PlayerState.READY);
            } else if (mi instanceof ClientDataUpdateMessage && state == PlayerState.READY) {
                dataUpdater.updatePlayers((ClientDataUpdateMessage) mi);
            }
        }
    };

    protected void changeState(PlayerState newState) {
        logger.debug("ChangeState from " + state + " to " + newState);

        state = newState;
    }

    /**
     * Responsable de la transformation en message des données recues du leader
     */
    private MessageTransformer messageTransformer = new MessageTransformer();


    @Override
    protected void doBoot() {
        super.doBoot();

        initIdentification(player.getName());
    }

    /**
     * Notifie au leader le nom du client.
     *
     * @param clientId
     */
    public void initIdentification(String clientId) {
        if (state == PlayerState.CREATED) {
            changeState(PlayerState.WAITING_IDENTIFICATION_CONFIRMATION);

            sendLeader(new TokenIdentifyMessage(clientId, token));
        }
    }

    @Override
    public void sendLeader(MessageInterface message) {
        if (channelLeader != null && channelLeader.isOpen()) {
            getEngine().send(
                    channelLeader,
                    ObjectSerializer.toBytes(message)
            );
        } else {
            //TODO erreur: envoi de message quand non identifié avec le leader ou channel fermé
        }
    }

    @Override
    public void register(EventDispatcherInterface dispatcher) {
        super.register(dispatcher);

        dispatcher
                .register((SelectableChannel) channelLeader,
                        EventProvider.provider().get(DisconnectEvent.class),
                        disconnectListener)
                .register(channelLeader, EventProvider.provider().get(ReceiveEvent.class), messageTransformer)
                .register(channelLeader,
                        EventProvider.provider().get(ReceiveMessageEvent.class),
                        receiveMessageListener);
    }

    @Override
    public void unregister(EventDispatcherInterface dispatcher) {
        super.unregister(dispatcher);

        dispatcher
                .unregister(disconnectListener)
                .unregister(messageTransformer)
                .unregister(receiveMessageListener);
    }
}
