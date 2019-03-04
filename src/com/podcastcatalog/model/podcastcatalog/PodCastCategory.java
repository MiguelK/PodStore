package com.podcastcatalog.model.podcastcatalog;

import java.util.List;

public class PodCastCategory extends BundleItem {

    private final List<PodCast> podCasts;

    private final PodCastCategoryType podCastCategoryType;

    public PodCastCategory(String title, String description,
                           String artworkUrl600, List<PodCast> podCasts, PodCastCategoryType podCastCategoryType) {
        super(title, description, artworkUrl600);
        this.podCasts = podCasts;
        this.podCastCategoryType = podCastCategoryType;
    }

    public PodCastCategoryType getPodCastCategoryType() {
        return podCastCategoryType;
    }

    public List<PodCast> getPodCasts() {
        return podCasts;
    }
}
