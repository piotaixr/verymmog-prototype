package com.verymmog.nioengine.event;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.verymmog.nioengine.event.events.Event;
import com.verymmog.nioengine.event.events.data.ChannelEventData;
import com.verymmog.nioengine.event.events.data.EventDataInterface;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class EventDispatcher implements EventDispatcherInterface {
    private Callable<List<EventListener>> listCreator = new Callable<List<EventListener>>() {
        @Override
        public List<EventListener> call() throws Exception {
            return new ArrayList<>();
        }
    };
    private Callable<Cache<Event, List<EventListener>>> cacheCreator = new Callable<Cache<Event, List<EventListener>>>() {
        @Override
        public Cache<Event, List<EventListener>> call() throws Exception {
            return CacheBuilder.newBuilder().build();
        }
    };

    private Cache<Event, List<EventListener>> globalListeners;

    private Cache<SelectableChannel, Cache<Event, List<EventListener>>> channelListeners;

    public EventDispatcher() {
        globalListeners = CacheBuilder.newBuilder().build();
        channelListeners = CacheBuilder.newBuilder().build();
    }

    @Override
    public <CC extends SelectableChannel> Cache<Event, List<EventListener>> getListenersFor(CC channel) throws ExecutionException {
        return channelListeners.get(channel, cacheCreator);
    }

    @Override
    public EventDispatcherInterface unregister(EventListener... listeners) {
        //pour chaque listener
//        lockModif.lock();
        for (EventListener listener : listeners) {

            for (SelectableChannel chan : channelListeners.asMap().keySet()) {
                Cache<Event, List<EventListener>> cl = channelListeners.getIfPresent(chan);
                if (cl != null)
                    for (List<EventListener> ls : cl.asMap().values()) {
                        ls.remove(listener);
                    }
            }

            for (List<EventListener> ls : globalListeners.asMap().values()) {
                ls.remove(listener);
            }

        }
//        lockModif.unlock();
        return this;
    }

    private <E extends Event<ED>, ED extends EventDataInterface> void addListener(Cache<Event, List<EventListener>> listeners, E event, EventListener<ED> listener) throws ExecutionException {
        List<EventListener> listenersList = listeners.get(event, listCreator);

        if (listenersList == null) {
            listenersList = new ArrayList<>();
            listeners.put(event, listenersList);
        }
        if (!listenersList.contains(listener))
            listenersList.add(listener);
    }

    @Override
    public <CC extends SelectableChannel, E extends Event<ED>, ED extends ChannelEventData<CC>> EventDispatcher register(CC chan, E event, EventListener<ED>... listeners) {
        try {
            Cache<Event, List<EventListener>> map = map = getListenersFor(chan);

            for (EventListener<ED> l : listeners)
                addListener(map, event, l);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return this;
    }

    @Override
    public <E extends Event<ED>, ED extends EventDataInterface> EventDispatcher register(E event, EventListener<ED> listener) {
        try {
            addListener(globalListeners, event, listener);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return this;
    }

    @Override
    public <E extends Event<ED>, ED extends ChannelEventData<? extends SelectableChannel>> void dispatch(E event, ED eventData) {
        try {
            doDispatch(getListenersFor(eventData.getChannel()).get(event, listCreator), eventData);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (!eventData.isPropagationStopped()) {
            dispatchGlobal(event, eventData);
        }
    }

    protected <E extends Event<ED>, ED extends EventDataInterface> ED dispatchGlobal(E event, ED eventData) {

        try {
            doDispatch(globalListeners.get(event, listCreator), eventData);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return eventData;
    }

    protected <E extends Event<ED>, ED extends EventDataInterface> void doDispatch(List<EventListener> listeners, ED eventData) {
        if (listeners == null)
            return;

        for (EventListener el : listeners.subList(0, listeners.size())) {
            EventListener<ED> eled = (EventListener<ED>) el;

            eventData.setCurrentDispatcher(this);

            try {
                eled.listen(eventData);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (eventData.isPropagationStopped())
                break;
        }
    }
}
