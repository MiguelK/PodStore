package com.podcastcatalog.tag;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;

import java.util.ArrayList;
import java.util.List;

public class TagSearchResult {
    private List<PodCast> podCasts = new ArrayList<>();
    private List<PodCastEpisode> podCastEpisodes = new ArrayList<>();

    public List<PodCast> getPodCasts() {
        return podCasts;
    }

    public List<PodCastEpisode> getPodCastEpisodes() {
        return podCastEpisodes;
    }
}
