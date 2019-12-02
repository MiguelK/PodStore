package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;

//XML or Database...
public class PodCastCatalogBuilderSE extends PodCastCatalogBuilderBase {

    @Override
    void addCategoryNames(CategoryContext categoryContext) {
        categoryContext.add(PodCastIdCollector.Category.VIDEO_GAMES, "Videospel");
        categoryContext.add(PodCastIdCollector.Category.NEWS, "Nyheter & politik");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "Musik");
        categoryContext.add(PodCastIdCollector.Category.DESIGN, "Konst"); //DESIGN better then ARTS
        categoryContext.add(PodCastIdCollector.Category.HEALTH_FITNESS, "Hälsa");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "Näringsliv");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Komedi");
        categoryContext.add(PodCastIdCollector.Category.EDUCATION, "Utbildning");
        categoryContext.add(PodCastIdCollector.Category.GAMES, "Spel & hobby");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT, "Myndigheter & organisationer");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "Barn & familj");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Religion & andlighet");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE, "Vetenskap & medicin");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Samhälle & kultur");
        categoryContext.add(PodCastIdCollector.Category.SPORTS, "Sport & fritid");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Teknologi");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "TV & film");
        categoryContext.add(PodCastIdCollector.Category.TRUE_CRIME, "True Crime");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.SE;
    }
}
