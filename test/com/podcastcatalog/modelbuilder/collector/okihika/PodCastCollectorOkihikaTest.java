package com.podcastcatalog.modelbuilder.collector.okihika;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastcatalog.PodCastCategoryType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class PodCastCollectorOkihikaTest {
    @Test
    public void testParseID() {


        for (PodCastCollectorOkihika.TopList list : PodCastCollectorOkihika.TopList.values()) {

            if(list== PodCastCollectorOkihika.TopList.TOPLIST_COUNTRY){
                continue;
            }

            PodCastCategoryType a = list.toPodCastCategoryType();

            Assert.assertNotNull(a,list.name());
        }
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void toplist_SWE() {
        PodCastCollectorOkihika swe = new PodCastCollectorOkihika(PodCastCatalogLanguage.SE, PodCastCollectorOkihika.TopList.TOPLIST_COUNTRY,20);
        List<PodCast> podCasts = swe.collectPodCasts();

        Assert.assertTrue(podCasts.size()>10, "Size=" + podCasts.size());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void toplist_US() {
        PodCastCollectorOkihika us = new PodCastCollectorOkihika(PodCastCatalogLanguage.US, PodCastCollectorOkihika.TopList.TOPLIST_COUNTRY,20);
        List<PodCast> podCasts = us.collectPodCasts();

        Assert.assertTrue(podCasts.size()>10, "Size=" + podCasts.size());
    }
}