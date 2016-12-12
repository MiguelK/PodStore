package com.podcastcatalog.model.podcaststatus;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;

import java.util.HashMap;
import java.util.Map;

public class PodCastStatus {

    private final Map<String, PodCast> podCastById;
    private final Map<String, String> podCastIdLatestEpisodeId; //Client can check,if different the later episode exist

    public PodCastStatus(Map<String, PodCast> podCastById) {
        this.podCastById = podCastById;


        podCastIdLatestEpisodeId = new HashMap<>();
        for (PodCast podCast : podCastById.values()) {
            PodCastEpisode latestPodCastEpisode = podCast.getLatestPodCastEpisode();
            podCastIdLatestEpisodeId.put(podCast.getCollectionId(),latestPodCastEpisode.getId());
        }


    }

    public Map<String, String> getPodCastIdLatestEpisodeId() {
        return podCastIdLatestEpisodeId;
    }

    public Map<String, PodCast> getPodCastById() {
        return podCastById;
    }
}
