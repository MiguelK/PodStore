package com.podcastcatalog.api.response;


import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public abstract class BundleItem implements Serializable, Visitable {

    private final String title;
    private final String description;
    private final String artworkUrl600;//Mandatory for PodCast and Category

    BundleItem(String title, String description, String artworkUrl600) {
        this.title = StringUtils.trimToNull(title);
        this.description = StringUtils.trimToNull(description);
        this.artworkUrl600 = StringUtils.trimToNull(artworkUrl600);

        if (this.title == null) {
            throw new IllegalArgumentException("title is null");
        }
        if (this.description == null) {
            throw new IllegalArgumentException("description is null, title=" + title);
        }

        if (artworkUrl600 != null && !artworkUrl600.startsWith("http")) {
            throw new IllegalArgumentException("Invalid artworkUrl600 " + artworkUrl600);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getArtworkUrl600() {
        return artworkUrl600;
    }
}
