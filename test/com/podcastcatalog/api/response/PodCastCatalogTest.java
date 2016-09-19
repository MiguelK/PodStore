package com.podcastcatalog.api.response;

import com.google.gson.Gson;
import com.podcastcatalog.api.response.bundle.Bundle;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PodCastCatalogTest {

    private static final Gson GSON = new Gson();

    @Test
    public void to_JSON() {
        Assert.assertNotNull(GSON.toJson(createValid()));
    }

    @Test
    public void created() {
        LocalDateTime now = LocalDateTime.now();
        Assert.assertEquals(createValid().getCreated().getDayOfMonth(), now.getDayOfMonth());
        Assert.assertEquals(createValid().getCreated().getDayOfWeek(), now.getDayOfWeek());
        Assert.assertEquals(createValid().getCreated().getHour(), now.getHour());
        Assert.assertEquals(createValid().getCreated().getMonth(), now.getMonth());
        Assert.assertEquals(createValid().getCreated().getMinute(), now.getMinute());
    }

    @Test
    public void serializable() {
       PodCastCatalog podCastCatalog = createValid();
       List<? extends Class<?>> classes = Arrays.asList(podCastCatalog.getClass().getInterfaces());
        Assert.assertTrue(classes.contains(Serializable.class));
    }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void podCastBundleCategories_unmodifiable() {
       createValid().getBundles().add(PodCastBundleTest.createValid().build());
   }


        @Test
        public void create_PodCastCatalog() {
           Assert.assertNotNull(createValid());
        }

        @Test(expectedExceptions = IllegalArgumentException.class)
        public void invalid_podCastCatalogLanguage_null() {
            List<Bundle> podCastBundle1s = Collections.singletonList(PodCastBundleTest.createValid().build());
            PodCastCatalog.create(null, podCastBundle1s);
        }

        @Test(expectedExceptions = IllegalArgumentException.class)
        public void invalid_bundles_null() {
            PodCastCatalog.create(PodCastCatalogLanguage.Sweden, null);
        }

        @Test(expectedExceptions = IllegalArgumentException.class)
        public void invalid_bundles_empty() {
            PodCastCatalog.create(PodCastCatalogLanguage.Sweden, Collections.emptyList());
        }

        @Test
        public void podCastCatalogLanguage() {
            Assert.assertEquals(createValid().getPodCastCatalogLanguage(),PodCastCatalogLanguage.Sweden);
        }

        @Test
        public void testToSTring() {
            Assert.assertNotNull(createValid().toString());
        }

    private PodCastCatalog createValid() {
        List<Bundle> podCastBundle1s = Collections.singletonList(PodCastBundleTest.createValid().build());
        return PodCastCatalog.create(PodCastCatalogLanguage.Sweden, podCastBundle1s);
    }
}