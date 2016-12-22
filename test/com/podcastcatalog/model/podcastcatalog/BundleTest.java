package com.podcastcatalog.model.podcastcatalog;

import com.podcastcatalog.TestUtil;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

public class BundleTest {

    private Bundle bundle;

    @BeforeMethod(groups = TestUtil.SLOW_TEST)
    public void setUp() {
        bundle = new PodCastBundle(" test ", " desc ", Collections.singletonList(PodCastTest.createValid().build()));
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void trim_input() {
        Assert.assertEquals(bundle.getTitle(), "test");
        Assert.assertEquals(bundle.getDescription(), "desc");
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void testGetBundleItems() {
        Assert.assertTrue(bundle.getBundleItems().size() == 1);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void getBundleType() {
        Assert.assertTrue(new PodCastBundle(" test ", " desc ", Collections.singletonList(PodCastTest.createValid().build())).getBundleType() == BundleType.PodCast);
        Assert.assertTrue(new PodCastEpisodeBundle(" test ", " desc ", Collections.singletonList(PodCastEpisodeTest.createValid().build())).getBundleType() == BundleType.Episode);
        Assert.assertTrue(new PodCastCategoryBundle(" test ", " desc ", Collections.singletonList(PodCastCategoryTest.createValid())).getBundleType() == BundleType.Category);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void testToString() {
        Bundle bundle = new PodCastBundle(" test ", " desc ", Collections.singletonList(PodCastTest.createValid().build()));
        Assert.assertTrue(bundle.toString().contains("1"));
    }
}