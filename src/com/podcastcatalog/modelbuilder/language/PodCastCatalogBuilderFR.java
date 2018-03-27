package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

public class PodCastCatalogBuilderFR extends PodCastCatalogBuilderBase {

    @Override
    void addCategoryNames(CategoryContext categoryContext) {
        categoryContext.add(PodCastCollectorOkihika.TopList.VIDEO_GAMES, "vidéo");
        categoryContext.add(PodCastCollectorOkihika.TopList.NEWS_POLITICS, "Nouvelles et politique");
        categoryContext.add(PodCastCollectorOkihika.TopList.MUSIC, "Musique");
        categoryContext.add(PodCastCollectorOkihika.TopList.DESIGN, "Art"); //DESIGN better then ARTS
        categoryContext.add(PodCastCollectorOkihika.TopList.HEALTH, "Santé");
        categoryContext.add(PodCastCollectorOkihika.TopList.BUSINESS, "Affaires");
        categoryContext.add(PodCastCollectorOkihika.TopList.COMEDY, "Comédie");
        categoryContext.add(PodCastCollectorOkihika.TopList.K_12, "Formation");
        categoryContext.add(PodCastCollectorOkihika.TopList.GAMES_HOBBIES, "Jeux et passe-temps");
        categoryContext.add(PodCastCollectorOkihika.TopList.GOVERNMENT_ORGANIZATIONS, "Autorités et organisations");
        categoryContext.add(PodCastCollectorOkihika.TopList.KIDS_FAMILY, "Enfants et famille");
        categoryContext.add(PodCastCollectorOkihika.TopList.RELIGION_SPIRITUALITY, "Religion et spiritualité");
        categoryContext.add(PodCastCollectorOkihika.TopList.SCIENCE_MEDICINE, "Science et médecine");
        categoryContext.add(PodCastCollectorOkihika.TopList.SOCIETY_CULTURE, "Société et culture");
        categoryContext.add(PodCastCollectorOkihika.TopList.SPORTS_RECREATION, "Sports et loisirs");
        categoryContext.add(PodCastCollectorOkihika.TopList.TECHNOLOGY, "Technologie");
        categoryContext.add(PodCastCollectorOkihika.TopList.TV_FILM, "TV et film");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.FR;
    }
}
