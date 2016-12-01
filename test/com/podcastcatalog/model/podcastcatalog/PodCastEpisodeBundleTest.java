package com.podcastcatalog.model.podcastcatalog;

import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeTest;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeBundle;
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
        return new PodCastEpisodeBundle("Title","Descr", podCastEpisodes);
    }

    @Test
    public void getBundleItems() {
        Assert.assertTrue(craeteValid().getBundleItems().size()==1);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void getBundleItems_should_be_unmodifiable() {
        craeteValid().getBundleItems().add(PodCastEpisodeTest.createValid().build());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podcasts_null() {
        new PodCastEpisodeBundle("Title","Descr", null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podcasts_empty() {
        new PodCastEpisodeBundle("Title","Descr", new ArrayList<>());
    }
}