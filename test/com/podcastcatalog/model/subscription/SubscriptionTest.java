package com.podcastcatalog.model.subscription;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SubscriptionTest {
    @Test
    public void equal_subscription() {
        Assert.assertEquals(new Subscription("2222"), new Subscription(" 2222"));
        Assert.assertEquals(new Subscription(" 2222"), new Subscription(" 2222 "));
    }

    @Test
    public void not_equal_subscription() {
        Assert.assertNotEquals(new Subscription("2222"), new Subscription(" 22226"));
        Assert.assertNotEquals(new Subscription(" 2222"), new Subscription(" 222266 "));
    }

    @Test
    public void hasSubscribers_false() {
        Subscription  s1 = new Subscription("444");
        Assert.assertTrue(s1.hasNoSubscribers());
    }

    @Test
    public void hasSubscribers_true() {
        Subscription  s1 = new Subscription("444");
        s1.addSubscriber(new Subscriber("888"));
        Assert.assertFalse(s1.hasNoSubscribers());
    }

    @Test
    public void hasSubscribers_true_remove() {
        Subscription  s1 = new Subscription("444");
        s1.addSubscriber(new Subscriber("888"));
        s1.addSubscriber(new Subscriber("9999"));
        s1.removeSubscriber(new Subscriber("888"));
        Assert.assertFalse(s1.hasNoSubscribers());
        Assert.assertTrue(s1.getSubscribers().size()==1);
    }

    @Test
    public void testAddSubscriber() {

    }

    @Test
    public void testGetLatestPushedDateTime() {

    }

    @Test
    public void testIsNotAlreadyPushed() {

    }

    @Test
    public void testSetLatestPushedDateTime() {

    }

    @Test
    public void testHasSentPushMessage() {

    }

}