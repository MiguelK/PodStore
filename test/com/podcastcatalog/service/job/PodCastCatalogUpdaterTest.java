package com.podcastcatalog.service.job;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.service.ServiceDataStorage;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogServiceTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class PodCastCatalogUpdaterTest {


    @Test(groups = TestUtil.SLOW_TEST)
    public void doWork() throws InterruptedException, ExecutionException, TimeoutException {
        PodCastCatalogServiceTest.initPodCastCatalogs();;

        PodCastCatalogUpdater updater = new PodCastCatalogUpdater();
        updater.doWork();

        Thread.sleep(20000);

        ServiceDataStorage.PodCastCatalogVersion podCastCatalogVersion = ServiceDataStorage.useDefault().getCurrentVersion().get();

        Assert.assertTrue(podCastCatalogVersion.getVersion()==2);

    }

}