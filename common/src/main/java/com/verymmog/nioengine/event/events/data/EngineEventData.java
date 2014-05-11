package com.verymmog.nioengine.event.events.data;

import com.verymmog.nioengine.EngineInterface;

public class EngineEventData extends EventData {

    private EngineInterface engine;

    public EngineEventData(EngineInterface engine) {
        this.engine = engine;
    }

    public EngineInterface getEngine() {
        return engine;
    }
}