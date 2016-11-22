package com.podcastcatalog.api.response.search;

public class PodCastEpisodeSearchResult {
    private final String title;
    private final String description;
    private final String podCastCollectionId;

    public PodCastEpisodeSearchResult(String title, String description, String podCastCollectionId) {
        this.title = title;
        this.description = description;
        this.podCastCollectionId = podCastCollectionId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPodCastCollectionId() {
        return podCastCollectionId;
    }
}
