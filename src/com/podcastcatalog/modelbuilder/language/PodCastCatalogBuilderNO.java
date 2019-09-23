package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

public class PodCastCatalogBuilderNO extends PodCastCatalogBuilderBase {

    @Override
    void addCategoryNames(CategoryContext categoryContext) {
        categoryContext.add(PodCastIdCollector.Category.VIDEO_GAMES, "video");
        categoryContext.add(PodCastIdCollector.Category.NEWS, "Nyheter og politikk");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "Musikk");
        categoryContext.add(PodCastIdCollector.Category.DESIGN, "Kunst"); //DESIGN better then ARTS
        categoryContext.add(PodCastIdCollector.Category.HEALTH_FITNESS, "Helse");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "Forretnings");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Komedie");
        categoryContext.add(PodCastIdCollector.Category.EDUCATION, "Trening");
        categoryContext.add(PodCastIdCollector.Category.GAMES, "Spill og hobby");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT, "Myndigheter og organisasjoner");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "Barn og familie");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Religion og åndelighet");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE, "Vitenskap og medisin");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Samfunn og kultur");
        categoryContext.add(PodCastIdCollector.Category.SPORTS, "Sport og fritid");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Teknologi");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "TV og film");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.NO;
    }
}
