package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;

//Portugal
public class PodCastCatalogBuilderPT extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {

        categoryContext.add(PodCastIdCollector.Category.NEWS, "Notícias e Política");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "Música");
        categoryContext.add(PodCastIdCollector.Category.DESIGN, "Artes");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "O negócio");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "Comédia");
        categoryContext.add(PodCastIdCollector.Category.EDUCATION, "Educação");
        categoryContext.add(PodCastIdCollector.Category.GAMES, "Jogos");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT, "Governo e Organizações");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "Crianças e família");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "Religião e Espiritualidade");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE, "Ciência e medicina");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "Sociedade e Cultura");
        categoryContext.add(PodCastIdCollector.Category.SPORTS, "Esportes");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "Tecnologia");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "TV & Film");
        categoryContext.add(PodCastIdCollector.Category.TRUE_CRIME, "True Crime");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.PT;
    }
}
