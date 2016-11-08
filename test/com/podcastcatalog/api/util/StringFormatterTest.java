package com.podcastcatalog.api.util;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StringFormatterTest {


    @Test
    public void format() {
        PodCastCatalog valid = PodCastCatalogTest.createValid();
        Assert.assertNotNull(StringFormatter.create(valid));
    }
}