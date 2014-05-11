package com.verymmog.util;

import java.io.*;

/**
 * Simple serializer used to serialize object using basic java Serialization.
 */
public class ObjectSerializer {

    /**
     * Serializes the given object
     *
     * @param object The object to serialize
     * @return the byte representation of the given object
     */
    public synchronized static byte[] toBytes(Serializable object) {
        ObjectOutputStream oos;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(42);
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.flush();
            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Unserializes a previously serialized object
     *
     * @param data The serialized object
     * @param <T> The class of the object
     * @return The unserialized object
     */
    public synchronized static <T extends Serializable> T toObject(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);

            if (bais.read() == 42) {
                ObjectInputStream ois = new ObjectInputStream(bais);
                return (T) ois.readObject();
            } else {
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();

            return null;
        }
    }
}
