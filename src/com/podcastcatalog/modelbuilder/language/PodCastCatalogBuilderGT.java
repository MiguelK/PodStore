package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;

//Guatemala GT
public class PodCastCatalogBuilderGT extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {

        categoryContext.add(PodCastIdCollector.Category.NEWS, "Noticias");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "Música");
        categoryContext.add(PodCastIdCollector.Category.DESIGN, "Arte");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "Economía");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Comedia");
        categoryContext.add(PodCastIdCollector.Category.EDUCATION, "Educación");
        //categoryContext.add(PodCastIdCollector.Category.GAMES, "Juegos");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT, "Gobierno");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, " Para toda la familia");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Religion & Spirituality");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE, "Ciencia");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Ciencia & Culture");
        categoryContext.add(PodCastIdCollector.Category.SPORTS, "Sports");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Tecnología");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "Cine y TV");
        categoryContext.add(PodCastIdCollector.Category.TRUE_CRIME, "Crímenes reales");

        categoryContext.add (PodCastIdCollector.Category.SOCCER, "Football");
        categoryContext.add (PodCastIdCollector.Category.RUNNING, "Running");
        categoryContext.add (PodCastIdCollector.Category.DAILY_NEWS, "Daily News");
        categoryContext.add (PodCastIdCollector.Category.TECH_NEWS, "Tech News");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.GT;
    }
}
