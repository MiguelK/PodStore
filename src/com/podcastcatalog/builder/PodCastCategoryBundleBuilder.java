package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.bundle.PodCastCategoryBundle;
import com.podcastcatalog.builder.collector.PodCastCategoryCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PodCastCategoryBundleBuilder extends BundleBuilder {

    private final List<PodCastCategoryCollector> collectors;

    public PodCastCategoryBundleBuilder(String title, String description) {
        super(title, description);
        this.collectors = new ArrayList<>();
    }

    public void addCollector(PodCastCategoryCollector collector){
        collectors.add(collector);
    }

    @Override
    protected PodCastCategoryBundle createBundle(String title, String description) {
        List<PodCastCategory> podCastCategories = collectors.stream()
                .map(PodCastCategoryCollector::collectCategories).collect(Collectors.toList());

        //FIXME abstract Parser
        return new PodCastCategoryBundle(title, description, podCastCategories);
    }
}
