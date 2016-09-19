package com.podcastcatalog.builder.collector.okihika;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.PodCastCategoryType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PodCastCategoryCollectorOkihikaTest {


    @Test(groups = TestUtil.SLOW_TEST)
    public void testCollectPodCastCategory_3() {

        //FIXME MUSIC error parsing id
        PodCastCategoryCollectorOkihika collector = new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.ARTS, 3, "Musik", "descr", "image", PodCastCategoryType.Music);

        PodCastCategory podCastCategory = collector.collectPodCastCategory();

        System.out.println(podCastCategory);
        Assert.assertFalse(podCastCategory.getPodCasts().isEmpty());
    }

}