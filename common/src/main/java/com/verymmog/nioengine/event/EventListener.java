package com.verymmog.nioengine.event;

import com.verymmog.nioengine.event.events.data.EventDataInterface;

public interface EventListener<EventDataClass extends EventDataInterface> {

    public void listen(EventDataClass event);
}