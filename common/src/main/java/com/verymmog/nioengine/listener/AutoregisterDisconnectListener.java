package com.verymmog.nioengine.listener;

import com.verymmog.nioengine.event.EventListener;
import com.verymmog.nioengine.event.EventProvider;
import com.verymmog.nioengine.event.events.DisconnectEvent;
import com.verymmog.nioengine.event.events.data.DisconnectEventData;
import com.verymmog.nioengine.event.events.data.NewConnectionEventData;

import java.nio.channels.SelectableChannel;

public abstract class AutoregisterDisconnectListener implements EventListener<NewConnectionEventData> {

    private EventListener<DisconnectEventData> listener = new EventListener<DisconnectEventData>() {
        @Override
        public void listen(DisconnectEventData event) {
            AutoregisterDisconnectListener.this.listen(event);
        }
    };

    @Override
    public void listen(NewConnectionEventData event) {
        event.getCurrentDispatcher()
             .register((SelectableChannel) event.getNewChannel(),
                     EventProvider.provider().get(DisconnectEvent.class),
                     listener);
    }


    public EventListener<DisconnectEventData> getListener() {

        return listener;
    }

    protected abstract void listen(DisconnectEventData event);
}
