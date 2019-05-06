package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;

public class PodCastCatalogBuilderDK extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {

        categoryContext.add(PodCastIdCollector.Category.NEWS_POLITICS, "Nyheder & politik");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "Musik");
        categoryContext.add(PodCastIdCollector.Category.ARTS, "Kunst");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "Forretning");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Komedie");
        categoryContext.add(PodCastIdCollector.Category.K_12, "Uddannelse");
        categoryContext.add(PodCastIdCollector.Category.GAMES_HOBBIES, "Spil");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT_ORGANIZATIONS, "Regering & organisationer");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "Kids & Family");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Religion & åndelighed");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE_MEDICINE, "Videnskab & medicin");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Samfund & kultur");
        categoryContext.add(PodCastIdCollector.Category.SPORTS_RECREATION, "Sport");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Teknologi");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "TV & Film");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.DK;
    }
}
