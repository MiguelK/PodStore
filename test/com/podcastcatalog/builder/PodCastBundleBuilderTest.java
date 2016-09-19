package com.podcastcatalog.builder;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

public class PodCastBundleBuilderTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalidName_null() {
        PodCastBundleBuilder.newBuilder("image",null,"dsjdk").build();
    }

    @Test
    public void trimImgaeURL() throws Exception {
        String imageURL = PodCastBundleBuilder.newBuilder(" image ", "title", "dsjdk").
                addCollector(Collections::emptyList).build().call().getImageURL();

        Assert.assertEquals(imageURL,"image");
    }

    @Test
    public void testCreate() throws Exception {

     /*   PodCastBundleBuilder bundleBuilder = PodCastBundleBuilder.create("image", "4U", "recomended",
                Collections.singletonList(() -> Collections.singletonList(PodCastTest.createValid().build())));

        Bundle bundle = bundleBuilder.call();
        Assert.assertTrue(bundle.getBundleType()== Bundle.BundleType.PodCast);*/
    }

}