package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

public class PodCastCatalogBuilderDE extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {
        categoryContext.add(PodCastIdCollector.Category.VIDEO_GAMES, "Video");
        categoryContext.add(PodCastIdCollector.Category.NEWS, "Nachrichten und Politik");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "Musik");
        categoryContext.add(PodCastIdCollector.Category.DESIGN, "Kunst"); //DESIGN better then ARTS
        categoryContext.add(PodCastIdCollector.Category.HEALTH_FITNESS, "Gesundheit");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "Unternehmen");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Komödie");
        categoryContext.add(PodCastIdCollector.Category.EDUCATION, "Ausbildung");
        categoryContext.add(PodCastIdCollector.Category.GAMES, "Spiele & Hobby");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT, "Behörden und Organisationen");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "Kinder & Familie");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Religion und Spiritualität");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE, "Wissenschaft und Medizin");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Gesellschaft und Kultur");
        categoryContext.add(PodCastIdCollector.Category.SPORTS, "Sport und Freizeit");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Technologie");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "Fernsehen & Film");
        categoryContext.add(PodCastIdCollector.Category.TRUE_CRIME, "True Crime");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.DE;
    }
}
