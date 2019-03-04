package com.podcastcatalog.model.podcastsearch;

import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;

public class PodCastEpisodeResultItem extends ResultItem {
   // private final String description;
    // private final String targetURL;

    private PodCastEpisode podCastEpisode;

    public PodCastEpisodeResultItem(PodCastEpisode podCastEpisode) {
        super(podCastEpisode.getTitle(), podCastEpisode.getPodCastCollectionId(), podCastEpisode.getArtworkUrl600(), ResultType.EPISODE);
        this.podCastEpisode = podCastEpisode;
    }

    public PodCastEpisode getPodCastEpisode() {
        return podCastEpisode;
    }
}
