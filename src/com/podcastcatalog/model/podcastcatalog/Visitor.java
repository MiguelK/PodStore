package com.podcastcatalog.model.podcastcatalog;

public interface Visitor {
    void visit(PodCast podCast);
    void visit(PodCastCategory podCastCategory);
    void visit(PodCastEpisode podCastEpisode);
}
