package com.podcastcatalog;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.builder.BundleBuilder;
import com.podcastcatalog.builder.PodCastBundleBuilder;
import com.podcastcatalog.builder.PodCastCatalogBuilder;
import com.podcastcatalog.builder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.store.DiscStorage;
import com.podcastcatalog.store.Storage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

public class PodCastCatalogServiceTest {
    private Storage storage;

    @BeforeMethod
    public void setUp() {
        storage = new DiscStorage(TestUtil.IO_TEMP_DATA_DIRECTORY);
        storage.delete(PodCastCatalogLanguage.Sweden);
    }

    @Test
    public void testName() {

        PodCastCatalogService.getInstance().setStorage(storage);

        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilder() {
            @Override
            public Set<BundleBuilder> getBundleBuilders() {

                PodCastBundleBuilder.Builder topListBuilder = PodCastBundleBuilder.newBuilder("image", "Toplistan", "10 bästa podcas i Sverige");
                topListBuilder.addCollector(ItunesSearchAPI.search("term=p3&entity=podcast&limit=1"));

                Set<BundleBuilder> bundleBuilders = new HashSet<>();
                bundleBuilders.add(topListBuilder.build());

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

 /*   @Test(groups = TestUtil.SLOW_TEST)
    public void build_load_catalog() {

//        DiscStorage.setDataDirectory(); //Data dir in Open Shift?
        //0#
        PodCastCatalogService
                .getInstance().buildPodCastCatalogs();

        //1# App start get current built catalog
        PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);
        System.out.println(podCastCatalog);
        //podCastCatalog.toJSON()->

        TestUtil.assertToJSONNotNull(podCastCatalog);
     }*/

}