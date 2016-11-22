package com.podcastcatalog.api.response.search;

import java.io.Serializable;

public class PodCastSearchResponse implements Serializable {

    private final String collectionId;
    private final String title; //P3 Dokumentär
    private final String artworkUrl100;
    private final String feedUrl;

    public PodCastSearchResponse(String collectionId, String title, String artworkUrl100, String feedUrl) {
        this.collectionId = collectionId;
        this.title = title;
        this.artworkUrl100 = artworkUrl100;
        this.feedUrl = feedUrl;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }
}
