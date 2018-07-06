package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

public class PodCastCatalogBuilderFR extends PodCastCatalogBuilderBase {

    @Override
    void addCategoryNames(CategoryContext categoryContext) {
        categoryContext.add(PodCastIdCollector.Category.VIDEO_GAMES, "vidéo");
        categoryContext.add(PodCastIdCollector.Category.NEWS_POLITICS, "Nouvelles et politique");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "Musique");
        categoryContext.add(PodCastIdCollector.Category.DESIGN, "Art"); //DESIGN better then ARTS
        categoryContext.add(PodCastIdCollector.Category.HEALTH, "Santé");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "Affaires");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Comédie");
        categoryContext.add(PodCastIdCollector.Category.K_12, "Formation");
        categoryContext.add(PodCastIdCollector.Category.GAMES_HOBBIES, "Jeux et passe-temps");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT_ORGANIZATIONS, "Autorités et organisations");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "Enfants et famille");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Religion et spiritualité");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE_MEDICINE, "Science et médecine");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Société et culture");
        categoryContext.add(PodCastIdCollector.Category.SPORTS_RECREATION, "Sports et loisirs");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Technologie");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "TV et film");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.FR;
    }
}
