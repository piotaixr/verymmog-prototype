package com.verymmog.network.message.listener;

import com.verymmog.nioengine.event.EventProvider;
import com.verymmog.nioengine.event.events.ReceiveEvent;
import com.verymmog.nioengine.event.events.data.ReceiveEventData;
import com.verymmog.nioengine.listener.AutoRegisterListener;

public class AutoregisterMessageTransformer extends AutoRegisterListener<ReceiveEvent, ReceiveEventData> {
    public AutoregisterMessageTransformer(MessageTransformer transformer) {
        super(EventProvider.provider().get(ReceiveEvent.class), new MessageTransformer());

        this.transformer = transformer;
    }

    private MessageTransformer transformer;

    public MessageTransformer getTransformer() {
        return transformer;
    }
}

