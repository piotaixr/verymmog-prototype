package com.verymmog.nioengine.register;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;

public abstract class Register<ChannelClass extends SelectableChannel> {
    private ChannelClass channel;

    protected Register(ChannelClass channel) {
        this.channel = channel;
    }

    public ChannelClass getChannel() {
        return channel;
    }

    public abstract SelectableChannel execute(Selector selector) throws IOException;
}
