package com.podcastcatalog.service.job;

import org.testng.annotations.Test;

public class SubscriptionNotifierJobTest {


    @Test
    public void testDoWork() {

        SubscriptionNotifierJob job = new SubscriptionNotifierJob();
        job.doWork();

    }

}