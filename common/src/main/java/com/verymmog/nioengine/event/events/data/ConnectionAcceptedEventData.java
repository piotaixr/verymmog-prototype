package com.verymmog.nioengine.event.events.data;

import com.verymmog.nioengine.EngineInterface;

import java.nio.channels.SocketChannel;

public class ConnectionAcceptedEventData extends ChannelEventData<SocketChannel> {

    public ConnectionAcceptedEventData(EngineInterface engine, SocketChannel channel) {
        super(engine, channel);
    }
}
