package com.podcastcatalog.model.podcastcatalog;

import java.util.List;

/**
 * Contains a list of {@link PodCastCategory}
 */
public class PodCastCategoryBundle extends Bundle {

    private final List<PodCastCategory> podCastCategories;

    public PodCastCategoryBundle(String title, String description,
                                 List<PodCastCategory> podCastCategories) {
        super(title, description, BundleType.Category);
        this.podCastCategories = podCastCategories;
    }

    @Override
    public List<PodCastCategory> getBundleItems() {
        return podCastCategories;
    }

    @Override
    public String toString() {
        return "PodCastCategoryBundle{" +
                "podCastCategories=" + podCastCategories.size() +
                '}';
    }
}
