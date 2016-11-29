package com.podcastcatalog.subscribe;

import com.podcastcatalog.subscribe.internal.Subscriber;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PodCastSubscriptionsTest {

    private static final String VALID_SUBSCRIBER = "123-valid-DeviceToken";

    @BeforeMethod
    public void setUp() {
        PodCastSubscriptions.getInstance().registerSubscriber(VALID_SUBSCRIBER);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        PodCastSubscriptions.getInstance().deleteSubscriber(VALID_SUBSCRIBER);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void subscribe_invalid_content_null() {
        PodCastSubscriptions.getInstance().subscribe(VALID_SUBSCRIBER, null, contentId -> false);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void subscribe_invalid_content_empty() {
        PodCastSubscriptions.getInstance().subscribe(VALID_SUBSCRIBER, "", contentId -> false);
    }
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void subscribe_invalid_content_validator_false() {
        PodCastSubscriptions.getInstance().subscribe(VALID_SUBSCRIBER, "1234", contentId -> false);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void subscribe_not_registered() {
        PodCastSubscriptions.getInstance().subscribe("123dsdsd", "dfjkdf", contentId -> true);
    }

    @Test
    public void getSubscriber_success() {
        PodCastSubscriptions.getInstance().registerSubscriber("123");
        Assert.assertNotNull(PodCastSubscriptions.getInstance().getSubscriber(" 123 "));
    }

    @Test
    public void subscribe_success() {
        String deviceToken = "123";
        PodCastSubscriptions.getInstance().registerSubscriber(deviceToken);
        PodCastSubscriptions.getInstance().subscribe(deviceToken, "999", contentId -> true);
        PodCastSubscriptions.getInstance().subscribe(deviceToken, " 999", contentId -> true);
        PodCastSubscriptions.getInstance().subscribe(" 123 ", " 999 ", contentId -> true);

        Subscriber subscriber = PodCastSubscriptions.getInstance().getSubscriber(deviceToken);
        Assert.assertNotNull(subscriber);
        Subscription subscription = subscriber.getSubscriptions().get(0);
        Assert.assertEquals(subscription.getContentId(),"999");

        PodCastSubscriptions.getInstance().unSubscribe("123", "99");
    }

    @Test
    public void getStatusAsHTLM() {
        PodCastSubscriptions.getInstance().registerSubscriber("7777");
        PodCastSubscriptions.getInstance().registerSubscriber("8888");

        String html = PodCastSubscriptions.getInstance().getStatusAsHTLM();

        Document parse = Jsoup.parse(html);

        Assert.assertNotNull(parse);
        System.out.println(html);



    }
}