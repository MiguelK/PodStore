package com.podcastcatalog.builder.podcastfether;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.builder.collector.okihika.PodCastCollectorOkihika;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class PodCastCollectorOkihikaTest {

    @Test(groups = TestUtil.SLOW_TEST)
    public void testName() {
        PodCastCollectorOkihika podCastCollectorOkihika =  PodCastCollectorOkihika.parse(PodCastCollectorOkihika.TopList.DESIGN, 3);
        List<PodCast> podCasts = podCastCollectorOkihika.collect();
        System.out.println(podCasts);
//        Assert.assertTrue(podCasts.size()==3);
        System.out.println(podCasts);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void parse() {
        PodCastCollectorOkihika podCastCollectorOkihika =  PodCastCollectorOkihika.parse(PodCastCollectorOkihika.TopList.ARTS, 3);
        List<PodCast> podCasts = podCastCollectorOkihika.collect();
//        System.out.println("Result=" +podCasts);
//        Assert.assertTrue(podCasts.size()==3);
//        Assert.assertTrue(podCasts.get(0).getTitle().contains("FYLLEPODDEN"));
        Assert.assertFalse(podCasts.isEmpty());
//        Assert.assertTrue(podCasts.get(1).getTitle().contains("Lofsan"));
//        Assert.assertTrue(podCasts.get(2).getTitle().contains("I nöd och lust"));
    }

}