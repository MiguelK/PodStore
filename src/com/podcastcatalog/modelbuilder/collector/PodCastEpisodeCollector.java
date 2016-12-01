package com.podcastcatalog.modelbuilder.collector;

import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;

import java.util.List;

public interface PodCastEpisodeCollector {
    List<PodCastEpisode> collectEpisodes();
}
