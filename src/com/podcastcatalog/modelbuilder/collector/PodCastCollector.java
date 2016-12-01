package com.podcastcatalog.modelbuilder.collector;

import com.podcastcatalog.model.podcastcatalog.PodCast;

import java.util.List;

public interface PodCastCollector {
    List<PodCast> collectPodCasts();
}
