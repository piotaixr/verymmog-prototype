package com.verymmog.nioengine.output;

import com.verymmog.util.ObjectSerializer;

import java.io.Serializable;

public class SerializableOutput extends DelegateOutput<Serializable, byte[]> {
    public SerializableOutput(OutputInterface<byte[]> delegate) {
        super(delegate);
    }

    @Override
    public SerializableOutput send(Serializable data) {
        byte[] d = ObjectSerializer.toBytes(data);

        getDelegate().send(d);

        return this;
    }
}
