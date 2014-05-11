package com.verymmog.nioengine.event.events.data;

import com.verymmog.nioengine.EngineInterface;

import java.nio.channels.SelectableChannel;

public class ChannelEventData<T extends SelectableChannel> extends EngineEventData {
    private T channel;

    public ChannelEventData(EngineInterface engine, T channel) {
        super(engine);
        this.channel = channel;
    }

    public T getChannel() {
        return channel;
    }
}
