package com.podcastcatalog.subscribe;

import org.testng.annotations.Test;

public class PodCastSubscriptionsTest {

   /* @Test(expectedExceptions = IllegalArgumentException.class)
    public void subscribe_not_regstered() {
        PodCastSubscriptions.subscribe("123dsdsd", "dfjkdf", contentId -> true);
    }*/

    @Test
    public void subscribe_success() {
        String deviceToken = "123";
        PodCastSubscriptions.registerSubscriber(deviceToken);
        PodCastSubscriptions.subscribe(deviceToken, "999", contentId -> true);
        PodCastSubscriptions.subscribe(deviceToken, " 999", contentId -> true);
        PodCastSubscriptions.subscribe(" 123 ", " 999 ", contentId -> true);

        PodCastSubscriptions.unsubscribe("123", "99");
    }

    @Test
    public void invalid_co() {

    }

    @Test
    public void testDeleteSubscriber() {

    }

    @Test
    public void testRegisterSubscriber() {

    }

    @Test
    public void testPushMessage() {

    }

}