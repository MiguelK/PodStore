package com.podcastcatalog.api.response.bundle;

import com.podcastcatalog.api.response.PodCastCategory;

import java.util.List;

/**
 * Contains a list of {@link PodCastCategory}
 */
public class PodCastCategoryBundle extends Bundle {

    private List<PodCastCategory> podCastCategories;

    public PodCastCategoryBundle(String title, String description,
                                 String imageURL,List<PodCastCategory> podCastCategories) {
        super(title, description, imageURL, BundleType.Category);
        this.podCastCategories = podCastCategories;
    }

    @Override
    public List<PodCastCategory> getBundleItems() {
        return podCastCategories;
    }
}
