package com.verymmog.util;

import java.nio.ByteBuffer;

public class ByteBufferUtil {

    /**
     * Wraps data into a ByteBuffer prefixed by its length.
     * If data.length > Integer.Max, the returned data will be incoherent.
     *
     * @param data The data to wrap
     * @return The allocated ByteBuffer
     */
    public static ByteBuffer toByteBuffer(byte[] data) {
        ByteBuffer bb = ByteBuffer.allocate(4 + data.length);
        bb.putInt(data.length);
        bb.put(data);
        bb.rewind();

        return bb;
    }
}
