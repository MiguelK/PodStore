package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

public class PodCastCatalogBuilderDE extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {
        categoryContext.add(PodCastCollectorOkihika.TopList.VIDEO_GAMES, "Video");
        categoryContext.add(PodCastCollectorOkihika.TopList.NEWS_POLITICS, "Nachrichten und Politik");
        categoryContext.add(PodCastCollectorOkihika.TopList.MUSIC, "Musik");
        categoryContext.add(PodCastCollectorOkihika.TopList.DESIGN, "Kunst"); //DESIGN better then ARTS
        categoryContext.add(PodCastCollectorOkihika.TopList.HEALTH, "Gesundheit");
        categoryContext.add(PodCastCollectorOkihika.TopList.BUSINESS, "Unternehmen");
        categoryContext.add(PodCastCollectorOkihika.TopList.COMEDY, "Komödie");
        categoryContext.add(PodCastCollectorOkihika.TopList.K_12, "Ausbildung");
        categoryContext.add(PodCastCollectorOkihika.TopList.GAMES_HOBBIES, "Spiele & Hobby");
        categoryContext.add(PodCastCollectorOkihika.TopList.GOVERNMENT_ORGANIZATIONS, "Behörden und Organisationen");
        categoryContext.add(PodCastCollectorOkihika.TopList.KIDS_FAMILY, "Kinder & Familie");
        categoryContext.add(PodCastCollectorOkihika.TopList.RELIGION_SPIRITUALITY, "Religion und Spiritualität");
        categoryContext.add(PodCastCollectorOkihika.TopList.SCIENCE_MEDICINE, "Wissenschaft und Medizin");
        categoryContext.add(PodCastCollectorOkihika.TopList.SOCIETY_CULTURE, "Gesellschaft und Kultur");
        categoryContext.add(PodCastCollectorOkihika.TopList.SPORTS_RECREATION, "Sport und Freizeit");
        categoryContext.add(PodCastCollectorOkihika.TopList.TECHNOLOGY, "Technologie");
        categoryContext.add(PodCastCollectorOkihika.TopList.TV_FILM, "Fernsehen & Film");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.DE;
    }
}
