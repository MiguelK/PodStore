package com.podcastcatalog.store;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.api.response.PodCastBundleTest;
import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

public class DiscStorageTest {

    private Storage storage;

    @BeforeMethod
    public void setUp() {
        storage = new DiscStorage(TestUtil.IO_TEMP_DATA_DIRECTORY);
        storage.delete(PodCastCatalogLanguage.Sweden);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalidDataDirectory_null() {
        new DiscStorage(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalidDataDirectory_not_directory() throws IOException {
        Path tempFile = Files.createTempFile("prefix", "suffix");
        try {
            new DiscStorage(tempFile.toFile());
        } finally {
            Files.delete(tempFile);
        }
    }

    @Test
    public void null_if_file_does_not_exist() {
        Assert.assertFalse(storage.load(PodCastCatalogLanguage.Sweden).isPresent());
    }

    @Test
    public void load_not_existing() {
        Optional<PodCastCatalog> load = storage.load(PodCastCatalogLanguage.Sweden);
        Assert.assertFalse(load.isPresent());
    }

    @Test
    public void save_load() {
        PodCastCatalog podCastCatalog1 = PodCastCatalog.create(PodCastCatalogLanguage.Sweden,
                Collections.singletonList(PodCastBundleTest.createValid().build()));

        Assert.assertFalse(storage.load(PodCastCatalogLanguage.Sweden).isPresent());

        storage.save(podCastCatalog1);

        Assert.assertTrue(storage.load(PodCastCatalogLanguage.Sweden).isPresent());
    }
}