package com.verymmog.nioengine;

import com.verymmog.nioengine.event.EventListener;
import com.verymmog.nioengine.event.events.data.DisconnectEventData;

/**
 * Autocleans a dataprovider when a channel is reported to be disconnected by the engine.
 */
public class DataProviderCleaner implements EventListener<DisconnectEventData> {

    /**
     * The dataProvider to clean
     */
    private DataProviderInterface dataProvider;

    /**
     * @param dataProvider The dataProvider to clean
     */
    public DataProviderCleaner(DataProviderInterface dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public void listen(DisconnectEventData event) {
        dataProvider.clearDataFor(event.getChannel());
    }
}
