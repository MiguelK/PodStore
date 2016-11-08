package com.podcastcatalog.api.util;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogTest;
import org.testng.annotations.Test;

public class StringFormatterTest {


    @Test
    public void format() {
        PodCastCatalog valid = PodCastCatalogTest.createValid();

        StringFormatter formatter = StringFormatter.create(valid);

        System.out.println(formatter.format());
    }
}