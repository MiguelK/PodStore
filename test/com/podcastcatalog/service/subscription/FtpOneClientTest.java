package com.podcastcatalog.service.subscription;

import com.podcastcatalog.model.subscription.Subscription;
import com.podcastcatalog.model.subscription.SubscriptionData;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

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
        Set<String> countSubscribers = new HashSet<>();

        String deviceToken = "cEUOM74SbEMDrw7gL5YXwk:APA91bG7BJ0Wtnh3ebToxZnE3_9nEFeZjI9jLx07AJSs2MwhF3Pf7T93XixPJWZV9B1KxTCzJxx2nGb2ukw3HmOm34IhGRMruxf2RDp_NKR7dP3LF22_2uvhsb6HsPdagOzzht07ITlN";
        for (Subscription subscription : subscriptionData.getSubscriptions()) {

            if(subscription.getFeedURL() != null) {
                countFeeds++;
                //System.out.println("FeedURL=" + subscription.getFeedURL() + " contentID=" + subscription.getPodCastId() + " subscribbers=" + subscription.getSubscribers().size());
            } else {
             //   System.out.println(subscription.getFeedURL() + " " + subscription.getPodCastId() + " subs=" + subscription.getSubscribers().size());
            }
            for (String s : subscription.getSubscribers()) {
                countSubscribers.add(s);
             //   System.out.println("TOKEN: " + s);
               // System.out.println("FeedURL URL=" + subscription.getFeedURL());
                if(s.equals(deviceToken)) {
                    System.out.println("deviceToken found: " + subscription.getPodCastId() + ", latestId=" + subscription.getLatestPodCastEpisodeId());
                }
            }

        }

        System.out.println("Subscriptions=" + subscriptionData.getSubscriptions().size());

        System.out.println("countFeeds=" + countFeeds);
        System.out.println("countSubscribers=" + countSubscribers.size());


    }

}