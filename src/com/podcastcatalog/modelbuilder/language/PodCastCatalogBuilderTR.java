package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;

//Turkey TR
public class PodCastCatalogBuilderTR extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {

        categoryContext.add (PodCastIdCollector.Category.NEWS, "Haberler ve Politika");
        categoryContext.add (PodCastIdCollector.Category.MUSIC, "Müzik");
        categoryContext.add (PodCastIdCollector.Category.DESIGN, "Sanat");
        categoryContext.add (PodCastIdCollector.Category.BUSINESS, "Business");
        categoryContext.add (PodCastIdCollector.Category.COMEDY, "Komedi");
        categoryContext.add (PodCastIdCollector.Category.EDUCATION, "Eğitim");
        categoryContext.add (PodCastIdCollector.Category.GAMES, "Oyunlar");
        categoryContext.add (PodCastIdCollector.Category.GOVERNMENT, "Devlet ve Kuruluşlar");
        categoryContext.add (PodCastIdCollector.Category.KIDS_FAMILY, "Çocuklar ve Aile");
        categoryContext.add (PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Din ve Maneviyat");
        categoryContext.add (PodCastIdCollector.Category.SCIENCE, "Science_ & Medicine");
        categoryContext.add (PodCastIdCollector.Category.SOCIETY_CULTURE, "Toplum ve Kültür");
        categoryContext.add (PodCastIdCollector.Category.SPORTS, "Spor");
        categoryContext.add (PodCastIdCollector.Category.TECHNOLOGY, "Teknoloji");
        categoryContext.add (PodCastIdCollector.Category.TV_FILM, "TV ve Film");
        categoryContext.add (PodCastIdCollector.Category.TRUE_CRIME, "Gerçek Suç");

        categoryContext.add (PodCastIdCollector.Category.SOCCER, "Football");
        categoryContext.add (PodCastIdCollector.Category.RUNNING, "Running");
        categoryContext.add (PodCastIdCollector.Category.DAILY_NEWS, "Daily News");
        categoryContext.add (PodCastIdCollector.Category.TECH_NEWS, "Tech News");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.TR;
    }
}
