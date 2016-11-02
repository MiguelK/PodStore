package com.podcastcatalog.builder;

import org.testng.annotations.Test;

public class PodCastBundleBuilderTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalidName_null() {
        BundleBuilder.newPodCastBundleBuilder("image",null,"dsjdk");
    }

    /*@Test
    public void trimImgaeURL() throws Exception {
        String imageURL = BundleBuilder.newPodCastBundleBuilder(" image ", "title", "dsjdk").
                addCollector(Collections::emptyList).getImageURL();

        Assert.assertEquals(imageURL,"image");
    }*/

    @Test
    public void toJSON() {


    }

    @Test
    public void testCreate() throws Exception {

     /*   PodCastBundleBuilder bundleBuilder = PodCastBundleBuilder.create("image", "4U", "recomended",
                Collections.singletonList(() -> Collections.singletonList(PodCastTest.createValid().build())));

        Bundle bundle = bundleBuilder.call();
        Assert.assertTrue(bundle.getBundleType()== Bundle.BundleType.PodCast);*/
    }

}