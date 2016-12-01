package com.podcastcatalog.model.podcastcatalog;

import com.google.gson.Gson;
import com.podcastcatalog.TestUtil;
import org.apache.commons.lang3.ClassUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class PodCastBundleTest {

    private static final Gson GSON = new Gson();

    @Test(groups = TestUtil.SLOW_TEST)
    public void to_JSON() {
        PodCastBundle bundle = createValid().build();
        String json = GSON.toJson(bundle);
        Assert.assertNotNull(json);
    }

    @Test
    public void getBundleType() {
      Assert.assertTrue(createValid().build().getBundleType()== BundleType.PodCast);
    }

    @Test
    public void serializable() {
        PodCastBundle podCastBundle = createValid().build();
        List classes = ClassUtils.getAllInterfaces(podCastBundle.getClass());
        Assert.assertTrue(classes.contains(Serializable.class));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void podCasts_unmodifiable() {
        createValid().build().getBundleItems().add(PodCastTest.createValid().build());
    }

    @Test
    public void createPodCastBundle() {
        Assert.assertNotNull(createValid().build());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_title_null() {
        createValid().title(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_title_empty() {
        createValid().title(" ").build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_description_null() {
        createValid().description(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_description_empty() {
        createValid().description(" ").build();
    }

    @Test
    public void title() {
        Assert.assertEquals(createValid().title("test").build().getTitle(),"test");
    }

    @Test
    public void description() {
        Assert.assertEquals(createValid().description("test").build().getDescription(),"test");
    }

    @Test
    public void podCast() {
        PodCast podCast = PodCastTest.createValid().build();
        Assert.assertTrue(createValid().podCast(podCast).build().getBundleItems().contains(podCast));
    }

    @Test
    public void podCasts() {
        PodCast podCast = PodCastTest.createValid().build();
        List<PodCast> podCasts = Collections.singletonList(podCast);
        Assert.assertTrue(createValid().podCasts(podCasts).build().getBundleItems().contains(podCast));
    }

    public static PodCastBundle.Builder createValid(){
        return PodCastBundle.newBuilder().title("P3 PodCast").description("Lite beskrivnings text").
                podCast(PodCastTest.createValid().build());
    }
}