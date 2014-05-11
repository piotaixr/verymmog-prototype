package com.verymmog.network.player.identify;

import com.verymmog.model.map.MapInterface;
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
import com.verymmog.nioengine.extension.TryConnectionExtension;
import com.verymmog.nioengine.extension.TryConnectionListener;
import com.verymmog.util.ObjectSerializer;
import org.apache.log4j.Logger;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

public abstract class ManagerConnectionExtension extends BaseEngineExtension {
    private Logger logger = Logger.getLogger(this.getClass());

    private String token;
    private SocketChannel managerChannel;
    private MessageTransformer messageTransformer = new MessageTransformer();
    private String login, password;

    protected ManagerConnectionExtension(SocketChannel managerChannel, String login, String password) {
        this.managerChannel = managerChannel;
        this.login = login;
        this.password = password;
    }

    private EventListener<DisconnectEventData> disconnectListener = new EventListener<DisconnectEventData>() {
        @Override
        public void listen(DisconnectEventData event) {
            onError();

            desinstall();
        }
    };

    private EventListener<ReceiveMessageEventData> receiveListener = new EventListener<ReceiveMessageEventData>() {
        @Override
        public void listen(ReceiveMessageEventData event) {
            MessageInterface message = event.getMessage();
            if (token == null && message instanceof AuthenticationGrantedMessage) {
                AuthenticationGrantedMessage m = (AuthenticationGrantedMessage) message;
                token = m.getToken();
                logger.trace("Authentication OK auprès du manager");
            } else if (token != null && message instanceof MapTransferMessage) {
                onReceiveMap(((MapTransferMessage) message).getMap());
                logger.trace("Map recue du manager");
            } else if (token != null && message instanceof SetBaseLeaderMessage) {
                SetBaseLeaderMessage m = (SetBaseLeaderMessage) message;

                logger.trace("Reception du leader");

                TryConnectionExtension ext = new TryConnectionExtension(m.getLeaderAddress());
                ext.addListener(new TryConnectionListener() {
                    @Override
                    public void onError() {
                        ManagerConnectionExtension.this.onError();

                        desinstall();
                    }

                    @Override
                    public void onSuccess(SocketChannel channel) {
                        ManagerConnectionExtension.this.onInitialLeader(channel, token);

                        desinstall();
                    }
                });

                getEngine().addExtension(ext);
            }
        }
    };


    @Override
    protected void doBoot() {
        super.doBoot();

        getEngine().send(managerChannel, ObjectSerializer.toBytes(new AuthenticationMessage(login, password)));
    }

    protected abstract void onError();

    protected abstract void onReceiveMap(MapInterface map);

    protected abstract void onInitialLeader(SocketChannel leaderChannel, String token);

    @Override
    public void unregister(EventDispatcherInterface dispatcher) {
        super.unregister(dispatcher);

        dispatcher.unregister(disconnectListener, messageTransformer, receiveListener);
    }

    @Override
    public void register(EventDispatcherInterface dispatcher) {
        super.register(dispatcher);

        dispatcher
                .register((SelectableChannel) managerChannel,
                        EventProvider.provider().get(DisconnectEvent.class),
                        disconnectListener)
                .register(managerChannel, EventProvider.provider().get(ReceiveEvent.class), messageTransformer)
                .register(managerChannel, EventProvider.provider().get(ReceiveMessageEvent.class), receiveListener);
    }
}
