package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

public class PodCastCatalogBuilderNO extends PodCastCatalogBuilderBase {

    @Override
    void addCategoryNames(CategoryContext categoryContext) {
        categoryContext.add(PodCastCollectorOkihika.TopList.VIDEO_GAMES, "video");
        categoryContext.add(PodCastCollectorOkihika.TopList.NEWS_POLITICS, "Nyheter og politikk");
        categoryContext.add(PodCastCollectorOkihika.TopList.MUSIC, "Musikk");
        categoryContext.add(PodCastCollectorOkihika.TopList.DESIGN, "Kunst"); //DESIGN better then ARTS
        categoryContext.add(PodCastCollectorOkihika.TopList.HEALTH, "Helse");
        categoryContext.add(PodCastCollectorOkihika.TopList.BUSINESS, "Forretnings");
        categoryContext.add(PodCastCollectorOkihika.TopList.COMEDY, "Komedie");
        categoryContext.add(PodCastCollectorOkihika.TopList.K_12, "Trening");
        categoryContext.add(PodCastCollectorOkihika.TopList.GAMES_HOBBIES, "Spill og hobby");
        categoryContext.add(PodCastCollectorOkihika.TopList.GOVERNMENT_ORGANIZATIONS, "Myndigheter og organisasjoner");
        categoryContext.add(PodCastCollectorOkihika.TopList.KIDS_FAMILY, "Barn og familie");
        categoryContext.add(PodCastCollectorOkihika.TopList.RELIGION_SPIRITUALITY, "Religion og åndelighet");
        categoryContext.add(PodCastCollectorOkihika.TopList.SCIENCE_MEDICINE, "Vitenskap og medisin");
        categoryContext.add(PodCastCollectorOkihika.TopList.SOCIETY_CULTURE, "Samfunn og kultur");
        categoryContext.add(PodCastCollectorOkihika.TopList.SPORTS_RECREATION, "Sport og fritid");
        categoryContext.add(PodCastCollectorOkihika.TopList.TECHNOLOGY, "Teknologi");
        categoryContext.add(PodCastCollectorOkihika.TopList.TV_FILM, "TV og film");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.NO;
    }
}
