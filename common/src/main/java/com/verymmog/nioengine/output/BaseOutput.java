package com.verymmog.nioengine.output;

import com.verymmog.nioengine.EngineInterface;

import java.nio.channels.SelectableChannel;

public abstract class BaseOutput<T> implements OutputInterface<T> {

    private EngineInterface engine;
    private SelectableChannel channel;

    public BaseOutput(EngineInterface engine, SelectableChannel channel) {
        this.engine = engine;
        this.channel = channel;
    }

    public SelectableChannel getChannel() {
        return channel;
    }

    public EngineInterface getEngine() {
        return engine;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BaseOutput) {
            BaseOutput bo = (BaseOutput) obj;
            return bo.getChannel() == getChannel();
        }

        return false;
    }
}
