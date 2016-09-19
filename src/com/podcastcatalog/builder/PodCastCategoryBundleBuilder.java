package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.bundle.BundleType;
import com.podcastcatalog.api.response.bundle.PodCastCategoryBundle;
import com.podcastcatalog.builder.collector.PodCastCategoryCollector;

import java.util.ArrayList;
import java.util.List;

public class PodCastCategoryBundleBuilder extends BundleBuilder {

    private List<PodCastCategoryCollector> collectors;

    private PodCastCategoryBundleBuilder(String imageURL, String title, String description,
                                         List<PodCastCategoryCollector> collectors) {
        super(imageURL, title, description, BundleType.Category);
        this.collectors = collectors;
    }

    static  Builder newBuilder(String imageURL, String title, String description) {
        return new Builder(imageURL, title, description);
    }

    static class Builder{
        List<PodCastCategoryCollector> collectors;
        String imageURL;
        String title;
        String description;

        public Builder(String imageURL, String title, String description) {
            this.collectors = new ArrayList<>();
            this.imageURL =imageURL;
            this.title =title;
            this.description = description;
        }

        public Builder addCollector(PodCastCategoryCollector collector){
            collectors.add(collector);
            return this;
        }

        public PodCastCategoryBundleBuilder build(){
            return new PodCastCategoryBundleBuilder(imageURL, title, description,collectors);
        }
    }


    @Override
    public PodCastCategoryBundle call() throws Exception {

        List<PodCastCategory> podCastCategories = new ArrayList<>();

        for (PodCastCategoryCollector podCastFetcher : collectors) {//FIXME abstract Parser
            podCastCategories.add(podCastFetcher.collectPodCastCategory());
        }

        return new PodCastCategoryBundle(title, description, imageURL,podCastCategories);
    }
}
