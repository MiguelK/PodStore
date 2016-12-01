package com.podcastcatalog.model.podcastcatalog;

import com.google.gson.Gson;
import com.podcastcatalog.model.podcastcatalog.BundleItem;
import com.podcastcatalog.model.podcastcatalog.PodCastCategoryTest;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeTest;
import com.podcastcatalog.model.podcastcatalog.PodCastTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BundleItemTest {

    private static final Gson GSON = new Gson();

    @Test
    public void title() {
        BundleItem podCast = PodCastTest.createValid().description("SOME X").build();
        Assert.assertTrue(GSON.toJson(podCast).contains("SOME X"));
        BundleItem podCastCategory = PodCastCategoryTest.createValid();
        Assert.assertNotNull(GSON.toJson(podCastCategory));

        BundleItem build = PodCastEpisodeTest.createValid().description("Avsnitt B").build();
        Assert.assertTrue(GSON.toJson(build).contains("Avsnitt B"));
    }

}