package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastBundle;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastcatalog.PodCastCategory;
import com.podcastcatalog.model.podcastcatalog.Bundle;
import com.podcastcatalog.modelbuilder.*;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCategoryCollectorOkihika;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

import java.util.*;

//XML or Database...
public class PodCastCatalogBuilderSE implements PodCastCatalogBuilder {

    @Override
    public List<Bundle> createFromFetchedData(List<PodCast> podCasts, List<PodCastCategory> podCastCategories) {

        List<Bundle> bundles = new ArrayList<>();

        RandomPodCastEpisodeBundleBuilder randomPodCastEpisodeBundleBuilder = new RandomPodCastEpisodeBundleBuilder(podCasts, podCastCategories);
        Bundle bundle = randomPodCastEpisodeBundleBuilder.createEpisodeBundle("Popular Episodes");
        bundles.add(bundle);

        TimeDurationPodCastBundleBuilder timeDurationPodCastBundleBuilder = new TimeDurationPodCastBundleBuilder(podCasts, podCastCategories);
        PodCastBundle podCastBundle = timeDurationPodCastBundleBuilder.createPodCastBundle("Tid är pengar");
        bundles.add(podCastBundle);

        return bundles;
    }

    @Override
    public Set<BundleBuilder> getBundleBuilders() {

       PodCastBundleBuilder podCastBundle = BundleBuilder.newPodCastBundleBuilder("Toplistan", "De bästa i Sverige");//FIXME Image
        podCastBundle.addCollector(new PodCastCollectorOkihika(PodCastCollectorOkihika.Language.SWE, PodCastCollectorOkihika.TopList.TOPLIST_COUNTRY, 30));

        PodCastCategoryBundleBuilder categoryBundle = BundleBuilder.newPodCastCategoryBundleBuilder("Kategorier", "???");
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.NEWS_POLITICS, "Nyheter och politik"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.MUSIC, "Musik"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.ARTS, "Konst"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.BUSINESS, "Näringsliv"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.COMEDY, "Komedi"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.K_12, "Utbildning"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.GAMES_HOBBIES, "Spel och hobbu"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.GOVERNMENT_ORGANIZATIONS, "Myndigheter och organistaioner"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.KIDS_FAMILY, "Barn och familj"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.RELIGION_SPIRITUALITY, "Religion och andligh"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.SCIENCE_MEDICINE, "Vetenskap och medecin"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.SOCIETY_CULTURE, "Samhälle och kultur"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.SPORTS_RECREATION, "Sport och fritid"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.TECHNOLOGY, "Teknologi"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createSWE(PodCastCollectorOkihika.TopList.TV_FILM, "TV och film"));

        Set<BundleBuilder> bundleBuilders = new HashSet<>();
        bundleBuilders.add(podCastBundle);
        bundleBuilders.add(categoryBundle);

        return bundleBuilders;
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.SWE;
    }
}
