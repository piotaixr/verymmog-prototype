package com.verymmog.nioengine.event.events.data;

import com.verymmog.nioengine.EngineInterface;

import java.nio.channels.SelectableChannel;

public class DisconnectEventData extends ChannelEventData<SelectableChannel> {
    public DisconnectEventData(EngineInterface engine, SelectableChannel channel) {
        super(engine, channel);
    }
}
