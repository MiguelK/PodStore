package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

public class PodCastCatalogBuilderCN  extends PodCastCatalogBuilderBase {

    @Override
    void addCategoryNames(CategoryContext categoryContext) {
        categoryContext.add(PodCastCollectorOkihika.TopList.VIDEO_GAMES, "視頻");
       categoryContext.add(PodCastCollectorOkihika.TopList.VIDEO_GAMES, "視頻");
       categoryContext.add(PodCastCollectorOkihika.TopList.NEWS_POLITICS, "新聞與政治");
       categoryContext.add(PodCastCollectorOkihika.TopList.MUSIC, "音樂");
       categoryContext.add(PodCastCollectorOkihika.TopList.DESIGN, "藝術"); //DESIGN better then ARTS
       categoryContext.add(PodCastCollectorOkihika.TopList.HEALTH, "健康");
       categoryContext.add(PodCastCollectorOkihika.TopList.BUSINESS, "業務");
       categoryContext.add(PodCastCollectorOkihika.TopList.COMEDY, "喜劇");
       categoryContext.add(PodCastCollectorOkihika.TopList.K_12, "訓練");
       categoryContext.add(PodCastCollectorOkihika.TopList.GAMES_HOBBIES, "遊戲和愛好");
       categoryContext.add(PodCastCollectorOkihika.TopList.GOVERNMENT_ORGANIZATIONS, "當局和組織");
       categoryContext.add(PodCastCollectorOkihika.TopList.KIDS_FAMILY, "兒童與家庭");
       categoryContext.add(PodCastCollectorOkihika.TopList.RELIGION_SPIRITUALITY, "宗教與靈性");
       categoryContext.add(PodCastCollectorOkihika.TopList.SCIENCE_MEDICINE, "科學和醫學");
       categoryContext.add(PodCastCollectorOkihika.TopList.SOCIETY_CULTURE, "社會與文化");
       categoryContext.add(PodCastCollectorOkihika.TopList.SPORTS_RECREATION, "運動與休閒");
       categoryContext.add(PodCastCollectorOkihika.TopList.TECHNOLOGY, "技術");
       categoryContext.add(PodCastCollectorOkihika.TopList.TV_FILM, "電視和電影");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.CN;
    }
}
