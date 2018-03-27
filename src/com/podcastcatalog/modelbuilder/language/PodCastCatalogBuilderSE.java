package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

//XML or Database...
public class PodCastCatalogBuilderSE extends PodCastCatalogBuilderBase {

    @Override
    void addCategoryNames(CategoryContext categoryContext) {
        categoryContext.add(PodCastCollectorOkihika.TopList.VIDEO_GAMES, "Videospel");
        categoryContext.add(PodCastCollectorOkihika.TopList.NEWS_POLITICS, "Nyheter & politik");
        categoryContext.add(PodCastCollectorOkihika.TopList.MUSIC, "Musik");
        categoryContext.add(PodCastCollectorOkihika.TopList.DESIGN, "Konst"); //DESIGN better then ARTS
        categoryContext.add(PodCastCollectorOkihika.TopList.HEALTH, "Hälsa");
        categoryContext.add(PodCastCollectorOkihika.TopList.BUSINESS, "Näringsliv");
        categoryContext.add(PodCastCollectorOkihika.TopList.COMEDY, "Komedi");
        categoryContext.add(PodCastCollectorOkihika.TopList.K_12, "Utbildning");
        categoryContext.add(PodCastCollectorOkihika.TopList.GAMES_HOBBIES, "Spel & hobby");
        categoryContext.add(PodCastCollectorOkihika.TopList.GOVERNMENT_ORGANIZATIONS, "Myndigheter & organisationer");
        categoryContext.add(PodCastCollectorOkihika.TopList.KIDS_FAMILY, "Barn & familj");
        categoryContext.add(PodCastCollectorOkihika.TopList.RELIGION_SPIRITUALITY, "Religion & andlighet");
        categoryContext.add(PodCastCollectorOkihika.TopList.SCIENCE_MEDICINE, "Vetenskap & medicin");
        categoryContext.add(PodCastCollectorOkihika.TopList.SOCIETY_CULTURE, "Samhälle & kultur");
        categoryContext.add(PodCastCollectorOkihika.TopList.SPORTS_RECREATION, "Sport & fritid");
        categoryContext.add(PodCastCollectorOkihika.TopList.TECHNOLOGY, "Teknologi");
        categoryContext.add(PodCastCollectorOkihika.TopList.TV_FILM, "TV & film");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.SE;
    }
}
