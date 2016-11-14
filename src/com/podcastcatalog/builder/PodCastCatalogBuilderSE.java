package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.builder.collector.okihika.PodCastCategoryCollectorOkihika;
import com.podcastcatalog.builder.collector.okihika.PodCastCollectorOkihika;
import com.podcastcatalog.builder.collector.okihika.PodCastEpisodeCollectorOkihika;

import java.util.HashSet;
import java.util.Set;
//XML or Database...
public class PodCastCatalogBuilderSE implements PodCastCatalogBuilder{

    @Override
    public Set<BundleBuilder> getBundleBuilders() {
        //FIXME implement...
        //PodCast
//        PodCastBundleBuilder podCastBundle = BundleBuilder.newPodCastBundleBuilder("image", "Toplistan", "10 bästa podcas i Sverige");
//        podCastBundle.addCollector(ItunesSearchAPI.search("term=p3&entity=podcast"));

        PodCastCollectorOkihika toplistSweden = new PodCastCollectorOkihika(PodCastCollectorOkihika.TopList.TOPLIST_SWEDEN,20);
        PodCastBundleBuilder podCastBundle = BundleBuilder.newPodCastBundleBuilder("image", "Toplistan", "10 bästa podcas i Sverige");
        podCastBundle.addCollector(toplistSweden);

//        PodCastCollectorOkihika x = new PodCastCollectorOkihika(PodCastCollectorOkihika.TopList.TOPLIST_SWEDEN,20);


        //Categories (List of PodCasts) //FIXME Kategory image 100?
        PodCastCategoryBundleBuilder categoryBundle = BundleBuilder.newPodCastCategoryBundleBuilder("bundle image", "Alla Kategorier", "???..");
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.NEWS_POLITICS, "Nyheter och politik", "???","image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.MUSIC,"Musik", "Musik","image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.ARTS, "Konst", "???","image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.BUSINESS, "Näringsliv", "???","image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.COMEDY, "Komedi", "???","image"));

        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.K_12, "Utbildning", "???","image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.GAMES_HOBBIES, "Spel och hobbu", "???","image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.GOVERNMENT_ORGANIZATIONS, "Myndigheter och organistaioner", "???","image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.KIDS_FAMILY, "Barn och familj", "???","image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.RELIGION_SPIRITUALITY, "Religion och andligh", "???","image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.SCIENCE_MEDICINE, "Vetenskap och medecin", "???","image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.SOCIETY_CULTURE, "Samhälle och kultur", "???","image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.SPORTS_RECREATION, "Sport och fritid", "???","image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.TECHNOLOGY, "Teknologi", "???","image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.TV_FILM, "TV och film", "???","image"));

        //Episodes
        PodCastEpisodeBundleBuilder episodeBundle = BundleBuilder.newPodCastEpisodeBundleBuilder("bundle image", "Only for U", "???..");
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
