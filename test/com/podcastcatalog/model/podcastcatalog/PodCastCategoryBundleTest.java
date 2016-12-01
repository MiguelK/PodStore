package com.podcastcatalog.model.podcastcatalog;

import com.podcastcatalog.model.podcastcatalog.PodCastCategory;
import com.podcastcatalog.model.podcastcatalog.PodCastCategoryTest;
import com.podcastcatalog.model.podcastcatalog.PodCastCategoryBundle;
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