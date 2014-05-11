package com.verymmog.util;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Queue allowing mutual exclusion between processing messages having the same key.
 *
 * @param <I>
 * @param <T>
 */
public class MessageQueue<I, T> {

    private Map<I, Queue<T>> buf = new HashMap<>();
    private Map<I, T> working = new HashMap<>();
    private Set<I> ready = new LinkedHashSet<>();
    private Semaphore sem = new Semaphore(0);

    /**
     * Adds a message to the queue
     *
     * @param key The key to attach the message to
     * @param message The message to add
     */
    public synchronized void add(I key, T message) {
        Queue<T> q;
        if (!buf.containsKey(key)) {
            buf.put(key, new LinkedList<T>());
        }

        q = buf.get(key);

        if (doAdd(q, message)) {
            checkReady(key);
        }
    }

    protected boolean doAdd(Queue<T> q, T message) {
        try {
            q.offer(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Notify that the processing for the given gey is done.
     * Thie method has to be called in order to unblock the other messages attached to the given key.
     *
     * @param key The key
     */
    public synchronized void endWork(I key) {
        working.remove(key);
        checkReady(key);
    }

    /**
     * Called when the current work for the given key is finished.
     * This method checks if there is other message for the key and, if yes, add the first one to the ready list.
     *
     * @param key The key to verify
     */
    private void checkReady(I key) {
        if (!ready.contains(key) && !working.containsKey(key) && !buf.get(key).isEmpty()) {
            ready.add(key);
            sem.release();
        }
    }

    /**
     * Returns the first ready element to be processed.
     * The method {@see endWork} has to be called when the message has been processed.
     *
     *
     * @return A pair [Key, Message]
     * @throws InterruptedException
     */
    public Pair<I, T> take() throws InterruptedException {
        sem.acquire();

        synchronized (this) {
            I channel = ready.iterator().next();
            ready.remove(channel);

            T m = buf.get(channel).poll();

            working.put(channel, m);

            return new Pair<>(channel, m);
        }
    }

}
