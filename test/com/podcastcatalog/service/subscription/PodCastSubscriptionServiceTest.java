package com.podcastcatalog.service.subscription;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.subscription.Subscriber;
import com.podcastcatalog.model.subscription.Subscription;
import com.podcastcatalog.model.subscription.SubscriptionData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

public class PodCastSubscriptionServiceTest {
    @Test
    public void testLoadSubscribers() throws Exception {
    }

    private static final String VALID_SUBSCRIBER = "123-valid-DeviceToken";

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        //PodCastSubscriptionService.getInstance().start();
      //  PodCastSubscriptionService.getInstance().registerSubscriber(VALID_SUBSCRIBER);
    }

    @Test
    public void loadSubscribers()  {

        try {
            SubscriptionData subscriptionData = PodCastSubscriptionService.getInstance().loadSubscribers();
            Assert.assertNotNull(subscriptionData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void tearDown() throws Exception {
        PodCastSubscriptionService.getInstance().deleteSubscriber(VALID_SUBSCRIBER);
    }



    /*@Test(groups = TestUtil.SLOW_TEST)
    public void subscribe_success() {
        String deviceToken = "123";
        PodCastSubscriptionService.getInstance().registerSubscriber(deviceToken);
        PodCastSubscriptionService.getInstance().subscribe(deviceToken, "999");
        PodCastSubscriptionService.getInstance().subscribe(deviceToken, " 999");
        PodCastSubscriptionService.getInstance().subscribe(" 123 ", " 999 ");

        Subscriber subscriber = PodCastSubscriptionService.getInstance().getSubscriber(deviceToken);
        Assert.assertNotNull(subscriber);
        Subscription subscription = subscriber.getSubscriptions().get(0);
        Assert.assertEquals(subscription.getPodCastId(), "999");

        PodCastSubscriptionService.getInstance().unSubscribe("123", "99");
    }*/

    /*@Test(groups = TestUtil.SLOW_TEST)
    public void subscribe_unsubscribe() {
        String deviceToken = "123";
        PodCastSubscriptionService.getInstance().registerSubscriber(deviceToken);
        String contentId1 = "999";
        PodCastSubscriptionService.getInstance().subscribe(deviceToken, contentId1);

        Assert.assertTrue(PodCastSubscriptionService.getInstance().getSubscriptions().size() == 1);

        PodCastSubscriptionService.getInstance().unSubscribe(deviceToken, contentId1);

        Assert.assertTrue(PodCastSubscriptionService.getInstance().getSubscriptions().isEmpty());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void getStatusAsHTLM() {
        PodCastSubscriptionService.getInstance().registerSubscriber("7777");
        PodCastSubscriptionService.getInstance().registerSubscriber("8888");

        String html = PodCastSubscriptionService.getInstance().getStatusAsHTLM();

        Document parse = Jsoup.parse(html);

        Assert.assertNotNull(parse);
    }*/
}