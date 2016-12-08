package com.podcastcatalog.storage;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.PodCastBundleTest;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalog;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogTest;
import com.podcastcatalog.model.subscription.SubscriptionData;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

public class DataStorageTest {

    private DataStorage storage;

    @BeforeMethod
    public void setUp() {
        storage = new DataStorage();
        storage.deleteAll();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalidDataDirectory_null() {
        new DataStorage(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalidDataDirectory_not_directory() throws IOException {
        Path tempFile = Files.createTempFile("prefix", "suffix");
        try {
            new DataStorage(tempFile.toFile());
        } finally {
            Files.delete(tempFile);
        }
    }

    @Test
    public void null_if_file_does_not_exist() {
        Assert.assertFalse(storage.getCurrentVersion().isPresent());
    }

    @Test
    public void save_load() {
        PodCastCatalog podCastCatalog1 = PodCastCatalog.create(PodCastCatalogLanguage.Sweden,
                Collections.singletonList(PodCastBundleTest.createValid().build()));

        Assert.assertFalse(storage.getCurrentVersion().isPresent());

        storage.save(podCastCatalog1);

        Assert.assertNotNull(storage.getCurrentVersion().orElseGet(null).getPodCastCatalogSwedish());
    }

    @Test
    public void save_2_versions() {

        storage.save(PodCastCatalogTest.createValid());
        Assert.assertTrue(storage.getCurrentVersion().orElseGet(null).getVersion() == 1);

        storage.save(PodCastCatalogTest.createValid());
        Assert.assertTrue(storage.getCurrentVersion().orElseGet(null).getVersion() == 2);
    }

    @Test
    public void no_versions() {
        Assert.assertTrue(storage.getAllVersions().isEmpty());
    }

    @Test
    public void create_3_versions() {
        storage.save(PodCastCatalogTest.createValid());
        storage.save(PodCastCatalogTest.createValid());
        storage.save(PodCastCatalogTest.createValid());

        Assert.assertTrue(storage.getAllVersions().size() == 3);
    }

    @Test
    public void versionPaths() {
        storage.save(PodCastCatalogTest.createValid());

        for (DataStorage.PodCastCatalogVersion version : storage.getAllVersions()) {
            Assert.assertNotNull(version.getSweDat());
            Assert.assertNotNull(version.getSweJSON());
            Assert.assertNotNull(version.getSweJSONZipped());
        }
    }

    @Test
    public void verify_version_structure() throws IOException {
        storage.save(PodCastCatalogTest.createValid());

        File dataDirectory = TestUtil.IO_TEMP_DATA_DIRECTORY;
        File root = new File(dataDirectory,"PodCastCatalogVersions");
        File one = new File(root,"1");

        Assert.assertTrue(one.isDirectory());
        Assert.assertTrue(one.canRead());
        Assert.assertTrue(one.canWrite());

        File sweDat = new File(one,PodCastCatalogLanguage.Sweden.name() + ".dat");
        File sweJSON = new File(one,PodCastCatalogLanguage.Sweden.name() + ".json");
        File sweZIP = new File(one,PodCastCatalogLanguage.Sweden.name() + "_json.zip");

        Assert.assertTrue(sweDat.isFile());
        Assert.assertTrue(sweDat.canRead());
        Assert.assertTrue(Files.size(sweDat.toPath()) >100);

        Assert.assertTrue(sweJSON.isFile());
        Assert.assertTrue(sweJSON.canRead());
        Assert.assertTrue(Files.size(sweJSON.toPath()) >100,"was=" + Files.size(sweJSON.toPath()));

        Assert.assertTrue(sweZIP.isFile());
        Assert.assertTrue(sweZIP.canRead());
        Assert.assertTrue(Files.size(sweZIP.toPath()) >100);
    }

    @Test
    public void verify_version_directories() {
        storage.save(PodCastCatalogTest.createValid());
        storage.save(PodCastCatalogTest.createValid());
        storage.save(PodCastCatalogTest.createValid());
        storage.save(PodCastCatalogTest.createValid());

        File root = storage.getCatalogVersionHomeDir();

        assertDirectory(new File(root,"1"));
        assertDirectory(new File(root,"2"));
        assertDirectory(new File(root,"3"));
        assertDirectory(new File(root,"4"));
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void verify_order() throws InterruptedException {
        storage.save(PodCastCatalogTest.createValid());

        Thread.sleep(1000);

        PodCastCatalog castCatalog = PodCastCatalogTest.createValid();
        LocalDateTime created = castCatalog.getCreated();
        storage.save(castCatalog);

        Optional<DataStorage.PodCastCatalogVersion> currentVersion = storage.getCurrentVersion();

        PodCastCatalog podCastCatalogSwedish = currentVersion.orElseGet(null).getPodCastCatalogSwedish();

        Assert.assertEquals(podCastCatalogSwedish.getCreated(), created);
    }

    @Test
    public void deleteAll() {
        Assert.assertTrue(storage.getAllVersions().isEmpty());

        storage.save(PodCastCatalogTest.createValid());
        storage.save(PodCastCatalogTest.createValid());
        storage.save(PodCastCatalogTest.createValid());

        Assert.assertTrue(storage.getAllVersions().size()==3);

        storage.deleteAll();

        Assert.assertTrue(storage.getAllVersions().isEmpty());
    }

    @Test
    public void zipJSON() throws IOException {
        storage.save(PodCastCatalogTest.createValid());

        File zipped = storage.getCurrentVersion().orElseGet(null).getSweJSONZipped();
        Assert.assertNotNull(zipped);
        Assert.assertTrue(zipped.isFile());
        Assert.assertTrue(zipped.canRead());
        Assert.assertTrue(zipped.getName().endsWith("_json.zip"));
        Assert.assertTrue(Files.size(zipped.toPath()) >100);
    }

    @Test
    public void saveLoadSubscriptionData() {
        storage.save(new SubscriptionData());
        Assert.assertTrue(storage.getSubscriptionDataFile().exists());

        SubscriptionData subscriptionData = storage.loadSubscriptionData();

        Assert.assertNotNull(subscriptionData);
    }

    private void assertDirectory(File file){
        Assert.assertTrue(file.isDirectory());
        Assert.assertTrue(file.canRead());
        Assert.assertTrue(file.canWrite());
    }
}