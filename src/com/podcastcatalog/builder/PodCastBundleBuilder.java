package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.api.response.bundle.BundleType;
import com.podcastcatalog.api.response.bundle.PodCastBundle;
import com.podcastcatalog.builder.collector.PodCastCollector;

import java.util.ArrayList;
import java.util.List;

public class PodCastBundleBuilder extends BundleBuilder {

    private List<PodCastCollector> collectors;
    private PodCastBundleBuilder(String imageURL, String title, String description,
                                 List<PodCastCollector> collectors) {
        super(imageURL, title, description, BundleType.PodCast);
        this.collectors = collectors;
    }

    public static Builder newBuilder(String imageURL, String title, String description){
        return new Builder(imageURL, title, description);
    }

    public static class Builder{
        List<PodCastCollector> collectors;
        String imageURL;
        String title;
        String description;

        public Builder(String imageURL, String title, String description) {
            this.imageURL = imageURL;
            this.title = title;
            this.description = description;
            this.collectors = new ArrayList<>();
        }
        public Builder addCollector(PodCastCollector collector){
            collectors.add(collector);
            return this;
        }

        public PodCastBundleBuilder build(){
            return new PodCastBundleBuilder(imageURL, title, description, collectors);
        }
    }
    @Override
    public PodCastBundle call() throws Exception {

        List<PodCast> podCasts = new ArrayList<>();

        for (PodCastCollector podCastFetcher : collectors) {//FIXME abstract Parser
            podCasts.addAll(podCastFetcher.collect());
        }


        return new PodCastBundle(title, description, imageURL,podCasts);
    }
}
