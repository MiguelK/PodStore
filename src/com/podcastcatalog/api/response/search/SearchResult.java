package com.podcastcatalog.api.response.search;

import java.util.List;

public class SearchResult {

    private final List<ResultItem> resultItems;

    private final int totalCount;
    private final boolean noResultFound;

    public SearchResult(List<ResultItem> resultItems) {
        totalCount = resultItems.size();
        noResultFound = resultItems.isEmpty();
        this.resultItems = resultItems;
    }

    public List<ResultItem> getResultItems() {
        return resultItems;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public boolean isNoResultFound() {
        return noResultFound;
    }
}
