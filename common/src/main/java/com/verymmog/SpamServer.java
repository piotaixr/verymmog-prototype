package com.verymmog;

import com.verymmog.nioengine.NioEngine;
import com.verymmog.nioengine.event.EventListener;
import com.verymmog.nioengine.event.EventProvider;
import com.verymmog.nioengine.event.events.NewConnectionEvent;
import com.verymmog.nioengine.event.events.ReceiveEvent;
import com.verymmog.nioengine.event.events.data.NewConnectionEventData;
import com.verymmog.nioengine.event.events.data.ReceiveEventData;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

public class SpamServer {
    public static void main(String[] args) throws IOException {
        NioEngine engine = new NioEngine(SelectorProvider.provider().openSelector());

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress((Inet4Address) null, 20202));

        engine.registerChannel(ssc, SelectionKey.OP_ACCEPT);

        engine.getEventDispatcher()
                .register(
                        EventProvider.provider().get(NewConnectionEvent.class),
                        new EventListener<NewConnectionEventData>() {

                            @Override
                            public void listen(final NewConnectionEventData event) {
                                new Thread("SPAMMER") {
                                    @Override
                                    public void run() {
                                        for (int i = 0; i < 1000; i++) {
                                            event.getEngine().send(event.getNewChannel(), (i + ": message").getBytes());
                                        }
                                    }
                                }.start();
                            }
                        })
                .register(
                        EventProvider.provider().get(ReceiveEvent.class),
                        new EventListener<ReceiveEventData>() {
                            @Override
                            public void listen(ReceiveEventData event) {
                                System.out.println("Retransmission de " + new String(event.getData()));
                                event.getEngine().send(event.getChannel(), event.getData());
                            }
                        }
                );

        engine.start();

    }
}
