package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCastEpisode;
import com.podcastcatalog.api.response.bundle.PodCastEpisodeBundle;
import com.podcastcatalog.builder.collector.PodCastEpisodeCollector;

import java.util.ArrayList;
import java.util.List;

public class PodCastEpisodeBundleBuilder extends BundleBuilder {

    private final List<PodCastEpisodeCollector> collectors;

     PodCastEpisodeBundleBuilder(String imageURL, String title, String description) {
        super(imageURL, title, description);
        this.collectors = new ArrayList<>();
    }

    public void addCollector(PodCastEpisodeCollector collector) {
        collectors.add(collector);
    }

    @Override
    protected PodCastEpisodeBundle createBundle(String imageURL, String title, String description) {
        List<PodCastEpisode> podCastEpisodes = new ArrayList<>();

        for (PodCastEpisodeCollector podCastFetcher : collectors) {
            podCastEpisodes.addAll(podCastFetcher.collectEpisodes());
        }

        return new PodCastEpisodeBundle(title, description, imageURL, podCastEpisodes);
    }
}
