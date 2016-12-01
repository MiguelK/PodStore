package com.podcastcatalog.modelbuilder;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastBundle;
import com.podcastcatalog.modelbuilder.collector.PodCastCollector;

import java.util.ArrayList;
import java.util.List;

public class PodCastBundleBuilder extends BundleBuilder {
    private final List<PodCastCollector> collectors;

    public PodCastBundleBuilder(String title, String description) {
        super(title, description);
        collectors = new ArrayList<>();
    }

    public void addCollector(PodCastCollector collector) {
        collectors.add(collector);
    }

    @Override
    protected PodCastBundle createBundle(String title, String description) {
        List<PodCast> podCasts = new ArrayList<>();

        for (PodCastCollector podCastFetcher : collectors) {//FIXME abstract Parser
            podCasts.addAll(podCastFetcher.collectPodCasts());
        }

        return new PodCastBundle(title, description, podCasts);
    }
}
