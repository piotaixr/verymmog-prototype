package com.verymmog;

import com.verymmog.nioengine.EngineInterface;
import com.verymmog.nioengine.event.EventDispatcherInterface;
import com.verymmog.nioengine.event.EventListener;
import com.verymmog.nioengine.event.EventProvider;
import com.verymmog.nioengine.event.events.DisconnectEvent;
import com.verymmog.nioengine.event.events.NewConnectionEvent;
import com.verymmog.nioengine.event.events.ReceiveEvent;
import com.verymmog.nioengine.event.events.data.DisconnectEventData;
import com.verymmog.nioengine.event.events.data.NewConnectionEventData;
import com.verymmog.nioengine.event.events.data.ReceiveEventData;
import com.verymmog.nioengine.extension.BaseEngineExtension;
import com.verymmog.nioengine.listener.AutoRegisterListener;
import com.verymmog.nioengine.output.MulticastOutput;
import com.verymmog.nioengine.output.OutputInterface;

import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class PongServerExtension extends BaseEngineExtension {

    private ServerSocketChannel ssc;
    private MulticastOutput<byte[]> output = new MulticastOutput<>();

    public PongServerExtension(ServerSocketChannel ssc) {
        this.ssc = ssc;
    }

    EventListener<ReceiveEventData> receiveDataListener = new EventListener<ReceiveEventData>() {
        @Override
        public void listen(ReceiveEventData event) {
            EngineInterface engine = event.getEngine();

            Object o = new Object();
            synchronized (o) {
                try {
                    o.wait(0L, 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Set<SelectableChannel> channels = engine.getChannels();
            synchronized (channels) {
                for (SelectableChannel channel : channels) {
                    if (channel instanceof SocketChannel) {
                        engine.send(channel, event.getData());
                    }
                }
            }
        }
    };
    AutoRegisterListener<ReceiveEvent, ReceiveEventData> autoReceive = new AutoRegisterListener<>(
            EventProvider.provider().get(ReceiveEvent.class),
            receiveDataListener
    );

    EventListener<NewConnectionEventData> newConnectionListener = new EventListener<NewConnectionEventData>() {

        @Override
        public void listen(NewConnectionEventData event) {
            System.out.println("Nouvelle Connexion");
            OutputInterface<byte[]> o = event.getEngine().createOutput(event.getNewChannel());

            output.addReceiver(event.getNewChannel(), o);
        }
    };

    EventListener<DisconnectEventData> disconnectListener = new EventListener<DisconnectEventData>() {
        @Override
        public void listen(DisconnectEventData event) {
            output.removeReceiver(event.getChannel());
        }
    };

    AutoRegisterListener<DisconnectEvent, DisconnectEventData> autoDisconnectListener = new AutoRegisterListener<>(
            EventProvider.provider().get(DisconnectEvent.class),
            disconnectListener
    );

    @Override
    public void register(EventDispatcherInterface dispatcher) {
        super.register(dispatcher);
        dispatcher
                .register(ssc, EventProvider.provider().get(NewConnectionEvent.class), autoReceive)
                .register(ssc, EventProvider.provider().get(NewConnectionEvent.class), newConnectionListener)
                .register(ssc, EventProvider.provider().get(NewConnectionEvent.class), autoDisconnectListener);

    }

    @Override
    public void unregister(EventDispatcherInterface dispatcher) {
        super.unregister(dispatcher);

        dispatcher.unregister(newConnectionListener,
                autoReceive, receiveDataListener,
                autoDisconnectListener, disconnectListener
        );
    }
}
