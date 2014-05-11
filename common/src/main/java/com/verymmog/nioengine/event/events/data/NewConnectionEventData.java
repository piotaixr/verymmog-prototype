package com.verymmog.nioengine.event.events.data;

import com.verymmog.nioengine.EngineInterface;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NewConnectionEventData extends ChannelEventData<ServerSocketChannel> {
    private SocketChannel newChannel;

    public NewConnectionEventData(EngineInterface engine, ServerSocketChannel channel, SocketChannel newChannel) {
        super(engine, channel);
        this.newChannel = newChannel;
    }

    public SocketChannel getNewChannel() {
        return newChannel;
    }
}
