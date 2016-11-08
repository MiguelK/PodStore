package com.podcastcatalog.builder;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;

public class PodCastCatalogBuilderServiceTest {

    @Test
    public void fetchBuildCatalog() throws Exception {

        PodCastCatalogBuilderSE podCastCatalogBuilderSE = new PodCastCatalogBuilderSE();

        Set<BundleBuilder> bundleBuilders = podCastCatalogBuilderSE.getBundleBuilders();
        Assert.assertNotNull(bundleBuilders);
    }
}