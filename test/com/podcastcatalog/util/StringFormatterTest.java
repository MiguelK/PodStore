package com.podcastcatalog.util;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalog;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StringFormatterTest {


    @Test(groups = TestUtil.SLOW_TEST)
    public void format() {
        PodCastCatalog valid = PodCastCatalogTest.createValidPodCastCatalogSWE();
        StringFormatter object = StringFormatter.create(valid);
        Assert.assertNotNull(object.format());
    }
}