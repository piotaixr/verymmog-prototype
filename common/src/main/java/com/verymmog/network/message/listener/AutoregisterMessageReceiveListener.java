package com.verymmog.network.message.listener;

import com.verymmog.network.message.event.ReceiveMessageEvent;
import com.verymmog.network.message.event.ReceiveMessageEventData;
import com.verymmog.nioengine.event.EventListener;
import com.verymmog.nioengine.event.EventProvider;
import com.verymmog.nioengine.event.events.data.NewConnectionEventData;

public abstract class AutoregisterMessageReceiveListener implements EventListener<NewConnectionEventData> {

    private EventListener<ReceiveMessageEventData> listener = new EventListener<ReceiveMessageEventData>() {
        @Override
        public void listen(ReceiveMessageEventData event) {
            AutoregisterMessageReceiveListener.this.listen(event);
        }
    };

    @Override
    public void listen(NewConnectionEventData event) {
        event.getCurrentDispatcher().register(
                event.getNewChannel(),
                EventProvider.provider().get(ReceiveMessageEvent.class),
                listener
        );
    }

    protected abstract void listen(ReceiveMessageEventData event);

    public EventListener<ReceiveMessageEventData> getListener() {
        return listener;
    }
}
