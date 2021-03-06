package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;

//France
public class PodCastCatalogBuilderFR extends PodCastCatalogBuilderBase {

    @Override
    void addCategoryNames(CategoryContext categoryContext) {
        categoryContext.add(PodCastIdCollector.Category.VIDEO_GAMES, "vidéo");
        categoryContext.add(PodCastIdCollector.Category.NEWS, "Nouvelles et politique");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "Musique");
        categoryContext.add(PodCastIdCollector.Category.DESIGN, "Art"); //DESIGN better then ARTS
        categoryContext.add(PodCastIdCollector.Category.HEALTH_FITNESS, "Santé");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "Affaires");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Comédie");
        categoryContext.add(PodCastIdCollector.Category.EDUCATION, "Formation");
        categoryContext.add(PodCastIdCollector.Category.GAMES, "Jeux et passe-temps");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT, "Autorités et organisations");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "Enfants et famille");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Religion et spiritualité");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE, "Science et médecine");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Société et culture");
        categoryContext.add(PodCastIdCollector.Category.SPORTS, "Sports et loisirs");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Technologie");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "TV et film");
        categoryContext.add(PodCastIdCollector.Category.TRUE_CRIME, "True Crime");

        categoryContext.add (PodCastIdCollector.Category.SOCCER, "Football");
        categoryContext.add (PodCastIdCollector.Category.RUNNING, "Running");
        categoryContext.add (PodCastIdCollector.Category.DAILY_NEWS, "Daily News");
        categoryContext.add (PodCastIdCollector.Category.TECH_NEWS, "Tech News");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.FR;
    }
}
