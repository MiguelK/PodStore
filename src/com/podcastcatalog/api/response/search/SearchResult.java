package com.podcastcatalog.api.response.search;

import com.podcastcatalog.api.response.PodCastEpisode;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {

    private List<PodCastSearchResponse> casts = new ArrayList<>();

    private List<PodCastEpisode> podCastEpisodes = new ArrayList<>();

    private int podCastCount;
    private int podCastEpisodeCount;

    private boolean noResultFound;

    public SearchResult(List<PodCastSearchResponse> casts, List<PodCastEpisode> podCastEpisodes) {
        this.casts = casts;
        this.podCastEpisodes = podCastEpisodes;
        podCastCount = casts.size();
        podCastEpisodeCount = 88;
    }

    public List<PodCastSearchResponse> getCasts() {
        return casts;
    }

    public boolean isNoResultFound() {
        return noResultFound;
    }

    public List<PodCastEpisode> getPodCastEpisodes() {
        return podCastEpisodes;
    }

    public int getPodCastCount() {
        return podCastCount;
    }

    public int getPodCastEpisodeCount() {
        return podCastEpisodeCount;
    }
}
