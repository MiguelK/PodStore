package com.podcastcatalog.model.podcastcatalog;

import java.util.List;

public class PodCastEpisodeBundle extends Bundle {

    private final List<PodCastEpisode> podCastEpisodes;
    public PodCastEpisodeBundle(String title, String description, List<PodCastEpisode> podCastEpisodes) {
        super(title, description, BundleType.Episode);

        if(podCastEpisodes==null || podCastEpisodes.isEmpty()){
            throw new IllegalArgumentException("Missing  podCastEpisodes");
        }

        this.podCastEpisodes = podCastEpisodes;
    }

    @Override
    public List<PodCastEpisode> getBundleItems() {
        return podCastEpisodes;
    }

    @Override
    public String toString() {
        return "PodCastEpisodeBundle{" +
                "podCastEpisodes=" + podCastEpisodes.size() +
                '}';
    }
}
