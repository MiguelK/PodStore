package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;

//Finland
public class PodCastCatalogBuilderFI extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {

        categoryContext.add(PodCastIdCollector.Category.NEWS_POLITICS, "Uutiset ja politiikka");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "Musiikki");
        categoryContext.add(PodCastIdCollector.Category.ARTS, "Art");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "Business");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Komedia");
        categoryContext.add(PodCastIdCollector.Category.K_12, "Koulutus");
        categoryContext.add(PodCastIdCollector.Category.GAMES_HOBBIES, "Pelit");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT_ORGANIZATIONS, "Hallinto ja järjestöt");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "Lapset ja perhe");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Uskonto ja hengellisyys");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE_MEDICINE, "Science_ & Medicine");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Yhteiskunta ja kulttuuri");
        categoryContext.add(PodCastIdCollector.Category.SPORTS_RECREATION, "Urheilu");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Technology");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "TV & Film");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.FI;
    }
}
