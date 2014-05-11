package com.verymmog.nioengine;

import java.nio.channels.SelectableChannel;

/**
 * A DataProvider manages the buffers of data sent to each channel of the engine.
 */
public interface DataProviderInterface {
    /**
     * Adds a data to the queue for the given channel
     *
     * @param channel The destination channel
     * @param data    The data to send
     */
    public void addData(SelectableChannel channel, byte[] data);

    /**
     * Return the next data to send for the given channel
     *
     * @param channel The channel
     * @return The next data for the given channel
     */
    public byte[] nextData(SelectableChannel channel);

    /**
     * Clears the buffer for the given channel
     *
     * @param channel The channel
     */
    public void clearDataFor(SelectableChannel channel);
}
