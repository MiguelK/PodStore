package com.podcastcatalog.service.podcastcatalog;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.*;
import com.podcastcatalog.modelbuilder.*;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCategoryCollectorOkihika;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;
import com.podcastcatalog.service.ServiceDataStorageDisk;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PodCastCatalogServiceTest {

    //bundles=[PodCastCategoryBundle{podCastCategories=15}, PodCastBundle{podCasts=15}, PodCastEpisodeBundle{podCastEpisodes=16}]}
    //1:21 min
    //Old 4:08 bundles=[PodCastCategoryBundle{podCastCategories=15}, PodCastBundle{podCasts=15}, PodCastEpisodeBundle{podCastEpisodes=16}]}
    //@Test(groups = TestUtil.SLOW_TEST)
    public void build_catalog_swe() throws InterruptedException, ExecutionException, TimeoutException {

        setUpStorage();


        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilderSE());

         PodCastCatalogService
                .getInstance().buildPodCastCatalogsAsync().get(16, TimeUnit.MINUTES);


        PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);
        System.out.println(podCastCatalog);

    }

    @Test
    public void getPodCastCatalogIndexStatus() {
        String status = PodCastCatalogService.getInstance().getPodCastCatalogIndexStatus();

        Assert.assertTrue(status.contains("Size=0"), " status=" + status);
    }

    @Test
    public void getPodCastById() {
        PodCast podCast = PodCastTest.createValid().build();
        PodCastCatalogService.getInstance().updatePodCastIndex(podCast);

        Optional<PodCast> podCastById = PodCastCatalogService.getInstance().getPodCastById(podCast.getCollectionId());
        Assert.assertTrue(podCastById.isPresent());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void buildPodCastCatalogs() throws InterruptedException, ExecutionException, TimeoutException {

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


        PodCastCatalogService.getInstance().buildPodCastCatalogsAsync().get(5,TimeUnit.MINUTES);
        PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);
        Assert.assertNotNull(podCastCatalog);
        Assert.assertFalse(podCastCatalog.getBundles().isEmpty());

    }

    private static void setUpStorage() {
        ServiceDataStorageDisk storage = new ServiceDataStorageDisk();
        storage.deleteAll();

        PodCastCatalogService.getInstance().setStorage(storage);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void build_hallo_world_catalog() throws InterruptedException, ExecutionException, TimeoutException {

        initPodCastCatalogs();

        //1# App start get current built catalog
        PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);

        TestUtil.assertToJSONNotNull(podCastCatalog);

        System.out.println("LocalCatalog = " + podCastCatalog);
    }

    public static void initPodCastCatalogs() throws InterruptedException, ExecutionException, TimeoutException {
        setUpStorage();

        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilder() {
            @Override
            public Set<BundleBuilder> getBundleBuilders() {
                PodCastBundleBuilder podCastBundleBuilder = BundleBuilder.newPodCastBundleBuilder("Toplistan", "10 bästa podcas i Sverige");
                podCastBundleBuilder.addCollector(new PodCastCollectorOkihika(PodCastCollectorOkihika.TopList.TOPLIST_SWEDEN, 4));

                PodCastCategoryBundleBuilder categoryBundleBuilder = BundleBuilder.newPodCastCategoryBundleBuilder("Alla Kategorier", "???..");
                categoryBundleBuilder.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.NEWS_POLITICS, 2, "Nyheter och politik", "???"));
                categoryBundleBuilder.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.TECHNOLOGY, 2, "Teknologi", "???"));
                categoryBundleBuilder.addCollector(new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.TV_FILM, 1, "TV och film", "???"));


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
                .getInstance().buildPodCastCatalogsAsync().get(3, TimeUnit.MINUTES);
    }

}