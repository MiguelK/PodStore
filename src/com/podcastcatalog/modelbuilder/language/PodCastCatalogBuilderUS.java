package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.Bundle;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastBundle;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastcatalog.PodCastCategory;
import com.podcastcatalog.modelbuilder.*;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCategoryCollectorOkihika;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//XML or Database...
public class PodCastCatalogBuilderUS implements PodCastCatalogBuilder {

    @Override
    public List<Bundle> createFromFetchedData(List<PodCast> podCasts, List<PodCastCategory> podCastCategories) {
        RandomPodCastEpisodeBundleBuilder randomPodCastEpisodeBundleBuilder = new RandomPodCastEpisodeBundleBuilder(podCasts, podCastCategories);

        List<Bundle> bundles = new ArrayList<>();

        Bundle bundle = randomPodCastEpisodeBundleBuilder.createEpisodeBundle();
        bundles.add(bundle);

        TimeDurationPodCastBundleBuilder timeDurationPodCastBundleBuilder = new TimeDurationPodCastBundleBuilder(podCasts, podCastCategories);
        PodCastBundle podCastBundle = timeDurationPodCastBundleBuilder.createPodCastBundle("Time is money");
        bundles.add(podCastBundle);

        return bundles;
    }

    @Override
    public Set<BundleBuilder> getBundleBuilders() {

        PodCastBundleBuilder podCastBundle = BundleBuilder.newPodCastBundleBuilder("Toplist", "Best PodCasts");
        podCastBundle.addCollector(new PodCastCollectorOkihika(PodCastCollectorOkihika.Language.US, PodCastCollectorOkihika.TopList.TOPLIST_COUNTRY, 20));

        PodCastCategoryBundleBuilder categoryBundle = BundleBuilder.newPodCastCategoryBundleBuilder("Categories", "???");
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.NEWS_POLITICS, "News and Politics"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.MUSIC, "Music"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.ARTS, "Art"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.BUSINESS, "Business"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.COMEDY, "Comedy"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.K_12, "Education"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.GAMES_HOBBIES, "Games"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.GOVERNMENT_ORGANIZATIONS, "Government & Organizations"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.KIDS_FAMILY, "Kids & Family"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.RELIGION_SPIRITUALITY, "Religion & Spirituality"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.SCIENCE_MEDICINE, "Science_& Medicine"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.SOCIETY_CULTURE, "Society & Culture"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.SPORTS_RECREATION, "Sports"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.TECHNOLOGY, "Technology"));
        categoryBundle.addCollector(PodCastCategoryCollectorOkihika.createUS(PodCastCollectorOkihika.TopList.TV_FILM, "TV & Film"));

        Set<BundleBuilder> bundleBuilders = new HashSet<>();
        bundleBuilders.add(podCastBundle);
        bundleBuilders.add(categoryBundle);

        return bundleBuilders;
    }

    @Override
    public PodCastCatalogLanguage getPodCastCatalogLang() {
        return PodCastCatalogLanguage.US;
    }
}
