package com.podcastcatalog.model.subscription;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.service.ServiceDataStorageDisk;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SubscriptionDataTest {

    @Test
    public void assertSerializable() {
        TestUtil.assertSerializable(new SubscriptionData());
    }

    @Test
    public void save_load_equal() {
        ServiceDataStorageDisk storageDisk = new ServiceDataStorageDisk(TestUtil.IO_TEMP_DATA_DIRECTORY);

        SubscriptionData a = new SubscriptionData();
        storageDisk.save(a);

        Assert.assertEquals(storageDisk.loadSubscriptionData(), a);
    }


    @Test
    public void save_load_SubscriptionData() {
        ServiceDataStorageDisk storageDisk = new ServiceDataStorageDisk(TestUtil.IO_TEMP_DATA_DIRECTORY);

        storageDisk.save(new SubscriptionData());

        Assert.assertNotNull(storageDisk.loadSubscriptionData());
    }

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
    }
}