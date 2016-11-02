package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCastEpisodeTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;

public class PodCastEpisodeBundleBuilderTest {


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_image_url() {
        BundleBuilder.newPodCastEpisodeBundleBuilder(null, "title", "descr");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_title_null() {
        BundleBuilder.newPodCastEpisodeBundleBuilder("image.se", null, "descr");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_title_emptyl() {
        BundleBuilder.newPodCastEpisodeBundleBuilder("image.se", " ", "descr");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void onvalid_descr_null() {
        BundleBuilder.newPodCastEpisodeBundleBuilder("image.se", "title", null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void onvalid_descr_empty() {
        BundleBuilder.newPodCastEpisodeBundleBuilder("image.se", "title", " ");
    }

    @Test
    public void trim_title() throws Exception {
        PodCastEpisodeBundleBuilder podCastEpisodeBundleBuilder = BundleBuilder.newPodCastEpisodeBundleBuilder("image.se", " title ", " sdsdsd");
        podCastEpisodeBundleBuilder.addCollector(() -> Collections.singletonList(PodCastEpisodeTest.createValid().build()));
        Assert.assertEquals(podCastEpisodeBundleBuilder.call().getTitle(), "title");
    }

    @Test
    public void trim_descr() throws Exception {
        PodCastEpisodeBundleBuilder podCastEpisodeBundleBuilder = BundleBuilder.newPodCastEpisodeBundleBuilder("image.se", "title", " aaa ");
        podCastEpisodeBundleBuilder.addCollector(() -> Collections.singletonList(PodCastEpisodeTest.createValid().build()));
        Assert.assertEquals(podCastEpisodeBundleBuilder.call().getDescription(), "aaa");
    }

    @Test
    public void trim_image_url() throws Exception {
        PodCastEpisodeBundleBuilder podCastEpisodeBundleBuilder = BundleBuilder.newPodCastEpisodeBundleBuilder(" image.se/x.png ", " title ", "sdsdsd");
        podCastEpisodeBundleBuilder.addCollector(() -> Collections.singletonList(PodCastEpisodeTest.createValid().build()));
        Assert.assertEquals(podCastEpisodeBundleBuilder.call().getImageURL(), "image.se/x.png");
    }
}