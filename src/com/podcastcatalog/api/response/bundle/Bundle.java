package com.podcastcatalog.api.response.bundle;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public abstract class Bundle implements Serializable  {

    private final String title;
    private final String description;
    private final String imageURL; //FIXME Many???

    private final BundleType bundleType;

    public Bundle(String title, String description, String imageURL, BundleType bundleType) {
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

    public BundleType getBundleType() {
        return bundleType;
    }

}
