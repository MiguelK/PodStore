package com.podcastcatalog.api.response.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {

    private final List<ResultItem> sortedResult = new ArrayList<>();

    private final int totalCount;
    private final boolean noResultFound;

    public SearchResult(List<ResultItem> resultItems) {
        totalCount = resultItems.size();
        noResultFound = resultItems.isEmpty();

        sortedResult.addAll(resultItems);
    }

    public List<ResultItem> getSortedResult() {
        return sortedResult;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public boolean isNoResultFound() {
        return noResultFound;
    }
}
