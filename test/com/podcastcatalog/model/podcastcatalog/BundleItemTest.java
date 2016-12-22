package com.podcastcatalog.model.podcastcatalog;

import com.podcastcatalog.TestUtil;
import org.testng.annotations.Test;

public class BundleItemTest {


    @Test(groups = TestUtil.SLOW_TEST)
    public void title() {
        BundleItem podCast = PodCastTest.createValid().description("SOME X").build();
        TestUtil.assertToJSONContains(podCast, "SOME X");
        BundleItem podCastCategory = PodCastCategoryTest.createValid();

        TestUtil.assertToJSONNotNull(podCastCategory);

        BundleItem build = PodCastEpisodeTest.createValid().description("Avsnitt B").build();
        TestUtil.assertToJSONContains(build, "Avsnitt B");
    }

}