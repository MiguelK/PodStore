package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.api.response.bundle.PodCastBundle;
import com.podcastcatalog.builder.collector.PodCastCollector;

import java.util.ArrayList;
import java.util.List;

public class PodCastBundleBuilder extends BundleBuilder {
    private List<PodCastCollector> collectors;

    public PodCastBundleBuilder(String imageURL, String title, String description) {
        super(imageURL, title, description);
        collectors = new ArrayList<>();
    }

    public void addCollector(PodCastCollector collector) {
        collectors.add(collector);
    }

    @Override
    protected PodCastBundle createBundle(String imageURL, String title, String description) {
        List<PodCast> podCasts = new ArrayList<>();

        for (PodCastCollector podCastFetcher : collectors) {//FIXME abstract Parser
            podCasts.addAll(podCastFetcher.collectPodCasts());
        }

        return new PodCastBundle(title, description, imageURL, podCasts);
    }
}
