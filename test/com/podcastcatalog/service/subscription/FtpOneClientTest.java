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


        int countFeeds = 0;

        System.out.println("Subscriptions=" + subscriptionData.getSubscriptions().size());
        String deviceToken = "donBlQVnFkYbgQr__SBVUq:APA91bETwGHfPvwP7ZQamaZXdupix_JzfVNFKrJahP5Sj6JFz2dVx2glyXIx5xhFCEsPZ35VkgPJk-epfV0O1LnZpAcKHASpE69C6xItRGWOA9SSF-MOuVO3srFI2fvbfK-hKiZw1LiM";
        for (Subscription subscription : subscriptionData.getSubscriptions()) {

            if(subscription.getFeedURL() != null) {
                countFeeds++;
                //System.out.println("FeedURL=" + subscription.getFeedURL() + " contentID=" + subscription.getPodCastId() + " subscribbers=" + subscription.getSubscribers().size());
            } else {
                System.out.println(subscription.getFeedURL() + " " + subscription.getPodCastId() + " subs=" + subscription.getSubscribers().size());
            }
            for (String s : subscription.getSubscribers()) {
             //   System.out.println("TOKEN: " + s);
               // System.out.println("FeedURL URL=" + subscription.getFeedURL());
                if(s.equals(deviceToken)) {
                    System.out.println("SUB: " + subscription.getPodCastId() + ", latestId=" + subscription.getLatestPodCastEpisodeId());
                }
            }

        }

        System.out.println("countFeeds=" + countFeeds);


    }

}