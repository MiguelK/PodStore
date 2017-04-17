package com.podcastcatalog.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import static com.podcastcatalog.util.PodCastURLParser.parsePodCastEpisodeId;
import static org.testng.Assert.*;

public class PodCastURLParserTest {
    @Test
    public void testParsePodCastEpisodeId()  {

        PodCastURLParser.Parameters parameters3 = PodCastURLParser.parsePodCastEpisodeId("pid%3D308339623&isi=1209200428&ibi=com.app.Pods");
        PodCastURLParser.Parameters parameters1 = PodCastURLParser.parsePodCastEpisodeId("pid%3D308339623%26eid%3D-1f2c4e48+79d72f26&isi=1209200428&ibi=com.app.Pods");
        PodCastURLParser.Parameters parameters2 = PodCastURLParser.parsePodCastEpisodeId("pid%3D1219271980%26eid%3D+7ce425ca-62ff7def&isi=1209200428&ibi=com.app.Pods");

        Assert.assertEquals(parameters1.getPodCastEpisodeId(), "-1f2c4e48+79d72f26");
        Assert.assertEquals(parameters2.getPodCastEpisodeId(), "+7ce425ca-62ff7def");
        Assert.assertEquals(parameters3.getPodCastId(), "308339623");
        Assert.assertNull(parameters3.getPodCastEpisodeId());


    }

}