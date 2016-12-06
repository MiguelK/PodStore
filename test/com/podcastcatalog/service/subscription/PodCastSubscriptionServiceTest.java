package com.podcastcatalog.service.subscription;

import com.podcastcatalog.model.subscription.Subscriber;
import com.podcastcatalog.model.subscription.Subscription;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PodCastSubscriptionServiceTest {

    private static final String VALID_SUBSCRIBER = "123-valid-DeviceToken";

    @BeforeMethod
    public void setUp() {
        PodCastSubscriptionService.getInstance().loadFromDiskAsync(null);
        PodCastSubscriptionService.getInstance().registerSubscriber(VALID_SUBSCRIBER);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        PodCastSubscriptionService.getInstance().deleteSubscriber(VALID_SUBSCRIBER);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void subscribe_invalid_content_null() {
        PodCastSubscriptionService.getInstance().subscribe(VALID_SUBSCRIBER, null, contentId -> false);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void subscribe_invalid_content_empty() {
        PodCastSubscriptionService.getInstance().subscribe(VALID_SUBSCRIBER, "", contentId -> false);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void subscribe_invalid_content_validator_false() {
        PodCastSubscriptionService.getInstance().subscribe(VALID_SUBSCRIBER, "1234", contentId -> false);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void subscribe_not_registered() {
        PodCastSubscriptionService.getInstance().subscribe("123dsdsd", "dfjkdf", contentId -> true);
    }

    @Test
    public void deleteSubscriber_success() {
        String deviceToken = "123dsdsd";
        PodCastSubscriptionService.getInstance().registerSubscriber(deviceToken);
        Assert.assertNotNull(PodCastSubscriptionService.getInstance().getSubscriber(deviceToken));

        PodCastSubscriptionService.getInstance().deleteSubscriber(deviceToken);
        Assert.assertNull(PodCastSubscriptionService.getInstance().getSubscriber(deviceToken));

    }

    @Test
    public void getSubscriber_success() {
        PodCastSubscriptionService.getInstance().registerSubscriber("123");
        Assert.assertNotNull(PodCastSubscriptionService.getInstance().getSubscriber(" 123 "));
    }

    @Test
    public void subscribe_success() {
        String deviceToken = "123";
        PodCastSubscriptionService.getInstance().registerSubscriber(deviceToken);
        PodCastSubscriptionService.getInstance().subscribe(deviceToken, "999", contentId -> true);
        PodCastSubscriptionService.getInstance().subscribe(deviceToken, " 999", contentId -> true);
        PodCastSubscriptionService.getInstance().subscribe(" 123 ", " 999 ", contentId -> true);

        Subscriber subscriber = PodCastSubscriptionService.getInstance().getSubscriber(deviceToken);
        Assert.assertNotNull(subscriber);
        Subscription subscription = subscriber.getSubscriptions().get(0);
        Assert.assertEquals(subscription.getContentId(), "999");

        PodCastSubscriptionService.getInstance().unSubscribe("123", "99");
    }

    @Test
    public void subscribe_unsubscribe() {
        String deviceToken = "123";
        PodCastSubscriptionService.getInstance().registerSubscriber(deviceToken);
        String contentId1 = "999";
        PodCastSubscriptionService.getInstance().subscribe(deviceToken, contentId1, contentId -> true);

        Assert.assertTrue(PodCastSubscriptionService.getInstance().getSubscriptions().size() == 1);

        PodCastSubscriptionService.getInstance().unSubscribe(deviceToken, contentId1);

        Assert.assertTrue(PodCastSubscriptionService.getInstance().getSubscriptions().isEmpty());
    }

    @Test
    public void getStatusAsHTLM() {
        PodCastSubscriptionService.getInstance().registerSubscriber("7777");
        PodCastSubscriptionService.getInstance().registerSubscriber("8888");

        String html = PodCastSubscriptionService.getInstance().getStatusAsHTLM();

        Document parse = Jsoup.parse(html);

        Assert.assertNotNull(parse);
        System.out.println(html);


    }
}