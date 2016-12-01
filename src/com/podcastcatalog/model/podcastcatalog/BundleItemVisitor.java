package com.podcastcatalog.model.podcastcatalog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
