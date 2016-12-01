package com.podcastcatalog.model.podcastsearch;

public class PodCastEpisodeResultItem extends ResultItem {
    private final String description;
    private final String targetURL;

    public PodCastEpisodeResultItem(String title, String description, String podCastCollectionId, String targetURL) {
        super(title, podCastCollectionId, ResultType.EPISODE);
        this.description = description;
        this.targetURL = targetURL;
    }

    public String getDescription() {
        return description;
    }

    public String getTargetURL() {
        return targetURL;
    }
}
