package com.podcastcatalog.modelbuilder.collector.itunes;


import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.modelbuilder.collector.PodCastFeedParser;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.RecursiveTask;

public class PodCastProcessor extends RecursiveTask<PodCast> {
    private static final long serialVersionUID = 1L;

    private final URL feedURL;
    private final String artworkUrl600;
    private final String collectionId;
    private final int maxEpisodes;


    PodCastProcessor(URL feedURL, String artworkUrl600, String collectionId, int maxEpisodes) {
        this.feedURL = feedURL;
        this.artworkUrl600 = artworkUrl600;
        this.collectionId = collectionId;
        this.maxEpisodes = maxEpisodes;
    }

    @Override
    protected PodCast compute() {
        Optional<PodCast> podCast = PodCastFeedParser.parse(feedURL, artworkUrl600, collectionId, maxEpisodes);
        return podCast.orElse(null);
    }

}