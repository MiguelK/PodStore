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


      //  String deviceToken = "eGpkdigS4k79jG6po0ljwm:APA91bHvM6MAzMcrznPmhe9wbMSPI7rtV1MmBbFBapfQHlOsKffWyyWVFLc2hNG6iLwXoNHx26sVJB8EVjDSSCCG9K3nQjsgo71UGQvBJE7hP7tm6igTe1vptUMvrHj-Q2gUrXR2AOjy";
        String deviceToken = "clkw6ml-2UieiGcy_BBdiI:APA91bGYkv7iogWg31w48dwRlYRHiU8VpuMrRrJg1_DYjYk3K-V5Qco-oDqr0NFUFfIoWHyOAnlG2MA_mZVPsFGMGLn5-MgtTeqEVXukYlSlesvitwfWkzmz-3m0xth1KgraMpshz1qa";

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
                    System.out.println("deviceToken found: " + subscription.getPodCastId() + ", latestId=" + subscription.getLatestPodCastEpisodeId()
                    + ", feed=" + subscription.getFeedURL());
                }
            }

        }

        System.out.println("Subscriptions=" + subscriptionData.getSubscriptions().size());

        System.out.println("countFeeds=" + countFeeds);
        System.out.println("countSubscribers=" + countSubscribers.size());


    }

}