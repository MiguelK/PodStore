package com.podcastcatalog.builder.collector;

import com.podcastcatalog.api.response.PodCastEpisode;

import java.util.List;

public interface PodCastEpisodeCollector {
    List<PodCastEpisode> collectEpisodes();
}
