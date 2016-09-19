package com.podcastcatalog.builder.collector;

import com.podcastcatalog.api.response.PodCast;

import java.util.List;

public interface PodCastCollector {
    List<PodCast> collect();
}
