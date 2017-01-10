package com.podcastcatalog.model.podcastsearch;

public class PodCastResultItem extends ResultItem {


    public PodCastResultItem(String collectionId, String title, String artworkUrl100) {
        super(title, collectionId, artworkUrl100, ResultType.PODCAST);
    }

}
