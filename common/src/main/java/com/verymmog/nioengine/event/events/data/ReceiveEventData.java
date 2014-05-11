package com.verymmog.nioengine.event.events.data;

import com.verymmog.nioengine.EngineInterface;

import java.nio.channels.SocketChannel;

public class ReceiveEventData extends ChannelEventData<SocketChannel> {

    private byte[] data;

    public ReceiveEventData(EngineInterface engine, SocketChannel channel, byte[] data) {
        super(engine, channel);
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
