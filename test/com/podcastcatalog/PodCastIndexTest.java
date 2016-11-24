package com.podcastcatalog;

import com.podcastcatalog.api.response.PodCast;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PodCastIndexTest {

    @Test
    public void lookup_found() {
        PodCast a = PodCastTest.createValid().description("a").collectionId("1").build();
        PodCast b = PodCastTest.createValid().description("b").collectionId("2").build();

        PodCastIndex podCastIndex = PodCastIndex.create();
        podCastIndex.buildIndex(Arrays.asList(a, b));

        Assert.assertEquals(podCastIndex.lookup("2").orElseGet(null), b);
    }

    @Test
    public void buildIndex_100() {

        List<PodCast> podCasts = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            podCasts.add(PodCastTest.createValid().description("b" + i).collectionId("" + i).build());
        }

        PodCastIndex podCastIndex = PodCastIndex.create();
        podCastIndex.buildIndex(podCasts);

        Assert.assertNotNull(podCastIndex.lookup("66"));
    }
}