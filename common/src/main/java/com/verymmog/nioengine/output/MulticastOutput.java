package com.verymmog.nioengine.output;

import java.nio.channels.SelectableChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MulticastOutput<T> implements OutputInterface<T> {

    private Map<SelectableChannel, OutputInterface<T>> delegates = Collections.synchronizedMap(new HashMap<SelectableChannel, OutputInterface<T>>());

    public MulticastOutput<T> addReceiver(SelectableChannel channel, OutputInterface<T> receiver) {
        delegates.put(channel, receiver);

        return this;
    }

    public MulticastOutput<T> removeReceiver(SelectableChannel channel) {
        delegates.remove(channel);

        return this;
    }

    @Override
    public OutputInterface<T> send(T data) {
        synchronized (delegates) {
            for (OutputInterface<T> d : delegates.values()) {
                d.send(data);
            }
        }

        return this;
    }
}
