package com.verymmog.nioengine;

import java.nio.channels.SelectableChannel;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class AsyncEngineBuffer implements DataProviderInterface {

    private Map<SelectableChannel, Deque<byte[]>> buffers = new ConcurrentHashMap<>();

    @Override
    public synchronized byte[] nextData(SelectableChannel channel) {
        Deque<byte[]> buffer = getBuffer(channel);

        if (buffer.isEmpty()) {
            return null;
        } else {
            return buffer.pollFirst();
        }
    }

    @Override
    public void clearDataFor(SelectableChannel channel) {
        buffers.remove(channel);
    }

    @Override
    public synchronized void addData(SelectableChannel channel, byte[] data) {
        getBuffer(channel).addLast(data);
    }

    /**
     * Returns (create if non existent) the buffer for the given channel.
     *
     * @param channel
     * @return
     */
    private Deque<byte[]> getBuffer(SelectableChannel channel) {
        Deque<byte[]> ret = buffers.get(channel);

        if (ret == null) {
            ret = new ConcurrentLinkedDeque<>();
            buffers.put(channel, ret);
        }

        return ret;
    }

}
