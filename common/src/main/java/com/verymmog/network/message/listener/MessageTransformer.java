package com.verymmog.network.message.listener;

import com.verymmog.network.message.event.ReceiveMessageEvent;
import com.verymmog.network.message.event.ReceiveMessageEventData;
import com.verymmog.network.message.messages.MessageInterface;
import com.verymmog.nioengine.event.EventListener;
import com.verymmog.nioengine.event.EventProvider;
import com.verymmog.nioengine.event.events.data.ReceiveEventData;
import com.verymmog.util.ObjectSerializer;

public class MessageTransformer implements EventListener<ReceiveEventData> {
    @Override
    public void listen(ReceiveEventData event) {
        MessageInterface message = ObjectSerializer.toObject(event.getData());
        if (message != null)
            event.getCurrentDispatcher().dispatch(
                    EventProvider.provider().get(ReceiveMessageEvent.class),
                    new ReceiveMessageEventData(event.getEngine(), event.getChannel(), message)
            );
    }
}
