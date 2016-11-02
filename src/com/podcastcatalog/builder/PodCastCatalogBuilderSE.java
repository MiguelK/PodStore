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
        PodCastBundleBuilder topListBuilder = BundleBuilder.newPodCastBundleBuilder("image", "Toplistan", "10 bästa podcas i Sverige");
        topListBuilder.addCollector(ItunesSearchAPI.search("term=p3&entity=podcast"));


        //CategoryBundleBuilder
        PodCastCategoryBundleBuilder categoryBundle = BundleBuilder.newPodCastCategoryBundleBuilder("bundle image", "All Kategorier", "descr..");
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.ARTS, 3,"Konst", "descr","image", PodCastCategoryType.Arts ));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.MUSIC, 3,"Musik", "descr","image", PodCastCategoryType.Music ));


        //EpisodeBundleBuilder
        PodCastEpisodeBundleBuilder newBuilder = BundleBuilder.newPodCastEpisodeBundleBuilder("bundle image", "Only for U", "descr..");
        newBuilder.addCollector(new PodCastEpisodeCollectorOkihika(PodCastCollectorOkihika.TopList.ARTS, 1));

        Set<BundleBuilder> bundleBuilders = new HashSet<>();
        bundleBuilders.add(topListBuilder);
        bundleBuilders.add(categoryBundle);

        return bundleBuilders;
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.Sweden;
    }
}
