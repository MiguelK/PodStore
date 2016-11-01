package com.podcastcatalog.api.response.bundle;

import com.podcastcatalog.api.response.PodCastEpisode;

import java.util.List;

public class PodCastEpisodeBundle extends Bundle {

    private List<PodCastEpisode> podCastEpisodes;
    public PodCastEpisodeBundle(String title, String description, String imageURL, List<PodCastEpisode> podCastEpisodes) {
        super(title, description, imageURL, BundleType.Episode);
        this.podCastEpisodes = podCastEpisodes;
    }

    @Override
    public List<PodCastEpisode> getBundleItems() {
        return podCastEpisodes;
    }
}
