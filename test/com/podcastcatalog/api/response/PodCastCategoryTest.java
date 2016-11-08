package com.podcastcatalog.api.response;

import com.podcastcatalog.PodCastTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PodCastCategoryTest {

    @Test
    public void create_valid() {
        Assert.assertNotNull(createValid());
    }

    @Test
    public void trim() {
        Assert.assertEquals(new PodCastCategory(" sommar ","Description B","image URL", createPodCasts(), PodCastCategoryType.ARTS).getTitle(), "sommar");
        Assert.assertEquals(new PodCastCategory(" sommar "," Description B ","image URL", createPodCasts(), PodCastCategoryType.ARTS).getDescription(), "Description B");
        Assert.assertEquals(new PodCastCategory(" sommar ","Description B","   img ", createPodCasts(), PodCastCategoryType.ARTS).getImageURL(), "img");
    }

    @Test
    public void getPodCastCategoryType() {
           Assert.assertTrue(createValid().getPodCastCategoryType()==PodCastCategoryType.ARTS);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void title_null() {
        PodCastCategory p=  new PodCastCategory(null,"Description B","image URL", createPodCasts(), PodCastCategoryType.ARTS);
        Assert.fail("Invalid " + p);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void title_empty() {
        PodCastCategory p=  new PodCastCategory(" ","Description B","image URL", createPodCasts(), PodCastCategoryType.ARTS);
        Assert.fail("Invalid " + p);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void image_null() {
        PodCastCategory p=  new PodCastCategory("sdsd ","Description B",null, createPodCasts(), PodCastCategoryType.ARTS);
        Assert.fail("Invalid " + p);
    }
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void image_empty() {
        PodCastCategory p=  new PodCastCategory("sdsd ","Description B","", createPodCasts(), PodCastCategoryType.ARTS);
        Assert.fail("Invalid " + p);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void description_null() {
        PodCastCategory p=  new PodCastCategory("sdsd ",null,"sds", createPodCasts(), PodCastCategoryType.ARTS);
        Assert.fail("Invalid " + p);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void description_empty() {
        PodCastCategory p=  new PodCastCategory("sdsd ","","sds", createPodCasts(), PodCastCategoryType.ARTS);
        Assert.fail("Invalid " + p);
    }

    @Test
    public void getPodCasts() {
        Assert.assertFalse(createValid().getPodCasts().isEmpty());
    }

    private List<PodCast> createPodCasts() {
        return Collections.singletonList(PodCastTest.createValid().build());
    }

    public static PodCastCategory createValid(){
        List<PodCast> podCasts = Arrays.asList(PodCastTest.createValid().build());
        return new PodCastCategory("Title A","Description B","image URL",podCasts, PodCastCategoryType.ARTS);
    }
}