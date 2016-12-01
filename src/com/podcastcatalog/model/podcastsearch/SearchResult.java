package com.podcastcatalog.model.podcastsearch;

import java.util.List;

public class SearchResult {

    private final List<ResultItem> resultItems;


    public SearchResult(List<ResultItem> resultItems) {
        this.resultItems = resultItems;
    }

    public List<ResultItem> getResultItems() {
        return resultItems;
    }
}
