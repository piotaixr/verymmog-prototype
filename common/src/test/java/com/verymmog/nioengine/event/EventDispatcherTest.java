package com.verymmog.nioengine.event;

import com.verymmog.nioengine.event.events.TestEvent;
import com.verymmog.nioengine.event.events.TestEventData;
import junit.framework.Assert;
import org.junit.Test;

public class EventDispatcherTest {
    @Test
    public void testDispatch() throws Exception {
        final boolean[] called = {false};
        EventDispatcher dispatcher = new EventDispatcher();
        dispatcher.register(EventProvider.provider().get(TestEvent.class), new EventListener<TestEventData>() {

            @Override
            public void listen(TestEventData event) {
                called[0] = true;
            }
        });

        TestEventData e = dispatcher.dispatchGlobal(
                EventProvider.provider().get(TestEvent.class),
                new TestEventData()
        );

        Assert.assertTrue(called[0]);
    }
}
