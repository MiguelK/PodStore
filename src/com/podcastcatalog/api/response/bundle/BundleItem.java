package com.podcastcatalog.api.response.bundle;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public abstract class BundleItem implements Serializable{

    private final String title;
    private final String description;
    private final String artworkUrl100;//FIXME Rename artworkUrl100? itemImage100

    protected BundleItem(String title, String description, String artworkUrl100) {
        this.title = StringUtils.trimToNull(title);
        this.description = StringUtils.trimToNull(description);
        this.artworkUrl100 = StringUtils.trimToNull(artworkUrl100);

        if(this.title == null){
            throw  new IllegalArgumentException("title is requred");
        }
        if(this.description == null){
            throw  new IllegalArgumentException("description is requred");
        }
        if(this.artworkUrl100 == null){
            throw  new IllegalArgumentException("artworkUrl100 is requred");
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }
}
