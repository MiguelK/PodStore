package com.podcastcatalog.service;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

public class HomeDirectoryLocatorTest {

    @Test
    public void locateDataDirectory() {
        HomeDirectoryLocator locator = new HomeDirectoryLocator();
        File file = locator.locateDataDirectory();

        Assert.assertTrue(file.isDirectory());
        Assert.assertTrue(file.canRead());
        Assert.assertTrue(file.canWrite());
    }

}