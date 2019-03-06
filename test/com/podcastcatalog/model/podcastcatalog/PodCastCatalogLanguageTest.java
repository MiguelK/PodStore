package com.podcastcatalog.model.podcastcatalog;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PodCastCatalogLanguageTest {

    @Test
    public void fromString_found() {
       Assert.assertTrue(PodCastCatalogLanguage.fromString(" swe ")== PodCastCatalogLanguage.SE);
       Assert.assertTrue(PodCastCatalogLanguage.fromString("SE")== PodCastCatalogLanguage.SE);
       Assert.assertTrue(PodCastCatalogLanguage.fromString(" Swe ")== PodCastCatalogLanguage.SE);
    }

    @Test
    public void fromString_not_found() {
        Assert.assertNull(PodCastCatalogLanguage.fromString(" sse "));
        Assert.assertNull(PodCastCatalogLanguage.fromString("  "));
        Assert.assertNull(PodCastCatalogLanguage.fromString(null));
    }
}