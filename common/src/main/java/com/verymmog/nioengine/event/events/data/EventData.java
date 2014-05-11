package com.verymmog.nioengine.event.events.data;

import com.verymmog.nioengine.event.EventDispatcher;

public class EventData implements EventDataInterface {

    private boolean propagationStopped = false;

    private EventDispatcher currentDispatcher;

    public EventDispatcher getCurrentDispatcher() {
        return currentDispatcher;
    }

    public void setCurrentDispatcher(EventDispatcher currentDispatcher) {
        this.currentDispatcher = currentDispatcher;
    }

    @Override
    public void stopPropagation() {
        propagationStopped = true;
    }

    @Override
    public boolean isPropagationStopped() {
        return propagationStopped;
    }
}
