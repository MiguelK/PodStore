package com.podcastcatalog.service;

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

public class ServiceDataStorageDiskTest {

    private ServiceDataStorage storage;

    @BeforeMethod(groups = TestUtil.SLOW_TEST)
    public void setUp() {
        storage = ServiceDataStorage.useDefault();
        storage.deleteAll();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalidDataDirectory_null() {
        new ServiceDataStorageDisk(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalidDataDirectory_not_directory() throws IOException {
        Path tempFile = Files.createTempFile("prefix", "suffix");
        try {
            new ServiceDataStorageDisk(tempFile.toFile());
        } finally {
            Files.delete(tempFile);
        }
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void null_if_file_does_not_exist() {
        Assert.assertFalse(storage.getCurrentVersion(PodCastCatalogLanguage.SWE).isPresent());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void save_load() {
        PodCastCatalog podCastCatalog1 = PodCastCatalog.create(PodCastCatalogLanguage.SWE,
                Collections.singletonList(PodCastBundleTest.createValid().build()));

        Assert.assertFalse(storage.getCurrentVersion(PodCastCatalogLanguage.SWE).isPresent());

        storage.save(podCastCatalog1);

        Assert.assertNotNull(storage.getCurrentVersion(PodCastCatalogLanguage.SWE).orElseGet(null).getPodCastCatalog());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void save_2_versions() {

        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());
        Assert.assertTrue(storage.getCurrentVersion(PodCastCatalogLanguage.SWE).orElseGet(null).getVersion() == 1);

        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());
        Assert.assertTrue(storage.getCurrentVersion(PodCastCatalogLanguage.SWE).orElseGet(null).getVersion() == 2);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void no_versions() {
        Assert.assertTrue(storage.getAllVersions(PodCastCatalogLanguage.SWE).isEmpty());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void create_3_versions() {
        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());
        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());
        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());

        Assert.assertTrue(storage.getAllVersions(PodCastCatalogLanguage.SWE).size() == 3);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void versionPaths() {
        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());

        for (ServiceDataStorage.PodCastCatalogVersion version : storage.getAllVersions(PodCastCatalogLanguage.SWE)) {
            Assert.assertNotNull(version.getLangDat());
            Assert.assertNotNull(version.getLangJSON());
            Assert.assertNotNull(version.getLangJSONZipped());
        }
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void verify_version_structure() throws IOException {
        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());
        storage.save(PodCastCatalogTest.createValidPodCastCatalogUS());

        File dataDirectory = storage.getPodDataHomeDir();
        File oneSWE = new File(dataDirectory, "PodCastCatalogVersions" + File.separator + PodCastCatalogLanguage.SWE.name() + File.separator + "1");
        File oneUS = new File(dataDirectory, "PodCastCatalogVersions" + File.separator + PodCastCatalogLanguage.US.name() + File.separator + "1");

        System.out.println("PATH= " + oneSWE.getAbsolutePath());

        assertDirectoryStructure(oneSWE, PodCastCatalogLanguage.SWE);
        assertDirectoryStructure(oneUS, PodCastCatalogLanguage.US);
    }

    private void assertDirectoryStructure(File oneSWE, PodCastCatalogLanguage language) throws IOException {
        File sweDat = new File(oneSWE, language.name() + ".dat");
        File sweJSON = new File(oneSWE, language.name() + ".json");
        File sweZIP = new File(oneSWE, language.name() + "_json.zip");

        Assert.assertTrue(sweDat.isFile());
        Assert.assertTrue(sweDat.canRead());
        Assert.assertTrue(Files.size(sweDat.toPath()) > 100);

        Assert.assertTrue(sweJSON.isFile());
        Assert.assertTrue(sweJSON.canRead());
        Assert.assertTrue(Files.size(sweJSON.toPath()) > 100, "was=" + Files.size(sweJSON.toPath()));

        Assert.assertTrue(sweZIP.isFile());
        Assert.assertTrue(sweZIP.canRead());
        Assert.assertTrue(Files.size(sweZIP.toPath()) > 100);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void verify_version_directories() {
        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());
        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());
        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());
        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());

        File root = storage.getCatalogVersionHomeDirSWE();

        assertDirectory(new File(root, "1"));
        assertDirectory(new File(root, "2"));
        assertDirectory(new File(root, "3"));
        assertDirectory(new File(root, "4"));
    }

    @Test //(groups = TestUtil.SLOW_TEST)
    public void verify_order() throws InterruptedException {
        storage = ServiceDataStorage.useDefault();
        storage.deleteAll();
        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());

        PodCastCatalog castCatalog = PodCastCatalogTest.createValidPodCastCatalogSWE();
        LocalDateTime created = castCatalog.getCreated();
        storage.save(castCatalog);

        Optional<ServiceDataStorageDisk.PodCastCatalogVersion> currentVersion = storage.getCurrentVersion(PodCastCatalogLanguage.SWE);

        PodCastCatalog podCastCatalogSwedish = currentVersion.orElseGet(null).getPodCastCatalog();

        Assert.assertEquals(podCastCatalogSwedish.getCreated(), created);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void deleteAll() {
        Assert.assertTrue(storage.getAllVersions(PodCastCatalogLanguage.SWE).isEmpty());

        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());
        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());
        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());

        Assert.assertTrue(storage.getAllVersions(PodCastCatalogLanguage.SWE).size() == 3);

        storage.deleteAll();

        Assert.assertTrue(storage.getAllVersions(PodCastCatalogLanguage.SWE).isEmpty());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void zipJSON() throws IOException {
        storage.save(PodCastCatalogTest.createValidPodCastCatalogSWE());

        File zipped = storage.getCurrentVersion(PodCastCatalogLanguage.SWE).orElseGet(null).getLangJSONZipped();
        Assert.assertNotNull(zipped);
        Assert.assertTrue(zipped.isFile());
        Assert.assertTrue(zipped.canRead());
        Assert.assertTrue(zipped.getName().endsWith("_json.zip"));
        Assert.assertTrue(Files.size(zipped.toPath()) > 100);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void saveLoadSubscriptionData() {
        storage.save(new SubscriptionData());
        Assert.assertTrue(storage.getSubscriptionDataFile().exists());

        SubscriptionData subscriptionData = storage.loadSubscriptionData();

        Assert.assertNotNull(subscriptionData);
    }

    private void assertDirectory(File file) {
        Assert.assertTrue(file.isDirectory());
        Assert.assertTrue(file.canRead());
        Assert.assertTrue(file.canWrite());
    }
}