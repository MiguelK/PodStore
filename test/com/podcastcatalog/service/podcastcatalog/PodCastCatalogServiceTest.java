package com.podcastcatalog.service.podcastcatalog;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.*;
import com.podcastcatalog.model.podcastsearch.ResultItem;
import com.podcastcatalog.modelbuilder.*;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCategoryCollectorOkihika;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderSE;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderUS;
import com.podcastcatalog.service.datastore.ServiceDataStorage;
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
    /*
    public void build_catalog_swe() throws InterruptedException, ExecutionException, TimeoutException {

        setUpStorage();


        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilderSE());

         PodCastCatalogService
                .getInstance().buildPodCastCatalogsAsync(PodCastCatalogLanguage.SE).get(16, TimeUnit.MINUTES);


        PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.SE);
        System.out.println(podCastCatalog);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void build_catalog_US() throws InterruptedException, ExecutionException, TimeoutException {

        setUpStorage();


        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilderUS());

        PodCastCatalogService
                .getInstance().buildPodCastCatalogsAsync(PodCastCatalogLanguage.US).get(16, TimeUnit.MINUTES);


        PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.US);
        System.out.println(podCastCatalog);
    }
*/

    @Test
    public void search_empty() {
        Assert.assertTrue(PodCastCatalogService.getInstance().search(PodCastCatalogLanguage.SE,null).isEmpty());
        Assert.assertTrue(PodCastCatalogService.getInstance().search(PodCastCatalogLanguage.SE,"").isEmpty());
        Assert.assertTrue(PodCastCatalogService.getInstance().search(PodCastCatalogLanguage.SE," ").isEmpty());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void search_podcasts_no_episodes() {

        List<ResultItem> resultItems = PodCastCatalogService.getInstance().search(PodCastCatalogLanguage.SE,"p1");

       Assert.assertTrue(resultItems.size()>=05);

    }

    /*@Test(groups = TestUtil.SLOW_TEST)
    public void getPodCastById() {
        PodCast podCast = PodCastTest.createValid().build();
        PodCastCatalogService.getInstance().updatePodCastIndex(podCast);

        Optional<PodCast> podCastById = PodCastCatalogService.getInstance().getPodCastById(podCast.getCollectionId());
        Assert.assertTrue(podCastById.isPresent());
    }*/

   /* @Test(groups = TestUtil.SLOW_TEST)
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
                topListBuilder.addCollector(ItunesSearchAPI.createCollector("term=p3&entity=podcast&limit=1"));

                Set<BundleBuilder> bundleBuilders = new HashSet<>();
                bundleBuilders.add(topListBuilder);

                return bundleBuilders;
            }

            @Override
            public PodCastCatalogLanguage getPodCastCatalogLang() {
                return PodCastCatalogLanguage.SE;
            }
        });


        //PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(PodCastCatalogLanguage.SE).get(5,TimeUnit.MINUTES);
        PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.SE);
        Assert.assertNotNull(podCastCatalog);
        Assert.assertFalse(podCastCatalog.getBundles().isEmpty());

    }*/

    private static void setUpStorage() {
        ServiceDataStorage storage =  TestUtil.createForTest();
       // storage.deleteAll();

        //PodCastCatalogService.getInstance().setStorage(storage);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void build_hallo_world_catalog() throws InterruptedException, ExecutionException, TimeoutException {

        initPodCastCatalogs();

        //1# App start get current built catalog
        PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.SE);

        TestUtil.assertToJSONNotNull(podCastCatalog);

        Assert.assertNotNull(podCastCatalog);

        System.out.println("LocalCatalog = " + podCastCatalog);
    }

    public static void initPodCastCatalogs() throws InterruptedException, ExecutionException, TimeoutException {
        setUpStorage();

   /*   PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilder() {
            @Override
            public Set<BundleBuilder> getBundleBuilders() {


               PodCastBundleBuilder podCastBundleBuilder = BundleBuilder.newPodCastBundleBuilder("Toplistan", "10 bästa podcas i Sverige");

                PodCastIdCollector podCastIdCollectorBusiness = PodCastIdCollector.createPodCastIdCollector(PodCastCatalogLanguage.SE,
                        PodCastIdCollector.Category.TOPLIST_COUNTRY);
                podCastBundleBuilder.addCollector(podCastIdCollectorBusiness);

                PodCastCategoryBundleBuilder categoryBundleBuilder = BundleBuilder.newPodCastCategoryBundleBuilder("Alla Kategorier", "???..");


                PodCastIdCollector podCastIdCollector = new PodCastIdCollector(PodCastCatalogLanguage.SE,
                        PodCastIdCollector.Category.BUSINESS, PodCastIdCollector.Category.BUSINESS.name());
                categoryBundleBuilder.addCollector(podCastIdCollector);

                Set<BundleBuilder> bundleBuilders = new HashSet<>();
                bundleBuilders.add(podCastBundleBuilder);
                bundleBuilders.add(categoryBundleBuilder);

                return bundleBuilders;
            }

            @Override
            public List<Bundle> createFromFetchedData(List<PodCast> podCas,
                                                      List<PodCastCategory> podCastCategories) {

                RandomPodCastEpisodeBundleBuilder bundleBuilder = new RandomPodCastEpisodeBundleBuilder(podCas, podCastCategories);
                Bundle episodeBundle = bundleBuilder.createEpisodeBundle("Populär avsnitt");

                return Collections.singletonList(episodeBundle);
            }

            @Override
            public PodCastCatalogLanguage getPodCastCatalogLang() {
                return PodCastCatalogLanguage.SE;
            }
        });

       // PodCastCatalogService
         //       .getInstance().buildPodCastCatalogsAsync(PodCastCatalogLanguage.SE).get(3, TimeUnit.MINUTES);
         */
    }

}