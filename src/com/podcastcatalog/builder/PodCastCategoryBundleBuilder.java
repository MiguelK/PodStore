package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.bundle.PodCastCategoryBundle;
import com.podcastcatalog.builder.collector.PodCastCategoryCollector;

import java.util.ArrayList;
import java.util.List;

public class PodCastCategoryBundleBuilder extends BundleBuilder {

    private List<PodCastCategoryCollector> collectors;

    public PodCastCategoryBundleBuilder(String imageURL, String title, String description) {
        super(imageURL, title, description);
        this.collectors = new ArrayList<>();
    }

    public void addCollector(PodCastCategoryCollector collector){
        collectors.add(collector);
    }

    @Override
    protected PodCastCategoryBundle createBundle(String imageURL, String title, String description) {
        List<PodCastCategory> podCastCategories = new ArrayList<>();

        for (PodCastCategoryCollector collector : collectors) {//FIXME abstract Parser
            podCastCategories.add(collector.collectCategories());
        }

        return new PodCastCategoryBundle(title, description, imageURL,podCastCategories);
    }
}
