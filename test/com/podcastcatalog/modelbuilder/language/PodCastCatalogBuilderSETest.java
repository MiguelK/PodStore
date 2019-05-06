package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.Bundle;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.BundleBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PodCastCatalogBuilderSETest {

    private final ExecutorService bundleBuilderExecutor = Executors.newFixedThreadPool(10);

    @Test
    public void fetchBuildCatalog() throws Exception {

        Set<BundleBuilder> bundleBuilders = PodCastCatalogLanguage.IE.create().getBundleBuilders();

        Assert.assertNotNull(bundleBuilders);

        List<Future<Bundle>> futures = bundleBuilderExecutor.invokeAll(bundleBuilders);

        for (Future<Bundle> future : futures) {

            Bundle bundle = future.get(20, TimeUnit.SECONDS);
            System.out.println("Bundle " + bundle.getTitle() + bundle.getBundleType() + " bundleItems=" + bundle.getBundleItems().size());

        }

    }


}