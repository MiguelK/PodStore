package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;

//Portugal
public class PodCastCatalogBuilderPT extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {

        categoryContext.add(PodCastIdCollector.Category.NEWS_POLITICS, "Notícias e Política");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "Música");
        categoryContext.add(PodCastIdCollector.Category.ARTS, "Artes");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "O negócio");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Comédia");
        categoryContext.add(PodCastIdCollector.Category.K_12, "Educação");
        categoryContext.add(PodCastIdCollector.Category.GAMES_HOBBIES, "Jogos");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT_ORGANIZATIONS, "Governo e Organizações");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "Crianças e família");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Religião e Espiritualidade");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE_MEDICINE, "Ciência e medicina");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Sociedade e Cultura");
        categoryContext.add(PodCastIdCollector.Category.SPORTS_RECREATION, "Esportes");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Tecnologia");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "TV & Film");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.PT;
    }
}