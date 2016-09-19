package com.podcastcatalog.builder;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.api.response.bundle.Bundle;
import org.testng.annotations.Test;

import java.util.Set;

public class PodCastCatalogBuilderServiceTest {

    @Test(groups = TestUtil.SLOW_TEST)
    public void fetchBuildCatalog() throws Exception {
//        PodCastCatalogBuilderService service = new PodCastCatalogBuilderService();

        PodCastCatalogBuilderSE podCastCatalogBuilderSE = new PodCastCatalogBuilderSE();

        Set<BundleBuilder> bundleBuilders = podCastCatalogBuilderSE.getBundleBuilders();

        for (BundleBuilder bundleBuilder : bundleBuilders) {
            Bundle bundle = bundleBuilder.call();
            System.out.println(bundle);
        }

// /        PodCastCatalog podCastCatalog = service.fetchCatalog(new PodCastCatalogBuilderSE());

//        System.out.println("" + podCastCatalog);
//        Assert.assertNotNull(podCastCatalog);
    }

}