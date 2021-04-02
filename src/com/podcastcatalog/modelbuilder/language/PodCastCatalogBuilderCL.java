package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;

//Chile
public class PodCastCatalogBuilderCL extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {

        categoryContext.add(PodCastIdCollector.Category.VIDEO_GAMES, "Vídeo");
        categoryContext.add( PodCastIdCollector.Category.NEWS, "Noticias y política");
        categoryContext.add( PodCastIdCollector.Category.MUSIC, "Música");
        categoryContext.add( PodCastIdCollector.Category.DESIGN, "Arte"); //DESIGN better then ARTS
        categoryContext.add( PodCastIdCollector.Category.HEALTH_FITNESS, "Salud");
        categoryContext.add( PodCastIdCollector.Category.BUSINESS, "Negocios");
        categoryContext.add( PodCastIdCollector.Category.COMEDY, "Comedia");
        categoryContext.add( PodCastIdCollector.Category.EDUCATION, "Formación");
        categoryContext.add( PodCastIdCollector.Category.GAMES, "Juegos y hobby");
        categoryContext.add( PodCastIdCollector.Category.GOVERNMENT, "Autoridades y organizaciones");
        categoryContext.add( PodCastIdCollector.Category.KIDS_FAMILY, "Niños y familia");
        categoryContext.add( PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Religión y Espiritualidad");
        categoryContext.add( PodCastIdCollector.Category.SCIENCE, "Ciencia y Medicina");
        categoryContext.add( PodCastIdCollector.Category.SOCIETY_CULTURE, "Sociedad y cultura");
        categoryContext.add( PodCastIdCollector.Category.SPORTS, "Deportes y ocio");
        categoryContext.add( PodCastIdCollector.Category.TECHNOLOGY, "Tecnología");
        categoryContext.add( PodCastIdCollector.Category.TV_FILM, "TV y película");
        categoryContext.add(PodCastIdCollector.Category.TRUE_CRIME, "True Crime");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.CL;
    }
}
