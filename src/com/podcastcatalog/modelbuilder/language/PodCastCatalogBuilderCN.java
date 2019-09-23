package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

public class PodCastCatalogBuilderCN  extends PodCastCatalogBuilderBase {

    @Override
    void addCategoryNames(CategoryContext categoryContext) {
      //  categoryContext.add(PodCastIdCollector.Category.VIDEO_GAMES, "視頻"); //BUG Not working?
       categoryContext.add(PodCastIdCollector.Category.NEWS, "新聞與政治");
       categoryContext.add(PodCastIdCollector.Category.MUSIC, "音樂");
       categoryContext.add(PodCastIdCollector.Category.DESIGN, "藝術"); //DESIGN better then ARTS
       categoryContext.add(PodCastIdCollector.Category.HEALTH_FITNESS, "健康");
       categoryContext.add(PodCastIdCollector.Category.BUSINESS, "業務");
       categoryContext.add(PodCastIdCollector.Category.COMEDY, "喜劇");
       categoryContext.add(PodCastIdCollector.Category.EDUCATION, "訓練");
       categoryContext.add(PodCastIdCollector.Category.GAMES, "遊戲和愛好");
       categoryContext.add(PodCastIdCollector.Category.GOVERNMENT, "當局和組織");
       categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "兒童與家庭");
       categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "宗教與靈性");
       categoryContext.add(PodCastIdCollector.Category.SCIENCE, "科學和醫學");
       categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "社會與文化");
      // categoryContext.add(PodCastIdCollector.Category.SPORTS, "運動與休閒");
        // categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "技術");
        //  categoryContext.add(PodCastIdCollector.Category.TV_FILM, "電視和電影");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.CN;
    }
}
