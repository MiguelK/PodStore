package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCastEpisode;
import com.podcastcatalog.api.response.bundle.BundleType;
import com.podcastcatalog.api.response.bundle.PodCastEpisodeBundle;
import com.podcastcatalog.builder.collector.PodCastEpisodeCollector;

import java.util.ArrayList;
import java.util.List;

class PodCastEpisodeBundleBuilder extends BundleBuilder {

    private List<PodCastEpisodeCollector> collectors;

    public PodCastEpisodeBundleBuilder(String imageURL, String title, String description,List<PodCastEpisodeCollector> collectors) {
        super(imageURL, title, description, BundleType.Episode);
        this.collectors =collectors;
    }

    public static Builder newBuilder(String imageURL, String title, String description) {
        return new Builder(imageURL, title, description);
    }

    public static class Builder{
        private List<PodCastEpisodeCollector> collectors;
        String imageURL;
        String title;
        String description;

        public Builder(String imageURL, String title, String description) {
            this.imageURL = imageURL;
            this.title = title;
            this.description = description;
            this.collectors = new ArrayList<>();
        }

        public Builder addCollector(PodCastEpisodeCollector collector){
            this.collectors.add(collector);
            return this;
        }

        public PodCastEpisodeBundleBuilder build(){
            return new PodCastEpisodeBundleBuilder(imageURL, title, description, collectors);
        }
    }

    @Override
    public PodCastEpisodeBundle call() throws Exception {

        List<PodCastEpisode> podCastEpisodes = new ArrayList<>();

        for (PodCastEpisodeCollector podCastFetcher : collectors) {//FIXME abstract Parser
            podCastEpisodes.addAll(podCastFetcher.collectPodCastEpisodes());
        }

        return new PodCastEpisodeBundle(title, description, imageURL,podCastEpisodes);
    }
}
