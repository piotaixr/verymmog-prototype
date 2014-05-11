package com.verymmog.nioengine.extension;

import com.verymmog.nioengine.event.EventDispatcherInterface;
import com.verymmog.nioengine.event.EventListener;
import com.verymmog.nioengine.event.EventProvider;
import com.verymmog.nioengine.event.events.ConnectionAccepted;
import com.verymmog.nioengine.event.events.DisconnectEvent;
import com.verymmog.nioengine.event.events.data.ConnectionAcceptedEventData;
import com.verymmog.nioengine.event.events.data.DisconnectEventData;

import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TryConnectionExtension extends BaseEngineExtension {

    private SocketChannel channel = null;
    private InetSocketAddress address;
    private Set<TryConnectionListener> listeners = Collections.synchronizedSet(new HashSet<TryConnectionListener>());

    public TryConnectionExtension(String host, int port) {
        this(new InetSocketAddress(host, port));
    }

    public TryConnectionExtension(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    protected void doBoot() {

        channel = getEngine().connect(address);

        if (channel == null) {
            error();
        }

        super.doBoot();
    }

    public void addListener(TryConnectionListener listener) {
        listeners.add(listener);
    }

    private EventListener<DisconnectEventData> disconnectListener = new EventListener<DisconnectEventData>() {
        @Override
        public void listen(DisconnectEventData event) {
            error();
        }
    };

    private EventListener<ConnectionAcceptedEventData> connectionAcceptedListener = new EventListener<ConnectionAcceptedEventData>() {
        @Override
        public void listen(ConnectionAcceptedEventData event) {
            success();
        }
    };

    @Override
    public void register(EventDispatcherInterface dispatcher) {
        super.register(dispatcher);

        dispatcher.register((SelectableChannel) channel,
                EventProvider.provider().get(DisconnectEvent.class),
                disconnectListener)
                  .register(channel,
                          EventProvider.provider().get(ConnectionAccepted.class),
                          connectionAcceptedListener);
    }

    @Override
    public void unregister(EventDispatcherInterface dispatcher) {
        super.unregister(dispatcher);

        dispatcher.unregister(disconnectListener, connectionAcceptedListener);
    }

    protected void success() {
        System.out.println("SUCCESS");
        synchronized (listeners) {
            for (TryConnectionListener listener : listeners) {
                listener.onSuccess(channel);
            }
        }

        desinstall();
    }

    protected void desinstall() {
        getEngine().removeExtension(this);
    }

    protected void error() {
        System.out.println("ERROR");
        synchronized (listeners) {
            for (TryConnectionListener listener : listeners) {
                listener.onError();
            }
        }

        desinstall();
    }
}
