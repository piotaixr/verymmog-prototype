package com.verymmog.nioengine.processor;

import com.verymmog.nioengine.NioEngine;

import java.nio.channels.SelectionKey;
import java.util.Set;

/**
 * A keyProcessor is responsible for managing NIO events (read/write/connect/accept)
 */
public interface KeyProcessorInterface {
    /**
     * Process a set of SelectionKey
     *
     * @param selectionKeys The keys to process
     */
    void processKeys(Set<SelectionKey> selectionKeys);

    /**
     * Sets the engine using this key processor
     *
     * @param engine The engine to use
     */
    void setEngine(NioEngine engine);
}
