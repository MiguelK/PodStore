package com.podcastcatalog.api.response;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PodCastEpisodeTypeTest {

    @Test
    public void fromString_valid() {
        Assert.assertTrue(PodCastEpisodeType.fromString("application/pdf")==PodCastEpisodeType.PDF);
        Assert.assertTrue(PodCastEpisodeType.fromString("application/PDF")==PodCastEpisodeType.PDF);
        Assert.assertTrue(PodCastEpisodeType.fromString("audio/mpeg")==PodCastEpisodeType.Audio);
    }

    @Test
    public void fromString_invalid() {
        Assert.assertTrue(PodCastEpisodeType.fromString("aaaspplication/pdf")==PodCastEpisodeType.Unknown);
        Assert.assertTrue(PodCastEpisodeType.fromString("audio/ssss")==PodCastEpisodeType.Unknown);
        Assert.assertTrue(PodCastEpisodeType.fromString("")==PodCastEpisodeType.Unknown);
        Assert.assertTrue(PodCastEpisodeType.fromString(null)==PodCastEpisodeType.Unknown);
    }
}