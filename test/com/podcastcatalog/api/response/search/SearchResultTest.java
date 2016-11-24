package com.podcastcatalog.api.response.search;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

public class SearchResultTest {

    @Test
    public void check_result_items() {
        SearchResult searchResult = new SearchResult(Arrays.asList(new PodCastEpisodeResultItem("title", "descr", "123", "url"),
                new PodCastResultItem("title", "descr", "artWorkUrl")));

        Assert.assertTrue(searchResult.getResultItems().stream().filter(e-> e instanceof PodCastEpisodeResultItem).toArray().length==1);
        Assert.assertTrue(searchResult.getResultItems().stream().filter(e-> e instanceof PodCastResultItem).toArray().length==1);
    }

    @Test
    public void totalCount() {
        SearchResult searchResult = new SearchResult(Arrays.asList(new PodCastEpisodeResultItem("title", "descr", "123", "url"),
                new PodCastEpisodeResultItem("title", "descr", "123", "url")));
        Assert.assertTrue(searchResult.getTotalCount() == 2);
    }

    @Test
    public void isNoResultFound_false() {
        SearchResult searchResult = new SearchResult(Collections.singletonList(new PodCastEpisodeResultItem("title", "descr", "123", "url")));
        Assert.assertFalse(searchResult.isNoResultFound());
    }

    @Test
    public void isNoResultFound_true() {
        SearchResult searchResult = new SearchResult(Collections.emptyList());
        Assert.assertTrue(searchResult.isNoResultFound());
    }
}