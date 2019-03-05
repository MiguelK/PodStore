package com.podcastcatalog.model.podcastsearch;

import java.io.Serializable;
import java.util.Comparator;

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

    public static Comparator<ResultItem> SORT_BY_POD_CAST_NAME = new Comparator<ResultItem>() {
        @Override
        public int compare(ResultItem o1, ResultItem o2) {

            boolean isPodCastResult1 = o1 instanceof PodCastResultItem;
            boolean isPodCastResult2 = o2 instanceof PodCastResultItem;
            if (isPodCastResult1 && isPodCastResult2) {
                PodCastResultItem podCast1 = (PodCastResultItem) o1;
                PodCastResultItem podCast2 = (PodCastResultItem) o2;
                return podCast1.getTitle().compareTo(podCast2.getTitle());
            }

            if (isPodCastResult1 || isPodCastResult2) {
                return -1;
            }


            return 0;
        }
    };

}
