package com.podcastcatalog.modelbuilder.collector.okihika;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastCategoryType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class PodCastCollectorOkihikaTest {
    @Test
    public void testParseID() {


        for (PodCastCollectorOkihika.TopList list : PodCastCollectorOkihika.TopList.values()) {

            if(list== PodCastCollectorOkihika.TopList.TOPLIST_SWEDEN){
                continue;
            }

            PodCastCategoryType a = list.toPodCastCategoryType();

            Assert.assertNotNull(a,list.name());
        }
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void toplist_Sweden() {
        PodCastCollectorOkihika swe = new PodCastCollectorOkihika(PodCastCollectorOkihika.Language.SWE, PodCastCollectorOkihika.TopList.TOPLIST_SWEDEN,20);
        List<PodCast> podCasts = swe.collectPodCasts();

        Assert.assertTrue(podCasts.size()>10);
    }
}