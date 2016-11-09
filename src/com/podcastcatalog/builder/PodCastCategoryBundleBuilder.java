package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.bundle.PodCastCategoryBundle;
import com.podcastcatalog.builder.collector.PodCastCategoryCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        List<PodCastCategory> podCastCategories = collectors.stream()
                .map(PodCastCategoryCollector::collectCategories).collect(Collectors.toList());

        //FIXME abstract Parser
        return new PodCastCategoryBundle(title, description, imageURL,podCastCategories);
    }
}
