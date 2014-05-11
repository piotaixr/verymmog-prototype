package com.verymmog.nioengine.event.events.data;

import com.verymmog.nioengine.EngineInterface;

import java.nio.channels.SocketChannel;

public class WriteReadyEventData extends ChannelEventData<SocketChannel> {
    private byte[] newData;

    public WriteReadyEventData(EngineInterface engine, SocketChannel channel) {
        super(engine, channel);
    }

    public byte[] getNewData() {
        return newData;
    }

    public void setNewData(byte[] newData) {
        this.newData = newData;
    }
}
