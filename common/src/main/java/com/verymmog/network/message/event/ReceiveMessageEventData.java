package com.verymmog.network.message.event;

import com.verymmog.network.message.messages.MessageInterface;
import com.verymmog.nioengine.EngineInterface;
import com.verymmog.nioengine.event.events.data.ChannelEventData;

import java.nio.channels.SocketChannel;

public class ReceiveMessageEventData extends ChannelEventData<SocketChannel> {

    private MessageInterface message;

    public ReceiveMessageEventData(EngineInterface engine, SocketChannel channel, MessageInterface message) {
        super(engine, channel);
        this.message = message;
    }

    public MessageInterface getMessage() {
        return message;
    }
}
