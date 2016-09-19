package com.podcastcatalog.builder.collector.okihika;

import com.podcastcatalog.api.response.PodCastEpisode;
import com.podcastcatalog.builder.collector.PodCastEpisodeCollector;

import java.util.List;

public class PodCastEpisodeCollectorOkihika extends PodCastCollectorOkihika implements PodCastEpisodeCollector {

    public PodCastEpisodeCollectorOkihika(PodCastCollectorOkihika.TopList toplist, int resultSize) {
        super(toplist, resultSize);
    }

    @Override
    public List<PodCastEpisode> collectPodCastEpisodes() {
        return null;
    }
}
