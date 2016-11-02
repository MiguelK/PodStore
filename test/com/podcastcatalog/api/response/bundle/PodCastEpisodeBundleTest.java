package com.podcastcatalog.api.response.bundle;

import com.podcastcatalog.api.response.PodCastEpisode;
import com.podcastcatalog.api.response.PodCastEpisodeTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class PodCastEpisodeBundleTest {

    @Test
    public void craeteValid_bundle() {
        Assert.assertNotNull(craeteValid());
    }

    public static PodCastEpisodeBundle craeteValid(){
        List<PodCastEpisode> podCastEpisodes = Arrays.asList(PodCastEpisodeTest.createValid().build());
        PodCastEpisodeBundle episodeBundle = new PodCastEpisodeBundle("Title","Descr","www.url.se", podCastEpisodes);
        return episodeBundle;

    }
}