package com.podcastcatalog.api.response.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {

    private final List<ResultItem> sortedResult = new ArrayList<>();

    private final int podCastCount;
    private final int podCastEpisodeCount;
    private final int totalCount;

    private final boolean noResultFound;

    public SearchResult(List<PodCastSearchResponse> podCasts, List<PodCastEpisodeResultItem> podCastEpisodes) {
        podCastCount = podCasts.size();
        podCastEpisodeCount = podCastEpisodes.size();
        totalCount = podCastCount + podCastEpisodeCount;
        noResultFound = podCastEpisodes.isEmpty() && podCasts.isEmpty();

        sortedResult.addAll(podCastEpisodes);
        sortedResult.addAll(podCasts);
    }

    public List<ResultItem> getSortedResult() {
        return sortedResult;
    }

    public int getPodCastCount() {
        return podCastCount;
    }

    public int getPodCastEpisodeCount() {
        return podCastEpisodeCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public boolean isNoResultFound() {
        return noResultFound;
    }
}
