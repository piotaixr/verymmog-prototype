package com.verymmog.nioengine.register;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class RegisterConnect extends Register<SocketChannel> {
    private InetSocketAddress address;

    public RegisterConnect(SocketChannel channel, InetSocketAddress address) {
        super(channel);
        this.address = address;
    }

    @Override
    public SocketChannel execute(Selector selector) throws IOException {
        SocketChannel channel = getChannel();
        System.out.println(address);
        channel.connect(address);
        channel.register(selector, SelectionKey.OP_CONNECT);

        return channel;
    }
}
