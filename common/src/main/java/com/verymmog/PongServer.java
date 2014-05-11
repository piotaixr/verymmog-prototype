package com.verymmog;

import com.verymmog.nioengine.AsyncEngineBuffer;
import com.verymmog.nioengine.NioEngine;
import com.verymmog.nioengine.event.MultithreadedEventDispatcher;
import com.verymmog.nioengine.processor.SwingKeyProcessor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

public class PongServer {
    public static void main(String[] args) throws IOException {
        NioEngine engine = new NioEngine(
                SelectorProvider.provider().openSelector(),
                new MultithreadedEventDispatcher(20),
                new AsyncEngineBuffer()
        );
        engine.setKeyProcessor(new SwingKeyProcessor());

        engine.start();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress("localhost", 20202));

        engine.addExtension(new PongServerExtension(ssc));
        engine.registerChannel(ssc, SelectionKey.OP_ACCEPT);

    }
}
