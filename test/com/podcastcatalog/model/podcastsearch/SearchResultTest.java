package com.podcastcatalog.model.podcastsearch;

import com.podcastcatalog.model.podcastsearch.PodCastEpisodeResultItem;
import com.podcastcatalog.model.podcastsearch.PodCastResultItem;
import com.podcastcatalog.model.podcastsearch.SearchResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

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
        Assert.assertTrue(searchResult.getResultItems().size() == 2);
    }


}