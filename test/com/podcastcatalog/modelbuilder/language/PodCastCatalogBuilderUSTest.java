package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PodCastCatalogBuilderUSTest {


    @Test
    public void testGetBundleBuilders() {
        PodCastCatalogBuilderUS builderUS = new PodCastCatalogBuilderUS();

        Assert.assertTrue(builderUS.getPodCastCatalogLang()== PodCastCatalogLanguage.US);
    }

}