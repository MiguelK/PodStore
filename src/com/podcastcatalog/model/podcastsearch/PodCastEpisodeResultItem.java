package com.podcastcatalog.model.podcastsearch;

import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;

public class PodCastEpisodeResultItem extends ResultItem {

    private final String id;

    public PodCastEpisodeResultItem(PodCastEpisode podCastEpisode) {
        super(podCastEpisode.getTitle(), podCastEpisode.getPodCastCollectionId(), podCastEpisode.getArtworkUrl600(), ResultType.EPISODE);
        this.id = podCastEpisode.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PodCastEpisodeResultItem that = (PodCastEpisodeResultItem) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
