package com.podcastcatalog.builder;

import com.podcastcatalog.builder.collector.okihika.PodCastCollectorOkihika;
import com.podcastcatalog.builder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.builder.collector.okihika.PodCastCategoryCollectorOkihika;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.builder.collector.okihika.PodCastEpisodeCollectorOkihika;

import java.util.HashSet;
import java.util.Set;
//XML or Database...
public class PodCastCatalogBuilderSE implements PodCastCatalogBuilder{

    @Override
    public Set<BundleBuilder> getBundleBuilders() {
        //FIXME implement...
        //PodCast
        PodCastBundleBuilder podCastBundle = BundleBuilder.newPodCastBundleBuilder("image", "Toplistan", "10 bästa podcas i Sverige");
        podCastBundle.addCollector(ItunesSearchAPI.search("term=p3&entity=podcast"));

        //Categories (List of PodCasts) //FIXME Kategory image 100?
        PodCastCategoryBundleBuilder categoryBundle = BundleBuilder.newPodCastCategoryBundleBuilder("bundle image", "Alla Kategorier", "descr..");
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.NEWS_POLITICS, "Nyheter och politik", "descr","image"));
//        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.MUSIC,"Musik", "Musik","image"));
//        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.ARTS, "Konst", "descr","image"));
//        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.BUSINESS, "Näringsliv", "descr","image"));
//        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.COMEDY, "Komedi", "descr","image"));
//
//        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.K_12, "Utbildning", "descr","image"));
//        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.GAMES_HOBBIES, "Spel och hobbu", "descr","image"));
//        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.GOVERNMENT_ORGANIZATIONS, "Myndigheter och organistaioner", "descr","image"));
//        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.KIDS_FAMILY, "Barn och familj", "descr","image"));
//        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.RELIGION_SPIRITUALITY, "Religion och andligh", "descr","image"));
//        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.SCIENCE_MEDICINE, "Vetenskap och medecin", "descr","image"));
//        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.SOCIETY_CULTURE, "Samhälle och kultur", "descr","image"));
//        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.SPORTS_RECREATION, "Sport och fritid", "descr","image"));
//        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.TECHNOLOGY, "Teknologi", "descr","image"));
//        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.TV_FILM, "TV och film", "descr","image"));

        //Episodes
        PodCastEpisodeBundleBuilder episodeBundle = BundleBuilder.newPodCastEpisodeBundleBuilder("bundle image", "Only for U", "descr..");
        episodeBundle.addCollector(new PodCastEpisodeCollectorOkihika(PodCastCollectorOkihika.TopList.ARTS, 1,1));
        episodeBundle.addCollector(new PodCastEpisodeCollectorOkihika(PodCastCollectorOkihika.TopList.TECH_NEWS, 2,2));
        episodeBundle.addCollector(new PodCastEpisodeCollectorOkihika(PodCastCollectorOkihika.TopList.NEWS_POLITICS, 2,2));

        Set<BundleBuilder> bundleBuilders = new HashSet<>();
        bundleBuilders.add(podCastBundle);
        bundleBuilders.add(categoryBundle);
        bundleBuilders.add(episodeBundle);

        return bundleBuilders;
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.Sweden;
    }
}
