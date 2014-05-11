package com.verymmog.nioengine;

import com.verymmog.nioengine.event.EventDispatcher;
import com.verymmog.nioengine.event.EventDispatcherInterface;
import com.verymmog.nioengine.event.EventProvider;
import com.verymmog.nioengine.event.events.DisconnectEvent;

import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;

/**
 * Base implementation of an asynchronous engine using NIO.
 * This class has to be subclassed to implement the loginc behind clients/channels management and behind the main loop.
 * <p/>
 * For the engine to boot successfully, a Dataprovider {@see DataProviderInterface} must be passed to it (by the engine's constructor or by using the assiciated setter)
 * The base behavior can be customized by the same way by tweaking the EventDispatcher used.
 */
public abstract class BaseAsyncEngine extends ThreadedEngine {

    /**
     * The NIO selector
     */
    private Selector selector;
    /**
     * The event dispatcher used by the engine to dispatch events
     */
    private EventDispatcherInterface eventDispatcher = null;
    /**
     * The data provider used
     */
    private DataProviderInterface dataProvider = null;
    /**
     * The data provider clearer used to avoid memory leaks in the data provider
     */
    private DataProviderCleaner clearer = null;

    protected BaseAsyncEngine(Selector selector) {
        this.selector = selector;
    }

    protected BaseAsyncEngine(Selector selector,
                              EventDispatcherInterface eventDispatcher,
                              DataProviderInterface dataProvider) {
        this(selector);
        setEventDispatcher(eventDispatcher);
        setDataProvider(dataProvider);
    }

    protected Selector getSelector() {
        return selector;
    }

    @Override
    public void start() {
        if (getEventDispatcher() == null) {
            setEventDispatcher(new EventDispatcher());
        }
        if (getDataProvider() == null) {
            //TODO: implementer un dataprovider de base
            throw new RuntimeException("DataProvider not set");
        } else {
            clearer = new DataProviderCleaner(getDataProvider());
            getEventDispatcher().register(EventProvider.provider().get(DisconnectEvent.class), clearer);
        }

        super.start();
    }

    @Override
    protected void shutdown() {
        super.shutdown();

        if (clearer != null) {
            getEventDispatcher().unregister(clearer);
            clearer = null;
        }
    }

    /**
     * Simple call to the dataProvider to register the data to send
     *
     * @param channel The destination channel
     * @param data    The data to send
     */
    @Override
    public void send(SelectableChannel channel, byte[] data) {
        getDataProvider().addData(channel, data);
    }

    @Override
    public EventDispatcherInterface getEventDispatcher() {
        return eventDispatcher;
    }

    /**
     * Set the event dispatcher, can only be called if the engine is not currently running
     *
     * @param eventDispatcher The event dispatcher that will be used by the engine.
     */
    public void setEventDispatcher(EventDispatcherInterface eventDispatcher) {
        if (!isRunning()) {
            this.eventDispatcher = eventDispatcher;
        }
    }

    public DataProviderInterface getDataProvider() {
        return dataProvider;
    }

    public synchronized void setDataProvider(DataProviderInterface dataProvider) {
        if (getDataProvider() == null) {
            this.dataProvider = dataProvider;
        }
    }


}
