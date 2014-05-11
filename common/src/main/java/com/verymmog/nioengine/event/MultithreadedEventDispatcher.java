package com.verymmog.nioengine.event;

import com.google.common.cache.Cache;
import com.verymmog.util.MessageQueue;
import com.verymmog.nioengine.event.events.Event;
import com.verymmog.nioengine.event.events.data.ChannelEventData;
import com.verymmog.nioengine.event.events.data.DisconnectEventData;
import com.verymmog.nioengine.event.events.data.EventDataInterface;
import com.verymmog.util.Pair;

import java.nio.channels.SelectableChannel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class MultithreadedEventDispatcher implements EventDispatcherInterface, EventListener<DisconnectEventData> {
    @Override
    public void listen(DisconnectEventData event) {
        synchronized (this) {

        }
    }

    private class PoolMember extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Pair<SelectableChannel, Pair<Event, ChannelEventData>> d = eventsQueue.take();

                    ed.dispatch(d.second.first, d.second.second);

                    eventsQueue.endWork(d.first);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Set<PoolMember> pool = new HashSet<>();
    private MessageQueue<SelectableChannel, Pair<Event, ChannelEventData>> eventsQueue = new MessageQueue<>();
    private EventDispatcher ed = new EventDispatcher();

    public MultithreadedEventDispatcher(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            PoolMember pm = new PoolMember();
            pool.add(pm);
            pm.start();
        }
    }

    @Override
    public <CC extends SelectableChannel> Cache<Event, List<EventListener>> getListenersFor(CC channel) throws ExecutionException {
        return ed.getListenersFor(channel);
    }

    @Override
    public <CC extends SelectableChannel, E extends Event<ED>, ED extends ChannelEventData<CC>> EventDispatcherInterface register(CC chan, E event, EventListener<ED>... listeners) {
        synchronized (this) {
            ed.register(chan, event, listeners);
        }
        return this;
    }

    @Override
    public <E extends Event<ED>, ED extends EventDataInterface> EventDispatcherInterface register(E event, EventListener<ED> listener) {
        ed.register(event, listener);

        return this;
    }

    public <E extends Event<ED>, ED extends ChannelEventData<? extends SelectableChannel>> void dispatch(E event, ED eventData) {
        eventsQueue.add(eventData.getChannel(), new Pair<Event, ChannelEventData>(event, eventData));
    }

    @Override
    public EventDispatcherInterface unregister(EventListener... listeners) {
        synchronized (this) {
            ed.unregister(listeners);
        }
        return this;
    }

}