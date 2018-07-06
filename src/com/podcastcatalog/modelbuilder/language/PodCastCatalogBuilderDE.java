package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

public class PodCastCatalogBuilderDE extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {
        categoryContext.add(PodCastIdCollector.Category.VIDEO_GAMES, "Video");
        categoryContext.add(PodCastIdCollector.Category.NEWS_POLITICS, "Nachrichten und Politik");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "Musik");
        categoryContext.add(PodCastIdCollector.Category.DESIGN, "Kunst"); //DESIGN better then ARTS
        categoryContext.add(PodCastIdCollector.Category.HEALTH, "Gesundheit");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "Unternehmen");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Komödie");
        categoryContext.add(PodCastIdCollector.Category.K_12, "Ausbildung");
        categoryContext.add(PodCastIdCollector.Category.GAMES_HOBBIES, "Spiele & Hobby");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT_ORGANIZATIONS, "Behörden und Organisationen");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "Kinder & Familie");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Religion und Spiritualität");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE_MEDICINE, "Wissenschaft und Medizin");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Gesellschaft und Kultur");
        categoryContext.add(PodCastIdCollector.Category.SPORTS_RECREATION, "Sport und Freizeit");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Technologie");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "Fernsehen & Film");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.DE;
    }
}
