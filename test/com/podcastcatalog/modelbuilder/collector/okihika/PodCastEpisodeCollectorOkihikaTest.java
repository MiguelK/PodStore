package com.podcastcatalog.modelbuilder.collector.okihika;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class PodCastEpisodeCollectorOkihikaTest {

    @Test(groups = TestUtil.SLOW_TEST)
    public void testCollectPodCastEpisodes() {

        PodCastEpisodeCollectorOkihika collectorOkihika = new PodCastEpisodeCollectorOkihika(PodCastCollectorOkihika.TopList.DESIGN, 1, 2);
        List<PodCastEpisode> podCastEpisodes = collectorOkihika.collectEpisodes();

        Assert.assertTrue(podCastEpisodes.size() == 2, " size=" + podCastEpisodes.size());

    }

}