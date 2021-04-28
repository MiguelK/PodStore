package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;

//Finland
public class PodCastCatalogBuilderFI extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {

        categoryContext.add(PodCastIdCollector.Category.NEWS, "Uutiset ja politiikka");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "Musiikki");
        categoryContext.add(PodCastIdCollector.Category.DESIGN, "Art");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "Business");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Komedia");
        categoryContext.add(PodCastIdCollector.Category.EDUCATION, "Koulutus");
        categoryContext.add(PodCastIdCollector.Category.GAMES, "Pelit");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT, "Hallinto ja järjestöt");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "Lapset ja perhe");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Uskonto ja hengellisyys");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE, "Science_ & Medicine");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Yhteiskunta ja kulttuuri");
        categoryContext.add(PodCastIdCollector.Category.SPORTS, "Urheilu");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Technology");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "TV & Film");
        categoryContext.add(PodCastIdCollector.Category.TRUE_CRIME, "True Crime");

        categoryContext.add (PodCastIdCollector.Category.SOCCER, "Football");
        categoryContext.add (PodCastIdCollector.Category.RUNNING, "Running");
        categoryContext.add (PodCastIdCollector.Category.DAILY_NEWS, "Daily News");
        categoryContext.add (PodCastIdCollector.Category.TECH_NEWS, "Tech News");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.FI;
    }
}
