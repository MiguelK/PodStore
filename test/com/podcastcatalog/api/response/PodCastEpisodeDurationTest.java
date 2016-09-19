package com.podcastcatalog.api.response;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PodCastEpisodeDurationTest {

    @Test
    public void parse_valid_duration_1() {
        PodCastEpisodeDuration podCastEpisodeDuration = PodCastEpisodeDuration.parse("01:24:15");
        Assert.assertTrue((podCastEpisodeDuration != null ? podCastEpisodeDuration.getTotalTimeInSeconds() : 0) ==5055," was=" + podCastEpisodeDuration.getTotalTimeInSeconds());
        Assert.assertTrue(podCastEpisodeDuration.getHour()==1);
        Assert.assertTrue(podCastEpisodeDuration.getMinutes()==24);
        Assert.assertTrue(podCastEpisodeDuration.getSeconds()==15);
    }

    @Test
    public void parse_valid_duration_2() {
        PodCastEpisodeDuration s = PodCastEpisodeDuration.parse("1:00:02");
        Assert.assertNotNull(s);
        Assert.assertTrue(s.getHour()==1);
        Assert.assertTrue(s.getMinutes()==0);
        Assert.assertTrue(s.getSeconds()==2);
    }

    @Test
    public void parse_valid_duration_3() {
        PodCastEpisodeDuration duration = PodCastEpisodeDuration.parse("02:23:12");
        Assert.assertNotNull(duration);
        Assert.assertTrue(duration.getHour()==2);
        Assert.assertTrue(duration.getMinutes()==23);
        Assert.assertTrue(duration.getSeconds()==12);
    }


    @Test
    public void invalid_duration_null() {
        Assert.assertNull(PodCastEpisodeDuration.parse("01::15"));
        Assert.assertNull(PodCastEpisodeDuration.parse(" 01::15"));
        Assert.assertNull(PodCastEpisodeDuration.parse("0:15"));
        Assert.assertNull(PodCastEpisodeDuration.parse(""));
        Assert.assertNull(PodCastEpisodeDuration.parse(null));
    }
}