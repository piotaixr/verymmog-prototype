package com.verymmog.nioengine.exception;

import java.nio.channels.SelectableChannel;

public class SelectableChannelException extends RuntimeException {

    private SelectableChannel channel;

    public SelectableChannelException(SelectableChannel channel) {
        this("Channel Error: " + channel, channel);
    }

    public SelectableChannelException(String message, SelectableChannel channel) {
        super(message);
        this.channel = channel;
    }

    public SelectableChannel getChannel() {
        return channel;
    }
}
