package com.podcastcatalog.builder.collector.okihika;

import com.podcastcatalog.api.response.PodCastEpisode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class PodCastEpisodeCollectorOkihikaTest {

/*
    @Test(groups = TestUtil.SLOW_TEST)
    public void parse() {
        PodCastCollectorOkihika podCastCollectorOkihika =  PodCastCollectorOkihika.parse(PodCastCollectorOkihika.TopList.ARTS, 3);
        List<PodCast> podCasts = podCastCollectorOkihika.collectEpisodes();
        Assert.assertFalse(podCasts.isEmpty());
    }*/
    @Test
    public void testCollectPodCastEpisodes() {

        PodCastEpisodeCollectorOkihika collectorOkihika = new PodCastEpisodeCollectorOkihika(PodCastCollectorOkihika.TopList.DESIGN, 1,2);
        List<PodCastEpisode> podCastEpisodes = collectorOkihika.collectEpisodes();

        Assert.assertTrue(podCastEpisodes.size()==2," size=" + podCastEpisodes.size());

    }

}