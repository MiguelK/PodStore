package com.podcastcatalog.api.response.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {

    private List<PodCastSearchResponse> casts = new ArrayList<>();

    private List<PodCastEpisodeSearchResult> podCastEpisodes = new ArrayList<>();

    private final int podCastCount;
    private final int podCastEpisodeCount;

    private final boolean noResultFound;

    public SearchResult(List<PodCastSearchResponse> podCasts, List<PodCastEpisodeSearchResult> podCastEpisodes) {
        this.casts = podCasts;
        this.podCastEpisodes = podCastEpisodes;
        podCastCount = podCasts.size();
        podCastEpisodeCount = podCastEpisodes.size();
        noResultFound = podCastEpisodes.isEmpty() && podCasts.isEmpty();
    }

    public List<PodCastSearchResponse> getCasts() {
        return casts;
    }

    public boolean isNoResultFound() {
        return noResultFound;
    }

    public List<PodCastEpisodeSearchResult> getPodCastEpisodes() {
        return podCastEpisodes;
    }

    public int getPodCastCount() {
        return podCastCount;
    }

    public int getPodCastEpisodeCount() {
        return podCastEpisodeCount;
    }
}
