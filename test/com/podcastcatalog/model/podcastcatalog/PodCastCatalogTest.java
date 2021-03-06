package com.podcastcatalog.model.podcastcatalog;

import com.google.gson.Gson;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PodCastCatalogTest {

    private static final Gson GSON = new Gson();

    @Test
    public void all_bunlde_types_to_JSON() {
        List<Bundle> bundles = new ArrayList<>();
        bundles.add(PodCastEpisodeBundleTest.craeteValid());
        bundles.add(PodCastBundleTest.createValid().build());
        bundles.add(PodCastCategoryBundleTest.createValid());

        PodCastCatalog podCastCatalog = PodCastCatalog.create(PodCastCatalogLanguage.SE, bundles);

        Assert.assertTrue(podCastCatalog.getBundles().size() == 3);
        String json = GSON.toJson(podCastCatalog);
        Assert.assertTrue(json.contains("\"bundleType\":\"PodCast\""));
        Assert.assertTrue(json.contains("\"bundleType\":\"Category\""));
        Assert.assertTrue(json.contains("\"bundleType\":\"Episode\""));
    }



    @Test
    public void createValid_to_JSON() {
        Assert.assertNotNull(GSON.toJson(createValidPodCastCatalogSWE()));
    }

    @Test
    public void created() {
        LocalDateTime now = LocalDateTime.now();
        Assert.assertEquals(createValidPodCastCatalogSWE().getCreated().getDayOfMonth(), now.getDayOfMonth());
        Assert.assertEquals(createValidPodCastCatalogSWE().getCreated().getDayOfWeek(), now.getDayOfWeek());
        Assert.assertEquals(createValidPodCastCatalogSWE().getCreated().getHour(), now.getHour());
        Assert.assertEquals(createValidPodCastCatalogSWE().getCreated().getMonth(), now.getMonth());
        Assert.assertEquals(createValidPodCastCatalogSWE().getCreated().getMinute(), now.getMinute());
    }

    @Test
    public void serializable() {
        PodCastCatalog podCastCatalog = createValidPodCastCatalogSWE();
        List<? extends Class<?>> classes = Arrays.asList(podCastCatalog.getClass().getInterfaces());
        Assert.assertTrue(classes.contains(Serializable.class));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void podCastBundleCategories_unmodifiable() {
        createValidPodCastCatalogSWE().getBundles().add(PodCastBundleTest.createValid().build());
    }

    @Test
    public void create_PodCastCatalog() {
        Assert.assertNotNull(createValidPodCastCatalogSWE());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podCastCatalogLanguage_null() {
        List<Bundle> podCastBundle1s = Collections.singletonList(PodCastBundleTest.createValid().build());
        PodCastCatalog.create(null, podCastBundle1s);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_bundles_null() {
        PodCastCatalog.create(PodCastCatalogLanguage.SE, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_bundles_empty() {
        PodCastCatalog.create(PodCastCatalogLanguage.SE, Collections.emptyList());
    }

    @Test
    public void podCastCatalogLanguage() {
        Assert.assertEquals(createValidPodCastCatalogSWE().getPodCastCatalogLanguage(), PodCastCatalogLanguage.SE);
    }

    @Test
    public void testToSTring() {
        Assert.assertNotNull(createValidPodCastCatalogSWE().toString());

        System.out.println(createValidPodCastCatalogSWE().toString());
    }

    public static PodCastCatalog createValidPodCastCatalogSWE() {
        List<Bundle> podCastBundle1s = new ArrayList<>();
        podCastBundle1s.add(PodCastBundleTest.createValid().build());
        podCastBundle1s.add(PodCastEpisodeBundleTest.craeteValid());
        podCastBundle1s.add(PodCastCategoryBundleTest.createValid());

        return PodCastCatalog.create(PodCastCatalogLanguage.SE, podCastBundle1s);
    }

    public static PodCastCatalog createValidPodCastCatalogUS() {
        List<Bundle> podCastBundle1s = new ArrayList<>();
        podCastBundle1s.add(PodCastBundleTest.createValid().build());
        podCastBundle1s.add(PodCastEpisodeBundleTest.craeteValid());
        podCastBundle1s.add(PodCastCategoryBundleTest.createValid());

        return PodCastCatalog.create(PodCastCatalogLanguage.US, podCastBundle1s);
    }
}