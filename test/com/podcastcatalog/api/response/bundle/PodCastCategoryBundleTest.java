package com.podcastcatalog.api.response.bundle;

import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.PodCastCategoryTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class PodCastCategoryBundleTest {

    @Test
    public void createValid_bunlde() {
        Assert.assertNotNull(createValid());
       }

    public static PodCastCategoryBundle createValid(){
        List<PodCastCategory> podCastCategories = Collections.singletonList(PodCastCategoryTest.createValid());

        return new PodCastCategoryBundle("Title","Description", podCastCategories);
    }
}