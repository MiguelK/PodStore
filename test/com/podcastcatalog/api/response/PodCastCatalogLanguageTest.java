package com.podcastcatalog.api.response;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PodCastCatalogLanguageTest {

    @Test
    public void fromString_found() {
       Assert.assertTrue(PodCastCatalogLanguage.fromString(" se ")== PodCastCatalogLanguage.Sweden);
       Assert.assertTrue(PodCastCatalogLanguage.fromString("SE")== PodCastCatalogLanguage.Sweden);
       Assert.assertTrue(PodCastCatalogLanguage.fromString(" Se ")== PodCastCatalogLanguage.Sweden);
    }

    @Test
    public void fromString_not_found() {
        Assert.assertNull(PodCastCatalogLanguage.fromString(" sse "));
        Assert.assertNull(PodCastCatalogLanguage.fromString("  "));
        Assert.assertNull(PodCastCatalogLanguage.fromString(null));
    }
}