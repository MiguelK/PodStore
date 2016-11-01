package com.podcastcatalog.api.response;

import com.google.gson.Gson;
import com.podcastcatalog.TestUtil;
import com.podcastcatalog.PodCastTest;
import com.podcastcatalog.api.response.bundle.BundleType;
import com.podcastcatalog.api.response.bundle.PodCastBundle;
import org.apache.commons.lang.ClassUtils;
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
        System.out.println(json);
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

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void no_podcasts() {
        PodCastBundle.newBuilder().title("dsdsd").description("dsdsds").imageURL("sdsdsd").build();
    }
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void podcasts_null_object() {
        PodCastBundle.newBuilder().title("dsdsd").description("dsdsds").imageURL("sdsdsd").podCast(null).build();
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

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_imageURL_null() {
        createValid().imageURL(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_imageURLn_empty() {
        createValid().imageURL(" ").build();
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
    public void imageURL() {
        Assert.assertEquals(createValid().imageURL("test").build().getImageURL(),"test");
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


    /*@Test
        public void numberOfPodCastsInBundele() {
            Assert.assertEquals(createValid().build().getNumberOfPodCastsInBundele(), 1);
        }
    */
    public static PodCastBundle.Builder createValid(){
        return PodCastBundle.newBuilder().title("dsdsd").description("dsdsds").imageURL("sdsdsd").
                podCast(PodCastTest.createValid().build());
    }
}