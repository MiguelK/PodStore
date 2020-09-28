package com.podcastcatalog.service.subscription;

import com.podcastcatalog.model.subscription.Subscription;
import com.podcastcatalog.model.subscription.SubscriptionData;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by miguelkrantz on 2020-09-15.
 */
public class FtpOneClientTest {
    @Test
    public void testLoadSubscribers() throws Exception {

        SubscriptionData subscriptionData =
                FtpOneClient.getInstance().loadSubscribers();


        System.out.println("Subscriptions=" + subscriptionData.getSubscriptions().size());
        String deviceToken = "donBlQVnFkYbgQr__SBVUq:APA91bETwGHfPvwP7ZQamaZXdupix_JzfVNFKrJahP5Sj6JFz2dVx2glyXIx5xhFCEsPZ35VkgPJk-epfV0O1LnZpAcKHASpE69C6xItRGWOA9SSF-MOuVO3srFI2fvbfK-hKiZw1LiM";
        for (Subscription subscription : subscriptionData.getSubscriptions()) {
            for (String s : subscription.getSubscribers()) {
             //   System.out.println("TOKEN: " + s);
               // System.out.println("FeedURL URL=" + subscription.getFeedURL());
                if(s.equals(deviceToken)) {
                    System.out.println("SUB: " + subscription.getPodCastId() + ", latestId=" + subscription.getLatestPodCastEpisodeId());
                }
            }

        }


    }

}