package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCastEpisodeTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

public class PodCastEpisodeBundleBuilderTest {


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_title_null() {
        BundleBuilder.newPodCastEpisodeBundleBuilder(null, "descr");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_title_emptyl() {
        BundleBuilder.newPodCastEpisodeBundleBuilder(" ", "descr");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void onvalid_descr_null() {
        BundleBuilder.newPodCastEpisodeBundleBuilder("title", null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void onvalid_descr_empty() {
        BundleBuilder.newPodCastEpisodeBundleBuilder("title", " ");
    }

    @Test
    public void trim_title() throws Exception {
        PodCastEpisodeBundleBuilder podCastEpisodeBundleBuilder = BundleBuilder.newPodCastEpisodeBundleBuilder(" title ", " sdsdsd");
        podCastEpisodeBundleBuilder.addCollector(() -> Collections.singletonList(PodCastEpisodeTest.createValid().build()));
        Assert.assertEquals(podCastEpisodeBundleBuilder.call().getTitle(), "title");
    }

    @Test
    public void trim_descr() throws Exception {
        PodCastEpisodeBundleBuilder podCastEpisodeBundleBuilder = BundleBuilder.newPodCastEpisodeBundleBuilder("title", " aaa ");
        podCastEpisodeBundleBuilder.addCollector(() -> Collections.singletonList(PodCastEpisodeTest.createValid().build()));
        Assert.assertEquals(podCastEpisodeBundleBuilder.call().getDescription(), "aaa");
    }

    @Test
    public void trim_image_url() throws Exception {
        PodCastEpisodeBundleBuilder podCastEpisodeBundleBuilder = BundleBuilder.newPodCastEpisodeBundleBuilder(" title ", "sdsdsd");
        podCastEpisodeBundleBuilder.addCollector(() -> Collections.singletonList(PodCastEpisodeTest.createValid().build()));
        Assert.assertEquals(podCastEpisodeBundleBuilder.call().getTitle(), "title");
    }
}