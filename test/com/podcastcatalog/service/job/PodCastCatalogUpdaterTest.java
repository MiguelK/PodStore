package com.podcastcatalog.service.job;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.service.datastore.PodCastCatalogVersion;
import com.podcastcatalog.service.datastore.ServiceDataStorage;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogServiceTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class PodCastCatalogUpdaterTest {


    @Test(groups = TestUtil.SLOW_TEST)
    public void doWork() throws InterruptedException, ExecutionException, TimeoutException {
        PodCastCatalogServiceTest.initPodCastCatalogs();

        PodCastCatalogUpdater updater = new PodCastCatalogUpdater();
        updater.doWork();

        Thread.sleep(15000);

        PodCastCatalogVersion podCastCatalogVersion = ServiceDataStorage.useDefault().getCurrentVersion(PodCastCatalogLanguage.SWE).get();

        Assert.assertTrue(podCastCatalogVersion.getVersion()==2,"was=" + podCastCatalogVersion.getVersion());

    }

}