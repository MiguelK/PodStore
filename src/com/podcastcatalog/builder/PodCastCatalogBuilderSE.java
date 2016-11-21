package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.bundle.Bundle;
import com.podcastcatalog.builder.collector.okihika.PodCastCategoryCollectorOkihika;
import com.podcastcatalog.builder.collector.okihika.PodCastCollectorOkihika;

import java.util.*;

//XML or Database...
public class PodCastCatalogBuilderSE implements PodCastCatalogBuilder {

    @Override
    public List<Bundle> createFromFetchedData(List<PodCast> podCasts, List<PodCastCategory> podCastCategories) {
        RandomPodCastEpisodeBundleBuilder randomPodCastEpisodeBundleBuilder = new RandomPodCastEpisodeBundleBuilder(podCasts, podCastCategories);

        Bundle bundle = randomPodCastEpisodeBundleBuilder.createEpisodeBundle();

        return Collections.singletonList(bundle);
    }

    @Override
    public Set<BundleBuilder> getBundleBuilders() {
        //FIXME implement...
//        PodCastBundleBuilder podCastBundle = BundleBuilder.newPodCastBundleBuilder("image", "Toplistan", "10 bästa podcas i Sverige");
//        podCastBundle.addCollector(ItunesSearchAPI.search("term=p3&entity=podcast"));

        PodCastBundleBuilder podCastBundle = BundleBuilder.newPodCastBundleBuilder("image", "Toplistan", "De bästa i Sverige");//FIXME Image
        podCastBundle.addCollector(new PodCastCollectorOkihika(PodCastCollectorOkihika.TopList.TOPLIST_SWEDEN, 20));

//        PodCastCollectorOkihika x = new PodCastCollectorOkihika(PodCastCollectorOkihika.TopList.TOPLIST_SWEDEN,20);

        PodCastCategoryBundleBuilder categoryBundle = BundleBuilder.newPodCastCategoryBundleBuilder("image", "Alla Kategorier", "???");
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.NEWS_POLITICS, "Nyheter och politik", "???"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.MUSIC, "Musik", ""));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.ARTS, "Konst", "???"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.BUSINESS, "Näringsliv", "???"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.COMEDY, "Komedi", "???"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.K_12, "Utbildning", "???"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.GAMES_HOBBIES, "Spel och hobbu", "???"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.GOVERNMENT_ORGANIZATIONS, "Myndigheter och organistaioner", "???"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.KIDS_FAMILY, "Barn och familj", "???"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.RELIGION_SPIRITUALITY, "Religion och andligh", "???"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.SCIENCE_MEDICINE, "Vetenskap och medecin", "???"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.SOCIETY_CULTURE, "Samhälle och kultur", "???"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.SPORTS_RECREATION, "Sport och fritid", "???"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.TECHNOLOGY, "Teknologi", "???"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.TV_FILM, "TV och film", "???"));


        Set<BundleBuilder> bundleBuilders = new HashSet<>();
        bundleBuilders.add(podCastBundle);
        bundleBuilders.add(categoryBundle);

        return bundleBuilders;
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.Sweden;
    }
}
