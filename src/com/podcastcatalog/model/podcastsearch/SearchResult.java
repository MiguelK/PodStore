package com.podcastcatalog.model.podcastsearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SearchResult {

    private final List<ResultItem> resultItems;


    public SearchResult(List<ResultItem> resultItems) {

        //Remove duplicates
        HashSet<ResultItem> resultItems1 = new HashSet<>(resultItems);
        this.resultItems = new ArrayList<>(resultItems1);
    }

    public List<ResultItem> getResultItems() {
        return resultItems;
    }
}
