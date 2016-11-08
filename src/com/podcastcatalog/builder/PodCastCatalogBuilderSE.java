package com.podcastcatalog.builder;

import com.podcastcatalog.builder.collector.okihika.PodCastCollectorOkihika;
import com.podcastcatalog.builder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.builder.collector.okihika.PodCastCategoryCollectorOkihika;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.api.response.PodCastCategoryType;
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


        //Categories (List of PodCasts)
        PodCastCategoryBundleBuilder categoryBundle = BundleBuilder.newPodCastCategoryBundleBuilder("bundle image", "All Kategorier", "descr..");
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.NEWS_POLITICS, 3,"Konst", "descr","image", PodCastCategoryType.News_Politics ));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.MUSIC, 3,"Musik", "descr","image", PodCastCategoryType.Music ));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.Tech_News, 3,"Musik", "descr","image", PodCastCategoryType.Tech_News ));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.ARTS, 3,"Musik", "descr","image", PodCastCategoryType.Arts ));

        //Episodes
        PodCastEpisodeBundleBuilder episodeBundle = BundleBuilder.newPodCastEpisodeBundleBuilder("bundle image", "Only for U", "descr..");
        episodeBundle.addCollector(new PodCastEpisodeCollectorOkihika(PodCastCollectorOkihika.TopList.ARTS, 1,1));
        episodeBundle.addCollector(new PodCastEpisodeCollectorOkihika(PodCastCollectorOkihika.TopList.Tech_News, 2,2));
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
