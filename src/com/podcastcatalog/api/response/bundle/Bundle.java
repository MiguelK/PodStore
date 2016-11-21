package com.podcastcatalog.api.response.bundle;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * A container class for a list of @{@link BundleItem }
 */
public abstract class Bundle implements Serializable  {

    private final String title; //Not used e.g "Alla Kategorier" //FIXME Remove?
    private final String description; //Not used //FIXME Remove?
    private final String imageURL; //Not used ? //FIXME Remove?
    private final BundleType bundleType;

    Bundle(String title, String description, String imageURL, BundleType bundleType) {
        this.title = title;
        this.description = description;
        this.imageURL = StringUtils.trim(imageURL);
        this.bundleType = bundleType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public abstract List<? extends BundleItem> getBundleItems();

    public BundleType getBundleType() {
        return bundleType;
    }

    public abstract String toString();

}
