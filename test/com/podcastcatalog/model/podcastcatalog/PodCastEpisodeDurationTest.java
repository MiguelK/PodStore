package com.podcastcatalog.model.podcastcatalog;

import com.google.gson.Gson;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PodCastEpisodeDurationTest {


    private final Gson gson = new Gson();

    @Test
    public void toJSON() {
        PodCastEpisodeDuration duration1 = PodCastEpisodeDuration.parse("1:00:02");
        PodCastEpisodeDuration duration2 = PodCastEpisodeDuration.parse("3:44");
        PodCastEpisodeDuration duration3 = PodCastEpisodeDuration.parse("103:06");

        Assert.assertNotNull(gson.toJson(duration1));
        Assert.assertNotNull(gson.toJson(duration2));
        Assert.assertNotNull(gson.toJson(duration3));
    }

    @Test
    public void parse_valid_duration_seconds1() {
        Assert.assertTrue(PodCastEpisodeDuration.parse("1044").getMinutes()==17);
        Assert.assertTrue(PodCastEpisodeDuration.parse("185").getMinutes()==3);
        Assert.assertTrue(PodCastEpisodeDuration.parse("185").getHour()==0);
        Assert.assertTrue(PodCastEpisodeDuration.parse("185").getSeconds()==5);
        Assert.assertTrue(PodCastEpisodeDuration.parse("60").getMinutes()==1);
    }

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
    public void parse_valid_duration_4() {
        PodCastEpisodeDuration duration = PodCastEpisodeDuration.parse("00:4:50");
        Assert.assertNotNull(duration);
        Assert.assertTrue(duration.getHour()==0);
        Assert.assertTrue(duration.getMinutes()==4);
        Assert.assertTrue(duration.getSeconds()==50);
    }

    @Test
    public void parse_valid_duration_seconds() {
        PodCastEpisodeDuration duration = PodCastEpisodeDuration.parse("45:34");
        Assert.assertNotNull(duration);
        Assert.assertTrue(duration.getHour() == 0);
        Assert.assertTrue(duration.getMinutes() == 45);
        Assert.assertTrue(duration.getSeconds() == 34);
    }

    @Test
    public void parse_valid_duration_seconds2() {
        PodCastEpisodeDuration duration = PodCastEpisodeDuration.parse("100:34");
        Assert.assertNotNull(duration);
        Assert.assertTrue(duration.getHour() == 1);
        Assert.assertTrue(duration.getMinutes() == 40);
        Assert.assertTrue(duration.getSeconds() == 34);
    }

    @Test
    public void parse_display_value() {
    //    Assert.assertEquals(PodCastEpisodeDuration.parse("02:23:12").getDisplayValue(),"2h 23m");
        //    Assert.assertEquals(PodCastEpisodeDuration.parse("01:03:12").getDisplayValue(),"1h 3m");
        //  Assert.assertEquals(PodCastEpisodeDuration.parse("01:25:56").getDisplayValue(),"1h 25m");
        //  Assert.assertEquals(PodCastEpisodeDuration.parse("01:25:60").getDisplayValue(),"1h 25m");
        // Assert.assertEquals(PodCastEpisodeDuration.parse(" 00:58:60 ").getDisplayValue(),"58m");
        Assert.assertEquals(PodCastEpisodeDuration.parse(" 1044 ").getDisplayValue(),"17m");
    }

    @Test
    public void invalid_duration_null() {
        Assert.assertNull(PodCastEpisodeDuration.parse("01::15"));
        Assert.assertNull(PodCastEpisodeDuration.parse(" 01::15"));
        Assert.assertNull(PodCastEpisodeDuration.parse(""));
        Assert.assertNull(PodCastEpisodeDuration.parse(null));
    }
}