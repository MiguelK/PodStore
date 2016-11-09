package com.podcastcatalog.api.response.bundle;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public abstract class BundleItem implements Serializable{

    private final String title;
    private final String description;
    private final String imageURL;

    protected BundleItem(String title, String description, String imageURL) {
        this.title = StringUtils.trimToNull(title);
        this.description = StringUtils.trimToNull(description);
        this.imageURL = StringUtils.trimToNull(imageURL);

        if(this.title == null){
            throw  new IllegalArgumentException("title is requred");
        }
        if(this.description == null){
            throw  new IllegalArgumentException("description is requred");
        }
        if(this.imageURL == null){
            throw  new IllegalArgumentException("imageURL is requred");
        }
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
}
