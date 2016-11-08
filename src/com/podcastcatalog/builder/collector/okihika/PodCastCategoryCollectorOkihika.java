package com.podcastcatalog.builder.collector.okihika;

import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.builder.collector.PodCastCategoryCollector;
import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.PodCastCategoryType;

import java.util.List;

public class PodCastCategoryCollectorOkihika extends PodCastCollectorOkihika implements PodCastCategoryCollector{

    private final String title;
    private final String description;
    private final String image;
    private final PodCastCategoryType podCastCategoryType;

    public PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList toplist, int resultSize, String title, String description,
                                           String image, PodCastCategoryType podCastCategoryType) {
        super(toplist, resultSize);
        this.title = title;
        this.description = description;
        this.image = image;
        this.podCastCategoryType = podCastCategoryType;
    }

    @Override
    public PodCastCategory collectCategories(){
        List<PodCast> podCasts = getPodCasts();

        return new PodCastCategory(title, description, image, podCasts,podCastCategoryType);
    }
}
