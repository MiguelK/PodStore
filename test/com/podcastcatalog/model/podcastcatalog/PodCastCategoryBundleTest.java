package com.podcastcatalog.model.podcastcatalog;

import com.podcastcatalog.TestUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class PodCastCategoryBundleTest {

    @Test(groups = TestUtil.SLOW_TEST)
    public void createValid_bunlde() {
        Assert.assertNotNull(createValid());
       }

    public static PodCastCategoryBundle createValid(){
        List<PodCastCategory> podCastCategories = Collections.singletonList(PodCastCategoryTest.createValid());

        return new PodCastCategoryBundle("Title","Description", podCastCategories);
    }
}