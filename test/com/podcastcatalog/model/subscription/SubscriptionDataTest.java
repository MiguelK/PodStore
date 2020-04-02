package com.podcastcatalog.model.subscription;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.PodCastCatalogMetaData;
import com.podcastcatalog.service.subscription.FtpOneClient;
import com.podcastcatalog.util.IOUtil;
import org.apache.commons.net.ftp.FTP;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.podcastcatalog.service.subscription.FtpOneClient.SUBSCRIPTIONS_PATH;

public class SubscriptionDataTest {

    @Test
    public void assertSerializable() {
        TestUtil.assertSerializable(new SubscriptionData());
    }

    @Test
    public void addOne() {
        SubscriptionData subscriptionData = new SubscriptionData();
        subscriptionData.addSubscription(new Subscription("1111"));
        Assert.assertEquals(subscriptionData.getSubscriptions().size(), 1);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void save_load_MetaData() throws Exception {

        String fileName = "SE_MetaData.dat";
        File testFile = new File(TestUtil.IO_TEMP_DATA_DIRECTORY, fileName);

        PodCastCatalogMetaData m = new PodCastCatalogMetaData();

        IOUtil.saveAsObject(m, testFile);

        Thread.sleep(2000);
        System.out.println("Try loadig...");
        Object object = IOUtil.getObject(testFile);
        System.out.println(object);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void save_load_SubscriptionData() throws Exception {

        String fileName = "test1.dat";
        File testFile = new File(TestUtil.IO_TEMP_DATA_DIRECTORY, fileName);
        testFile.createNewFile();

        SubscriptionData subscriptionData = new SubscriptionData();
        subscriptionData.addSubscription(new Subscription("1111"));
        subscriptionData.addSubscription(new Subscription("3333"));

        IOUtil.saveAsObject(subscriptionData, testFile);
        FtpOneClient.getInstance().uploadToOneCom(testFile, FtpOneClient.PATH_SUBSCRIPTION);

        Subscription subscription = subscriptionData.getSubscription("1111");
        if(subscription != null) {
            subscription.setLatestPodCastEpisodeId("2222");
            IOUtil.saveAsObject(subscriptionData, testFile);
            FtpOneClient.getInstance().uploadToOneCom(testFile, FtpOneClient.PATH_SUBSCRIPTION);

        }

        Thread.sleep(3000);

        String pathOnServer = SUBSCRIPTIONS_PATH + fileName;
        SubscriptionData subscriptionData1 = (SubscriptionData) FtpOneClient.getInstance().loadFromServer(testFile, pathOnServer);
        System.out.println("From remote: " + subscriptionData1.getSubscriptions().size());

        Assert.assertEquals(subscriptionData1.getSubscriptions().get(0).getLatestPodCastEpisodeId(), "2222");
        Assert.assertEquals(subscriptionData1.getSubscriptions().get(0).getPodCastId(), "1111");
        Assert.assertEquals(subscriptionData1.getSubscriptions().size(), 2);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void loadFromServer() throws IOException {
        String fileName = "Subscriptions.dat";
        File testFile = new File(TestUtil.IO_TEMP_DATA_DIRECTORY, fileName);
        testFile.createNewFile();

        String pathOnServer = SUBSCRIPTIONS_PATH + fileName;
        SubscriptionData subscriptionData = (SubscriptionData) FtpOneClient.getInstance().loadFromServer(testFile, pathOnServer);


        Set<String> subscribers = new HashSet<>();
        for (Subscription subscription : subscriptionData.getSubscriptions()) {

            String row = "https://pods.one/open?pid=" + subscription.getPodCastId() + " , " + subscription.getLatestPodCastEpisodeId() + ", " + subscription.getSubscribers().size();

            row += System.lineSeparator() + "{" + System.lineSeparator();
            //System.out.println(subscription.getPodCastId() + ", " + subscription.getLatestPodCastEpisodeId() + ", " + subscription.getSubscribers().size());

            for (String subscriber  : subscription.getSubscribers()) {
                row+=  subscriber + System.lineSeparator();
                subscribers.add(subscriber);
            }
            row+= "}";

            System.out.println(row);
        }

        System.out.println("Subscriptions=" + subscriptionData.getSubscriptions().size());
        System.out.println("Unique Subscribers=" + subscribers.size());
    }

    /*

    @Test
    public void equal_SubscriptionData() {
        SubscriptionData a = new SubscriptionData();
        SubscriptionData b = new SubscriptionData();

        Assert.assertEquals(a,b);
    }

    @Test
    public void equal_SubscriptionData_same_subscriber() {
        SubscriptionData a = new SubscriptionData();
        a.addSubscription(new Subscription("123"));
        SubscriptionData b = new SubscriptionData();
        b.addSubscription(new Subscription("123"));

        Assert.assertEquals(a,b);
    }

    @Test
    public void equal_SubscriptionData_same_subscriptions() {
        SubscriptionData a = new SubscriptionData();
        a.addSubscription(new Subscription("333"));
        SubscriptionData b = new SubscriptionData();
        b.addSubscription(new Subscription("333"));

        Assert.assertEquals(a,b);
    }*/
}