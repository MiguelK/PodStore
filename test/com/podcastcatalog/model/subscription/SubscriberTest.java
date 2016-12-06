package com.podcastcatalog.model.subscription;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SubscriberTest {

    @Test
    public void equal_subscriber() {
        Assert.assertEquals(new Subscriber("3333"), new Subscriber("3333"));
        Assert.assertEquals(new Subscriber("3333 "), new Subscriber("3333"));
        Assert.assertEquals(new Subscriber(" 3333"), new Subscriber(" 3333 "));
    }

    @Test
    public void not_equal_subscriber() {
        Assert.assertNotEquals(new Subscriber("x3333"), new Subscriber("3333"));
        Assert.assertNotEquals(new Subscriber("443333 "), new Subscriber("3333"));
        Assert.assertNotEquals(new Subscriber(" 3333"), new Subscriber(" 43333 "));
    }

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
    public void removeSubscription() {
        Subscriber subscriber = new Subscriber(" 1234 ");
        subscriber.addSubscription(new Subscription("3333"));

        Assert.assertTrue(subscriber.getSubscriptions().size()==1);
        subscriber.removeSubscription(new Subscription("3333"));
        Assert.assertTrue(subscriber.getSubscriptions().isEmpty());
    }

}