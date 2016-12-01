package com.podcastcatalog.model.subscription;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SubscriberTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_DeviceToken_null() {
        Subscriber subscriber = new Subscriber(null);
        Assert.fail("token should be invalid " + subscriber);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_DeviceToken_empty() {
        Subscriber subscriber = new Subscriber(" ");
        Assert.fail("token should be invalid " + subscriber);
    }

    @Test
    public void trim_deviceToken() {
        Subscriber subscriber = new Subscriber(" 1234 ");
        Assert.assertEquals(subscriber.getDeviceToken(),"1234");
    }

    @Test
    public void testAddSubscription() {

    }

}