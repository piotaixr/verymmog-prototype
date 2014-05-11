package com.verymmog.nioengine;

import java.nio.ByteBuffer;

/**
 * DataObject allowing to have in a single variable the buffer and its status (receiving data or datasize)
 */
public class BufferStatus {
    /**
     * The buffer
     */
    public ByteBuffer buffer;
    /**
     * The buffer's status
     */
    public RecvStatus status;

    public BufferStatus() {
        buffer = ByteBuffer.allocate(4);
        status = RecvStatus.SIZE;
    }
}
