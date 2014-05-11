package com.verymmog.nioengine.register;

import com.verymmog.nioengine.register.Register;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;

public class BasicRegister extends Register<SelectableChannel> {
    private int keys;

    public BasicRegister(SelectableChannel channel, int keys) {
        super(channel);
        this.keys = keys;
    }

    @Override
    public SelectableChannel execute(Selector selector) throws IOException {
        getChannel().register(selector, keys);

        return getChannel();
    }
}
