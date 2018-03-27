package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

public class PodCastCatalogBuilderES extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {
        categoryContext.add(PodCastCollectorOkihika.TopList.VIDEO_GAMES, "Vídeo");
        categoryContext.add( PodCastCollectorOkihika.TopList.NEWS_POLITICS, "Noticias y política");
        categoryContext.add( PodCastCollectorOkihika.TopList.MUSIC, "Música");
        categoryContext.add( PodCastCollectorOkihika.TopList.DESIGN, "Arte"); //DESIGN better then ARTS
        categoryContext.add( PodCastCollectorOkihika.TopList.HEALTH, "Salud");
        categoryContext.add( PodCastCollectorOkihika.TopList.BUSINESS, "Negocios");
        categoryContext.add( PodCastCollectorOkihika.TopList.COMEDY, "Comedia");
        categoryContext.add( PodCastCollectorOkihika.TopList.K_12, "Formación");
        categoryContext.add( PodCastCollectorOkihika.TopList.GAMES_HOBBIES, "Juegos y hobby");
        categoryContext.add( PodCastCollectorOkihika.TopList.GOVERNMENT_ORGANIZATIONS, "Autoridades y organizaciones");
        categoryContext.add( PodCastCollectorOkihika.TopList.KIDS_FAMILY, "Niños y familia");
        categoryContext.add( PodCastCollectorOkihika.TopList.RELIGION_SPIRITUALITY, "Religión y Espiritualidad");
        categoryContext.add( PodCastCollectorOkihika.TopList.SCIENCE_MEDICINE, "Ciencia y Medicina");
        categoryContext.add( PodCastCollectorOkihika.TopList.SOCIETY_CULTURE, "Sociedad y cultura");
        categoryContext.add( PodCastCollectorOkihika.TopList.SPORTS_RECREATION, "Deportes y ocio");
        categoryContext.add( PodCastCollectorOkihika.TopList.TECHNOLOGY, "Tecnología");
        categoryContext.add( PodCastCollectorOkihika.TopList.TV_FILM, "TV y película");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.ES;
    }
}
