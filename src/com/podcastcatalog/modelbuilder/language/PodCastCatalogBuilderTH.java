package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;

//Thailand
public class PodCastCatalogBuilderTH extends PodCastCatalogBuilderBase {


    @Override
    void addCategoryNames(CategoryContext categoryContext) {

        categoryContext.add(PodCastIdCollector.Category.NEWS, "ข่าวและการเมือง");
        categoryContext.add(PodCastIdCollector.Category.MUSIC, "เพลง");
        categoryContext.add(PodCastIdCollector.Category.DESIGN, "ศิลปะ");
        categoryContext.add(PodCastIdCollector.Category.BUSINESS, "ธุรกิจ");
        categoryContext.add(PodCastIdCollector.Category.COMEDY, "ความขบขัน");
        categoryContext.add(PodCastIdCollector.Category.EDUCATION, "การศึกษา");
        categoryContext.add(PodCastIdCollector.Category.GAMES, "เกม");
        categoryContext.add(PodCastIdCollector.Category.GOVERNMENT, "รัฐบาลและองค์กร");
        categoryContext.add(PodCastIdCollector.Category.KIDS_FAMILY, "เด็กและครอบครัว");
        categoryContext.add(PodCastIdCollector.Category.RELIGION_SPIRITUALITY, "ศาสนาและจิตวิญญาณ");
        categoryContext.add(PodCastIdCollector.Category.SCIENCE, "วิทยาศาสตร์และยา");
        categoryContext.add(PodCastIdCollector.Category.SOCIETY_CULTURE, "สังคมและวัฒนธรรม");
        categoryContext.add(PodCastIdCollector.Category.SPORTS, "กีฬา");
        categoryContext.add(PodCastIdCollector.Category.TECHNOLOGY, "เทคโนโลยี");
        categoryContext.add(PodCastIdCollector.Category.TV_FILM, "ทีวีและภาพยนตร์");
        categoryContext.add(PodCastIdCollector.Category.TRUE_CRIME, "True Crime");

        categoryContext.add (PodCastIdCollector.Category.SOCCER, "Football");
        categoryContext.add (PodCastIdCollector.Category.RUNNING, "Running");
        categoryContext.add (PodCastIdCollector.Category.DAILY_NEWS, "Daily News");
        categoryContext.add (PodCastIdCollector.Category.TECH_NEWS, "Tech News");
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.TH;
    }
}
