package com.podcastcatalog.api.response;

import com.podcastcatalog.api.response.bundle.BundleItem;

import java.util.List;

public class PodCastCategory extends BundleItem {

    private final List<PodCast> podCasts;

    private final PodCastCategoryType podCastCategoryType;

    public PodCastCategory(String title, String description,
                           String artworkUrl100, List<PodCast> podCasts, PodCastCategoryType podCastCategoryType) {
        super(title, description, artworkUrl100);
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
