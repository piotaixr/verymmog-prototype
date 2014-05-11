package com.verymmog.nioengine.event;

import com.verymmog.nioengine.event.events.TestEvent;
import junit.framework.Assert;
import org.junit.Test;

public class EventProviderTest {
    @Test
    public void testProvider() throws Exception {
        EventProvider p = EventProvider.provider();
        Assert.assertEquals(p, EventProvider.provider());
    }

    @Test
    public void testGet() throws Exception {
        TestEvent e = EventProvider.provider().get(TestEvent.class);
        Assert.assertNotNull(e);
        Assert.assertEquals(e, EventProvider.provider().get(TestEvent.class));
    }
}
