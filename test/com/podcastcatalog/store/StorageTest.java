package com.podcastcatalog.store;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class StorageTest {

    @BeforeMethod
    public void setUp() {
        Storage.delete(PodCastCatalogLanguage.Sweden);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalidDataDirectory_null() {
        DiscStorage.setDataDirectory(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalidDataDirectory_not_directory() throws IOException {
        Path tempFile = Files.createTempFile("prefix", "suffix");
        try{
            DiscStorage.setDataDirectory(tempFile.toFile());
        }finally {
            Files.delete(tempFile);
        }
    }

    @Test
    public void load_not_existing() {
        Optional<PodCastCatalog> load = Storage.load(PodCastCatalogLanguage.Sweden);
        Assert.assertFalse(load.isPresent());
    }

    /*@Test
    public void save_load() {
        PodCastCatalog podCastCatalog = PodCastCatalog.create(PodCastCatalogLanguage.Sweden,
                Arrays.asList(PodCastBundle1Test.createValid().build(), PodCastBundle1Test.createValid().build()),
                Collections.singletonList(PodCastBundle1Test.createValid().build()));
        Storage.save(podCastCatalog);

        Optional<PodCastCatalog> load = Storage.load(PodCastCatalogLanguage.Sweden);

        Assert.assertTrue(load.isPresent());
    }*/
}