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
        //PodCast
//        PodCastBundleBuilder podCastBundle = BundleBuilder.newPodCastBundleBuilder("image", "Toplistan", "10 bästa podcas i Sverige");
//        podCastBundle.addCollector(ItunesSearchAPI.search("term=p3&entity=podcast"));

        PodCastBundleBuilder podCastBundle = BundleBuilder.newPodCastBundleBuilder("image FIXME", "Toplistan", "10 bästa podcas i Sverige");//FIXME Image
        podCastBundle.addCollector(new PodCastCollectorOkihika(PodCastCollectorOkihika.TopList.TOPLIST_SWEDEN, 20));

//        PodCastCollectorOkihika x = new PodCastCollectorOkihika(PodCastCollectorOkihika.TopList.TOPLIST_SWEDEN,20);

        //Categories (List of PodCasts) //FIXME Kategory image 100?
        PodCastCategoryBundleBuilder categoryBundle = BundleBuilder.newPodCastCategoryBundleBuilder("bundle image", "Alla Kategorier", "???..");
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.NEWS_POLITICS, "Nyheter och politik", "???", "image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.MUSIC, "Musik", "Musik", "image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.ARTS, "Konst", "???", "image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.BUSINESS, "Näringsliv", "???", "image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.COMEDY, "Komedi", "???", "image"));

        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.K_12, "Utbildning", "???", "image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.GAMES_HOBBIES, "Spel och hobbu", "???", "image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.GOVERNMENT_ORGANIZATIONS, "Myndigheter och organistaioner", "???", "image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.KIDS_FAMILY, "Barn och familj", "???", "image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.RELIGION_SPIRITUALITY, "Religion och andligh", "???", "image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.SCIENCE_MEDICINE, "Vetenskap och medecin", "???", "image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.SOCIETY_CULTURE, "Samhälle och kultur", "???", "image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.SPORTS_RECREATION, "Sport och fritid", "???", "image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.TECHNOLOGY, "Teknologi", "???", "image"));
        categoryBundle.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.TV_FILM, "TV och film", "???", "image"));


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
