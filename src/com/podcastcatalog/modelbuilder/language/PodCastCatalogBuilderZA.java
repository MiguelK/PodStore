package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;

// //South Africa
public class PodCastCatalogBuilderZA extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {

        categoryContext.add (PodCastIdCollector.Category.NEWS, "Izindaba nezepolitiki");
        categoryContext.add (PodCastIdCollector.Category.MUSIC, "Music");
        categoryContext.add (PodCastIdCollector.Category.DESIGN, "Art");
        categoryContext.add (PodCastIdCollector.Category.BUSINESS, "Business");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Comedy");
        categoryContext.add(PodCastIdCollector.Category.EDUCATION, "Education");
        categoryContext.add(PodCastIdCollector.Category.GAMES, "Games");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT, "Uhulumeni nezinhlangano");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "Izingane Nomndeni");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Religion & Spirituality");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE, "Science_& Medicine");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Society & Culture");
        categoryContext.add(PodCastIdCollector.Category.SPORTS, "Sports");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Technology");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "TV & Film");
        categoryContext.add(PodCastIdCollector.Category.TRUE_CRIME, "True Crime");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.ZA;
    }
}
