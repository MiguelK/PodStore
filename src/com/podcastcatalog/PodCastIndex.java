package com.podcastcatalog;

import com.podcastcatalog.api.response.PodCast;

import java.util.*;

public class PodCastIndex {

    private Map<String, PodCast> index = new HashMap<>();

    private PodCastIndex() {
        index = new HashMap<>();
    }

    public static PodCastIndex create() {
        return new PodCastIndex();
    }

  /*  public PodCastIndex(List<PodCast> podCasts) {
        this.podCasts = Collections.unmodifiableList(podCasts);
    }*/


    public void buildIndex(List<PodCast> podCasts) {
        index = new HashMap<>(); //clear?

        podCasts.forEach(p -> index.put(p.getCollectionId(), p));
    }

    public Optional<PodCast> lookup(String id) {
        return Optional.ofNullable(index.get(id));
    }
}
