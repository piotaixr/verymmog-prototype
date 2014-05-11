package com.verymmog.nioengine.event;

public interface EventSubscriberInterface {

    public void register(EventDispatcherInterface dispatcher);

    public void unregister(EventDispatcherInterface dispatcher);
}
