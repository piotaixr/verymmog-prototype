package com.verymmog.nioengine.exception;

import java.nio.channels.SelectableChannel;

public class ChannelNotRegisteredException extends SelectableChannelException {

    public ChannelNotRegisteredException(SelectableChannel channel) {
        super("Channel not registered", channel);
    }
}
