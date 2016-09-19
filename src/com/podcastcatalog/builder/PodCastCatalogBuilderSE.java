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

        PodCastBundleBuilder.Builder topListBuilder = PodCastBundleBuilder.newBuilder("image", "Toplistan", "10 bästa podcas i Sverige");
        topListBuilder.addCollector(ItunesSearchAPI.search("term=p3&entity=podcast"));


        PodCastCategoryBundleBuilder.Builder builder = PodCastCategoryBundleBuilder.newBuilder("bundle image", "All Kategorier", "descr..");
        builder.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.ARTS, 3,"Konst", "descr","image", PodCastCategoryType.Arts )).
        addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.MUSIC, 3,"Musik", "descr","image", PodCastCategoryType.Music ));


        //FIXME Episode builder..
        PodCastEpisodeBundleBuilder.Builder newBuilder = PodCastEpisodeBundleBuilder.newBuilder("bundle image", "Only for U", "descr..");
        newBuilder.addCollector(new PodCastEpisodeCollectorOkihika(PodCastCollectorOkihika.TopList.ARTS, 1));
//        PodCastEpisodeBundleBuilder episodeBundleBuilder = PodCastEpisodeBundleBuilder.create("image", "5 avsnitt för dig", "descr", );

        Set<BundleBuilder> bundleBuilders = new HashSet<>();
        bundleBuilders.add(topListBuilder.build());
        bundleBuilders.add(builder.build());

        return bundleBuilders;
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.Sweden;
    }
}
