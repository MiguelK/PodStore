package com.podcastcatalog;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.builder.BundleBuilder;
import com.podcastcatalog.builder.PodCastBundleBuilder;
import com.podcastcatalog.builder.PodCastCatalogBuilder;
import com.podcastcatalog.builder.PodCastCatalogBuilderSE;
import com.podcastcatalog.builder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.store.DataStorage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class PodCastCatalogServiceTest {
    private DataStorage storage;

    @BeforeMethod
    public void setUp() {
        storage = new DataStorage(TestUtil.IO_TEMP_DATA_DIRECTORY_1);
        storage.deleteAll();
    }

    @Test
    public void buildPodCastCatalogs() {
        PodCastCatalogService.getInstance().setStorage(storage);

        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilder() {
            @Override
            public Set<BundleBuilder> getBundleBuilders() {

                PodCastBundleBuilder topListBuilder = BundleBuilder.newPodCastBundleBuilder("image", "Toplistan", "10 bästa podcas i Sverige");
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

//    @Test(groups = TestUtil.SLOW_TEST)
    public void build_load_catalog() {

        File catalogDir = new File("/home/krantmig/tools/temp");//FIXME

        DataStorage discStorage = new DataStorage(catalogDir);

        PodCastCatalogService.getInstance().setStorage(discStorage);
        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilderSE());

        PodCastCatalogService
                .getInstance().buildPodCastCatalogs();

        //1# App start get current built catalog
        PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);

        TestUtil.assertToJSONNotNull(podCastCatalog);

        discStorage.save(podCastCatalog);
    }

}