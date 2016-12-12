package com.podcastcatalog.service.podcastcatalog;

import com.podcastcatalog.model.podcastcatalog.PodCast;

import java.util.*;

public class PodCastIndex {

    private Map<String, PodCast> index = new HashMap<>();

    private PodCastIndex() {
        index = new HashMap<>();
    }

    public static PodCastIndex create() {
        return new PodCastIndex();
    }

    void buildIndex(List<PodCast> podCasts) {
        index = new HashMap<>(); //clear?

        podCasts.forEach(p -> index.put(p.getCollectionId(), p));
    }

    public Optional<PodCast> lookup(String id) {
        return Optional.ofNullable(index.get(id));
    }

    public String getStatus() {
        return "PodCastIndex: Size=" + index.size();
    }

    public void update(PodCast podCast) {

        index.put(podCast.getCollectionId(),podCast);
    }
}
