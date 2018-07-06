package com.podcastcatalog.modelbuilder.collector.okihika;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.PodCastCategoryCollector;
import com.podcastcatalog.model.podcastcatalog.PodCastCategory;
import com.podcastcatalog.model.podcastcatalog.PodCastCategoryType;

import java.util.List;

public class PodCastCategoryCollectorOkihika extends PodCastCollectorOkihika implements PodCastCategoryCollector {

    private static final int DEFAULT_RESULT_SIZE = 25;
    private final String title;
    private final String description;
    private final PodCastCategoryType podCastCategoryType;

    //Only for test FIXME remove
    public PodCastCategoryCollectorOkihika(TopList toplist, int resultSize, String title, String description) {
        super(PodCastCatalogLanguage.SE, toplist, resultSize);
        this.title = title;
        this.description = description;
        this.podCastCategoryType = toplist.toPodCastCategoryType();
    }

    private PodCastCategoryCollectorOkihika(PodCastCatalogLanguage language, TopList toplist, String title, String description) {
        super(language,toplist, DEFAULT_RESULT_SIZE);
        this.title = title;
        this.description = description;
        this.podCastCategoryType = toplist.toPodCastCategoryType();
    }

    public static PodCastCategoryCollectorOkihika create(PodCastCatalogLanguage lang, TopList toplist, String title){
        return new PodCastCategoryCollectorOkihika(lang, toplist, title, "????");
    }


    public static PodCastCategoryCollectorOkihika createSWE(TopList toplist, String title){
        return new PodCastCategoryCollectorOkihika(PodCastCatalogLanguage.SE, toplist, title, "????");
    }

    public static PodCastCategoryCollectorOkihika createUS(TopList toplist, String title){
        return new PodCastCategoryCollectorOkihika(PodCastCatalogLanguage.US, toplist, title, "????");
    }

    @Override
    public PodCastCategory collectCategories() {
        List<PodCast> podCasts = getPodCasts();

        String artworkUrl600 = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/Color_icon_red.svg/220px-Color_icon_red.svg.png";//FIXME Default error image?

        if (!podCasts.isEmpty()) {
            artworkUrl600 = podCasts.get(0).getArtworkUrl600();
        }

        return new PodCastCategory(title, "", artworkUrl600, podCasts, podCastCategoryType);
    }
}
