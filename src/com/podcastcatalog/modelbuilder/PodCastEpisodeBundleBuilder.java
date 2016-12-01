package com.podcastcatalog.modelbuilder;

import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeBundle;
import com.podcastcatalog.modelbuilder.collector.PodCastEpisodeCollector;

import java.util.ArrayList;
import java.util.List;

class PodCastEpisodeBundleBuilder extends BundleBuilder {

    private final List<PodCastEpisodeCollector> collectors;

     PodCastEpisodeBundleBuilder(String title, String description) {
        super(title, description);
        this.collectors = new ArrayList<>();
    }

    void addCollector(PodCastEpisodeCollector collector) {
        collectors.add(collector);
    }

    @Override
    protected PodCastEpisodeBundle createBundle(String title, String description) {
        List<PodCastEpisode> podCastEpisodes = new ArrayList<>();

        for (PodCastEpisodeCollector podCastFetcher : collectors) {
            podCastEpisodes.addAll(podCastFetcher.collectEpisodes());
        }

        return new PodCastEpisodeBundle(title, description, podCastEpisodes);
    }
}
