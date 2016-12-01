package com.podcastcatalog.model.podcastsearch;

public class PodCastResultItem extends ResultItem {

    private final String artworkUrl100;

    public PodCastResultItem(String collectionId, String title, String artworkUrl100) {
        super(title, collectionId, ResultType.PODCAST);
        this.artworkUrl100 = artworkUrl100;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }
}
