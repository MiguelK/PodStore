package com.podcastcatalog.api.response.bundle;

import com.podcastcatalog.api.response.PodCastEpisode;
import com.podcastcatalog.api.response.PodCastEpisodeTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PodCastEpisodeBundleTest {

    @Test
    public void craeteValid_bundle() {
        Assert.assertNotNull(craeteValid());
    }

    public static PodCastEpisodeBundle craeteValid(){
        List<PodCastEpisode> podCastEpisodes = Collections.singletonList(PodCastEpisodeTest.createValid().build());
        return new PodCastEpisodeBundle("Title","Descr","www.url.se", podCastEpisodes);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podcasts_null() {
        new PodCastEpisodeBundle("Title","Descr","www.url.se", null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podcasts_empty() {
        new PodCastEpisodeBundle("Title","Descr","www.url.se", new ArrayList<>());
    }
}