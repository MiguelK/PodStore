package com.podcastcatalog.service.podcastcatalog;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.*;
import com.podcastcatalog.modelbuilder.*;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCategoryCollectorOkihika;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;
import com.podcastcatalog.storage.DataStorage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PodCastCatalogServiceTest {


    @Test(groups = TestUtil.SLOW_TEST)
    public void buildPodCastCatalogs() {

        setUpStorage();

        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilder() {
            @Override
            public List<Bundle> createFromFetchedData(List<PodCast> podCas,
                                                      List<PodCastCategory> PodCastCategories) {
                return Collections.emptyList();
            }

            @Override
            public Set<BundleBuilder> getBundleBuilders() {

                PodCastBundleBuilder topListBuilder = BundleBuilder.newPodCastBundleBuilder("Toplistan", "10 bästa podcas i Sverige");
                topListBuilder.addCollector(ItunesSearchAPI.search("term=p3&entity=podcast&limit=1"));

                Set<BundleBuilder> bundleBuilders = new HashSet<>();
                bundleBuilders.add(topListBuilder);

                return bundleBuilders;
            }

            @Override
            public PodCastCatalogLanguage getPodCastCatalogLang() {
                return PodCastCatalogLanguage.Sweden;
            }
        });


        PodCastCatalogService.getInstance().buildPodCastCatalogs();
        PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);
        Assert.assertNotNull(podCastCatalog);
        Assert.assertFalse(podCastCatalog.getBundles().isEmpty());

    }

    private void setUpStorage() {
        DataStorage storage = new DataStorage();
        storage.deleteAll();

        PodCastCatalogService.getInstance().setStorage(storage);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void build_hallo_world_catalog() {

        setUpStorage();

        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilder() {
            @Override
            public Set<BundleBuilder> getBundleBuilders() {
                PodCastBundleBuilder podCastBundleBuilder = BundleBuilder.newPodCastBundleBuilder("Toplistan", "10 bästa podcas i Sverige");
                podCastBundleBuilder.addCollector(new PodCastCollectorOkihika(PodCastCollectorOkihika.TopList.TOPLIST_SWEDEN, 4));

                PodCastCategoryBundleBuilder categoryBundleBuilder = BundleBuilder.newPodCastCategoryBundleBuilder("Alla Kategorier", "???..");
                categoryBundleBuilder.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.NEWS_POLITICS, 2, "Nyheter och politik", "???"));
                categoryBundleBuilder.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.TECHNOLOGY,2, "Teknologi", "???"));
                categoryBundleBuilder.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.TV_FILM,1, "TV och film", "???"));


                Set<BundleBuilder> bundleBuilders = new HashSet<>();
                bundleBuilders.add(podCastBundleBuilder);
                bundleBuilders.add(categoryBundleBuilder);

                return bundleBuilders;
            }

            @Override
            public List<Bundle> createFromFetchedData(List<PodCast> podCas,
                                                      List<PodCastCategory> podCastCategories) {

                RandomPodCastEpisodeBundleBuilder bundleBuilder = new RandomPodCastEpisodeBundleBuilder(podCas, podCastCategories);
                Bundle episodeBundle = bundleBuilder.createEpisodeBundle();

                return Collections.singletonList(episodeBundle);
            }

            @Override
            public PodCastCatalogLanguage getPodCastCatalogLang() {
                return PodCastCatalogLanguage.Sweden;
            }
        });

        PodCastCatalogService
                .getInstance().buildPodCastCatalogs();

        //1# App start get current built catalog
        PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);

        TestUtil.assertToJSONNotNull(podCastCatalog);

        System.out.println("LocalCatalog = " + podCastCatalog);
    }
}