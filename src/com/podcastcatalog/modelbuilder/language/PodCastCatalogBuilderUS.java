package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

//XML or Database...
public class PodCastCatalogBuilderUS extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {

        categoryContext.add(PodCastCollectorOkihika.TopList.NEWS_POLITICS, "News & Politics");
        categoryContext.add(PodCastCollectorOkihika.TopList.MUSIC, "Music");
        categoryContext.add(PodCastCollectorOkihika.TopList.ARTS, "Art");
        categoryContext.add(PodCastCollectorOkihika.TopList.BUSINESS, "Business");
        categoryContext.add(PodCastCollectorOkihika.TopList.COMEDY, "Comedy");
        categoryContext.add(PodCastCollectorOkihika.TopList.K_12, "Education");
        categoryContext.add(PodCastCollectorOkihika.TopList.GAMES_HOBBIES, "Games");
        categoryContext.add(PodCastCollectorOkihika.TopList.GOVERNMENT_ORGANIZATIONS, "Government & Organizations");
        categoryContext.add(PodCastCollectorOkihika.TopList.KIDS_FAMILY, "Kids & Family");
        categoryContext.add(PodCastCollectorOkihika.TopList.RELIGION_SPIRITUALITY, "Religion & Spirituality");
        categoryContext.add(PodCastCollectorOkihika.TopList.SCIENCE_MEDICINE, "Science_& Medicine");
        categoryContext.add(PodCastCollectorOkihika.TopList.SOCIETY_CULTURE, "Society & Culture");
        categoryContext.add(PodCastCollectorOkihika.TopList.SPORTS_RECREATION, "Sports");
        categoryContext.add(PodCastCollectorOkihika.TopList.TECHNOLOGY, "Technology");
        categoryContext.add(PodCastCollectorOkihika.TopList.TV_FILM, "TV & Film");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.US;
    }
}
