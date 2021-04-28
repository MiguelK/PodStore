package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;

//Malaysia
public class PodCastCatalogBuilderMY extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {

        categoryContext.add(PodCastIdCollector.Category.NEWS, "Berita & Politik");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "Muzik");
        categoryContext.add(PodCastIdCollector.Category.DESIGN, "Seni");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "Perniagaan");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Komedi");
        categoryContext.add(PodCastIdCollector.Category.EDUCATION, "Pendidikan");
        categoryContext.add(PodCastIdCollector.Category.GAMES, "Permainan");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT, "Kerajaan & Organisasi");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "Kids & Family");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Kanak-kanak & Keluarga");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE, "Science_& Medicine");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Masyarakat & Budaya");
        categoryContext.add(PodCastIdCollector.Category.SPORTS, "Sukan");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Teknologi");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "TV & Filem");
        categoryContext.add(PodCastIdCollector.Category.TRUE_CRIME, "True Crime");

        categoryContext.add (PodCastIdCollector.Category.SOCCER, "Football");
        categoryContext.add (PodCastIdCollector.Category.RUNNING, "Running");
        categoryContext.add (PodCastIdCollector.Category.DAILY_NEWS, "Daily News");
        categoryContext.add (PodCastIdCollector.Category.TECH_NEWS, "Tech News");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.MY;
    }
}
