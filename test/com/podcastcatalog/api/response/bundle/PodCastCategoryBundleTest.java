package com.podcastcatalog.api.response.bundle;

import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.PodCastCategoryTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class PodCastCategoryBundleTest {

    @Test
    public void createValid_bunlde() {
        Assert.assertNotNull(createValid());
       }

    public static PodCastCategoryBundle createValid(){
        List<PodCastCategory> podCastCategories = Arrays.asList(PodCastCategoryTest.createValid());

        return new PodCastCategoryBundle("Title","Description","url.se",podCastCategories);
    }
}