package com.podcastcatalog.builder.collector.okihika;

import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.builder.collector.PodCastCategoryCollector;
import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.PodCastCategoryType;

import java.util.List;

public class PodCastCategoryCollectorOkihika extends PodCastCollectorOkihika implements PodCastCategoryCollector {

    private static final int DEFAULT_RESULT_SIZE = 25;
    private final String title;
    private final String description;
    private final PodCastCategoryType podCastCategoryType;

    public PodCastCategoryCollectorOkihika(TopList toplist, int resultSize, String title, String description) {
        super(toplist, resultSize);
        this.title = title;
        this.description = description;
        this.podCastCategoryType = toplist.toPodCastCategoryType();
    }

    public PodCastCategoryCollectorOkihika(TopList toplist, String title, String description) {
        super(toplist, DEFAULT_RESULT_SIZE);
        this.title = title;
        this.description = description;
        this.podCastCategoryType = toplist.toPodCastCategoryType();
    }

    @Override
    public PodCastCategory collectCategories() {
        List<PodCast> podCasts = getPodCasts();

        String artworkUrl100 = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/Color_icon_red.svg/220px-Color_icon_red.svg.png";//FIXME Default error image?

        if (!podCasts.isEmpty()) {
            artworkUrl100 = podCasts.get(0).getArtworkUrl100();
        }

        return new PodCastCategory(title, description, artworkUrl100, podCasts, podCastCategoryType);
    }
}
