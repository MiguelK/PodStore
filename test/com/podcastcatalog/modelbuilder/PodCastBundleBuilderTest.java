package com.podcastcatalog.modelbuilder;

import org.testng.annotations.Test;

public class PodCastBundleBuilderTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalidName_null() {
        BundleBuilder.newPodCastBundleBuilder(null, "dsjdk");
    }

}