package com.podcastcatalog.builder;

import org.testng.annotations.Test;

public class PodCastBundleBuilderTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalidName_null() {
        BundleBuilder.newPodCastBundleBuilder("image", null, "dsjdk");
    }

}