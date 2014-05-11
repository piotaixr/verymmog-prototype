package com.verymmog.nioengine.listener;

import com.verymmog.nioengine.event.EventListener;
import com.verymmog.nioengine.event.events.Event;
import com.verymmog.nioengine.event.events.data.ChannelEventData;
import com.verymmog.nioengine.event.events.data.NewConnectionEventData;

@Deprecated
public class AutoRegisterListener<E extends Event<ED>, ED extends ChannelEventData> implements EventListener<NewConnectionEventData> {
    private E e;
    private EventListener<ED> l;

    public AutoRegisterListener(E e, EventListener<ED> l) {
        this.e = e;
        this.l = l;
    }

    @Override
    public void listen(NewConnectionEventData event) {
        event.getCurrentDispatcher().register(event.getNewChannel(), e, l);
    }

    public E getE() {
        return e;
    }

    public EventListener<ED> getL() {
        return l;
    }
}
