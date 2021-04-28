package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;

public class PodCastCatalogBuilderDK extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {

        categoryContext.add(PodCastIdCollector.Category.NEWS, "Nyheder & politik");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "Musik");
        categoryContext.add(PodCastIdCollector.Category.DESIGN, "Kunst");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "Forretning");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Komedie");
        categoryContext.add(PodCastIdCollector.Category.EDUCATION, "Uddannelse");
        categoryContext.add(PodCastIdCollector.Category.GAMES, "Spil");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT, "Regering & organisationer");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "Kids & Family");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Religion & åndelighed");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE, "Videnskab & medicin");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Samfund & kultur");
        categoryContext.add(PodCastIdCollector.Category.SPORTS, "Sport");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Teknologi");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "TV & Film");
        categoryContext.add(PodCastIdCollector.Category.TRUE_CRIME, "True Crime");

        categoryContext.add (PodCastIdCollector.Category.SOCCER, "Football");
        categoryContext.add (PodCastIdCollector.Category.RUNNING, "Running");
        categoryContext.add (PodCastIdCollector.Category.DAILY_NEWS, "Daily News");
        categoryContext.add (PodCastIdCollector.Category.TECH_NEWS, "Tech News");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.DK;
    }
}
