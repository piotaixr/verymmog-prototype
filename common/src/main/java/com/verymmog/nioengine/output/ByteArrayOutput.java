package com.verymmog.nioengine.output;

import com.verymmog.nioengine.EngineInterface;

import java.nio.channels.SelectableChannel;

public class ByteArrayOutput extends BaseOutput<byte[]> {
    public ByteArrayOutput(EngineInterface engine, SelectableChannel channel) {
        super(engine, channel);
    }

    @Override
    public ByteArrayOutput send(byte[] data) {
        getEngine().send(getChannel(), data);

        return this;
    }


}
