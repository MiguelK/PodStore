package com.podcastcatalog.model.podcastsearch;

import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PodCastResultItemTest {

    @Test
    public void podCast() {
        PodCastResultItem item = new PodCastResultItem("1234","Title","image.jpg");

        Assert.assertTrue(item.getResultType()== ResultType.PODCAST);
    }
}