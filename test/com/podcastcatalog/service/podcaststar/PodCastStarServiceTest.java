package com.podcastcatalog.service.podcaststar;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PodCastStarServiceTest {

    private String episodeId;

    @BeforeMethod
    public void setUp() {
        PodCastStarService.getInstance().load(null);
        episodeId = "23h--s3";
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podCastId_null() {
        PodCastStarService.getInstance().starPodCast(null, episodeId, 3);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podCastId_emptyl() {
        PodCastStarService.getInstance().starPodCast(" ", episodeId, 3);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podCastEpisodeId_emptyl() {
        PodCastStarService.getInstance().starPodCast("43434 ", " ", 3);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podCastEpisodeId_nulll() {
        PodCastStarService.getInstance().starPodCast("43434 ", null, 3);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_stars_negative() {
        PodCastStarService.getInstance().starPodCast("43434 ", "eew", -1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_stars_zero() {
        PodCastStarService.getInstance().starPodCast("43434 ", "eew", 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_stars_greater_than_5() {
        PodCastStarService.getInstance().starPodCast("43434 ", "eew", 6);
    }

    @Test
    public void trim_input() {
        for (int i = 0; i < 33; i++) {
            PodCastStarService.getInstance().starPodCast("123", " " + episodeId + " ", 3);
        }
        int stars = PodCastStarService.getInstance().getStarsForEpisodeId("123",episodeId);
        Assert.assertTrue(stars == 3);
    }

    @Test
    public void do_not_show_stars_minimum_10_stars() {
        for (int i = 0; i < 8; i++) {
            PodCastStarService.getInstance().starPodCast("123", episodeId, 4);
        }
        int stars = PodCastStarService.getInstance().getStarsForEpisodeId("123",episodeId);
        Assert.assertTrue(stars == 0);
    }

    @Test
    public void average_4() {
        for (int i = 0; i < 45; i++) {
            PodCastStarService.getInstance().starPodCast("123", episodeId, 4);
        }
        for (int i = 0; i < 5; i++) {
            PodCastStarService.getInstance().starPodCast("123", episodeId, 1);
        }
        for (int i = 0; i < 15; i++) {
            PodCastStarService.getInstance().starPodCast("123", episodeId, 5);
        }

        int stars = PodCastStarService.getInstance().getStarsForEpisodeId("123",episodeId);
        Assert.assertTrue(stars == 4);
    }

}