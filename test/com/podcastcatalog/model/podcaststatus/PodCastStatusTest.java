package com.podcastcatalog.model.podcaststatus;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class PodCastStatusTest {
    @Test
    public void latestPodCastId() {

        Map<String,PodCast> pods = new HashMap<>();
        String podCastId = "1234";
        PodCast podCast = PodCastTest.createValid().collectionId(podCastId).build();
        String id = podCast.getLatestPodCastEpisode().getId();
        pods.put(podCastId, podCast);

        PodCastStatus podCastStatus = new PodCastStatus(pods);

        Assert.assertEquals(podCastStatus.getPodCastIdLatestEpisodeId().get(podCastId),id);

    }

}