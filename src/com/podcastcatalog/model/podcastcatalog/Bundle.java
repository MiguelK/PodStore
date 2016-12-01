package com.podcastcatalog.model.podcastcatalog;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * A container class for a list of @{@link BundleItem }
 */
public abstract class Bundle implements Serializable  {

    private final String title; //Not used e.g "Alla Kategorier" //FIXME Remove?
    private final String description; //Not used //FIXME Remove?
    private final BundleType bundleType;

    Bundle(String title, String description, BundleType bundleType) {
        this.title = StringUtils.trimToNull(title);
        this.description = StringUtils.trimToNull(description);
        this.bundleType = bundleType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public abstract List<? extends BundleItem> getBundleItems();

    public BundleType getBundleType() {
        return bundleType;
    }

    public abstract String toString();

}
