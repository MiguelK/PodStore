package com.podcastcatalog.api.response.search;

import java.io.Serializable;

public abstract class ResultItem implements Serializable {

    private final String title;
    private final String podCastCollectionId;
    private final ResultType resultType;

    ResultItem(String title, String podCastCollectionId, ResultType resultType) {
        this.title = title;
        this.podCastCollectionId = podCastCollectionId;
        this.resultType = resultType;
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
}
