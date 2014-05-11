package com.verymmog.nioengine.event;

import com.google.common.cache.Cache;
import com.verymmog.nioengine.event.events.Event;
import com.verymmog.nioengine.event.events.data.ChannelEventData;
import com.verymmog.nioengine.event.events.data.EventDataInterface;

import java.nio.channels.SelectableChannel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface EventDispatcherInterface {
    <CC extends SelectableChannel> Cache<Event, List<EventListener>> getListenersFor(CC channel) throws ExecutionException;

    <CC extends SelectableChannel, E extends Event<ED>, ED extends ChannelEventData<CC>> EventDispatcherInterface register(CC chan, E event, EventListener<ED>... listeners);

    <E extends Event<ED>, ED extends EventDataInterface> EventDispatcherInterface register(E event, EventListener<ED> listener);

    <E extends Event<ED>, ED extends ChannelEventData<? extends SelectableChannel>> void dispatch(E event, ED eventData);

    public EventDispatcherInterface unregister(EventListener... listeners);
}
