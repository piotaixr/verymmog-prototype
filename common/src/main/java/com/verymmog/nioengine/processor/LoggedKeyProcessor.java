package com.verymmog.nioengine.processor;

import com.verymmog.nioengine.BufferStatus;
import com.verymmog.nioengine.exception.SelectableChannelException;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class LoggedKeyProcessor extends KeyProcessor {

    @Override
    protected int handleRead(SocketChannel channel, BufferStatus bs) {
        try {
            int read = super.handleRead(channel, bs);

            logRead(channel, read);

            return read;
        } catch (SelectableChannelException ex) {
            logException(channel, ex);
            throw ex;
        }
    }

    protected abstract void logRead(SocketChannel channel, int read);

    @Override
    protected int handleWrite(SocketChannel channel, ByteBuffer outBuffer) {
        try {

            int written = super.handleWrite(channel, outBuffer);

            logWrite(channel, written);

            return written;
        } catch (SelectableChannelException ex) {
            logException(channel, ex);
            throw ex;
        }
    }

    protected abstract void logException(SocketChannel channel, SelectableChannelException ex);

    protected abstract void logWrite(SocketChannel channel, int written);
}
