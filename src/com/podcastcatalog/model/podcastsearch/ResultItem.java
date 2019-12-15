package com.podcastcatalog.model.podcastsearch;

import java.io.Serializable;

public abstract class ResultItem implements Serializable {

    private final String title;
    private final String podCastCollectionId;
    private final ResultType resultType;
    private final String artworkUrl100;

    ResultItem(String title, String podCastCollectionId, String artworkUrl100, ResultType resultType) {
        this.title = title;
        this.podCastCollectionId = podCastCollectionId;
        this.resultType = resultType;
        this.artworkUrl100 = artworkUrl100;
    }

    public String getTitle() {
        return title;
    }

    public String getPodCastCollectionId() {
        return podCastCollectionId;
    }

    public ResultType getResultType() {
        return resultType;
    }
    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResultItem that = (ResultItem) o;

        return podCastCollectionId.equals(that.podCastCollectionId);
    }

    @Override
    public int hashCode() {
        return podCastCollectionId.hashCode();
    }


}
