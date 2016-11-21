package com.podcastcatalog.api.response.search;

import java.io.Serializable;

public class PodCastSearchResponse implements Serializable {

    private String collectionId;
    private String collectionName; //P3 Dokumentär
    private String artworkUrl100;
    private String feedUrl;
    private String description;

    public PodCastSearchResponse(String collectionId, String collectionName, String artworkUrl100,String feedUrl) {
        this.collectionId = collectionId;
        this.collectionName = collectionName;
        this.artworkUrl100 = artworkUrl100;
        this.feedUrl = feedUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }
}
