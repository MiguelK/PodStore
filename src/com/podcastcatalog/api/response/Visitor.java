package com.podcastcatalog.api.response;

public interface Visitor {
    void visit(PodCast podCast);
    void visit(PodCastCategory podCastCategory);
    void visit(PodCastEpisode podCastEpisode);
}
