package com.podcastcatalog.service.datastore;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

public class LocatorProductionTest {

    @Test
    public void locateDataDirectory() {
        LocatorProduction locator = new LocatorProduction();
        File file = locator.getPodDataHomeDirectory();

        Assert.assertTrue(file.isDirectory());
        Assert.assertTrue(file.canRead());
        Assert.assertTrue(file.canWrite());
    }

}