package com.verymmog.nioengine.event.events.data;

import com.verymmog.nioengine.event.EventDispatcher;

public interface EventDataInterface {

    public void stopPropagation();

    public boolean isPropagationStopped();

    public EventDispatcher getCurrentDispatcher();

    public void setCurrentDispatcher(EventDispatcher currentDispatcher);
}
