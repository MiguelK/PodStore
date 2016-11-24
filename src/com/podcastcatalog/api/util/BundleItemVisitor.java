package com.podcastcatalog.api.util;

import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.PodCastEpisode;
import com.podcastcatalog.api.response.Visitor;

import java.util.*;

public class BundleItemVisitor implements Visitor{

    private final Set<PodCastEpisode> podCastEpisodes = new HashSet<>();
    private final Set<PodCast> podCasts = new HashSet<>();

    @Override
    public void visit(PodCast podCast) {
        podCastEpisodes.addAll(podCast.getPodCastEpisodes());
        podCasts.add(podCast);
    }

    @Override
    public void visit(PodCastCategory podCastCategory) {

        for (PodCast podCast : podCastCategory.getPodCasts()) {
            podCastEpisodes.addAll(podCast.getPodCastEpisodes());
            podCasts.add(podCast);
        }
    }

    @Override
    public void visit(PodCastEpisode podCastEpisode) {
        podCastEpisodes.add(podCastEpisode);

    }

    public List<PodCastEpisode> getPodCastEpisodes() {
        return new ArrayList<>(podCastEpisodes);
    }

    public List<PodCast> getPodCasts() {
        return new ArrayList<>(podCasts);
    }
}
