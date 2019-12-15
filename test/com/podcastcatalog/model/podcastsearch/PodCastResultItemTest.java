package com.podcastcatalog.model.podcastsearch;

import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class PodCastResultItemTest {

    @Test
    public void podCast() {
        PodCastResultItem item = new PodCastResultItem("1234","Title","image.jpg");

        Assert.assertTrue(item.getResultType()== ResultType.PODCAST);
    }


    @Test
    public void sort() {

        List<ResultItem> result = new ArrayList<>();

        result.add(new PodCastEpisodeResultItem(PodCastEpisodeTest.createValid().podCastCollectionId("30").build()));
        result.add(new PodCastResultItem("3","Som","image.jpg"));
        result.add(new PodCastResultItem("1","Fram","image.jpg"));
        result.add(new PodCastResultItem("2","Gha","image.jpg"));

        result.forEach(resultItem -> System.out.println(resultItem.getTitle()));

        System.out.println("AFTER...");
        result.forEach(resultItem -> System.out.println(resultItem.getTitle()));

        Assert.assertTrue(result.get(0).getPodCastCollectionId().equals("1"));
        Assert.assertTrue(result.get(1).getPodCastCollectionId().equals("2"));
        Assert.assertTrue(result.get(2).getPodCastCollectionId().equals("3"));
        Assert.assertTrue(result.get(3).getPodCastCollectionId().equals("30"));

    }

}