package com.verymmog.nioengine.event;

import com.verymmog.nioengine.event.events.Event;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EventProvider {

    private Map<Class, Event> events;

    public EventProvider() {
        events = Collections.synchronizedMap(new HashMap<Class, Event>());
    }

    private static EventProvider provider = null;

    public synchronized static EventProvider provider() {
        if (provider == null) {
            provider = new EventProvider();
        }

        return provider;
    }

    public synchronized <EventClass extends Event> EventClass get(Class<EventClass> cl) {
        try {
            Event e = events.get(cl);
            if (e == null) {
                e = cl.getConstructor().newInstance();
                events.put(cl, e);
            }

            return (EventClass) e;

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassCastException e) {

            e.printStackTrace();
            throw new RuntimeException("On devrait pas arriver la!", e);
        }
    }
}
