package com.podcastcatalog;

import com.google.gson.Gson;
import com.podcastcatalog.api.response.PodCastBundleTest;
import com.podcastcatalog.api.response.PodCastCatalogLanguageTest;
import com.podcastcatalog.api.response.PodCastCatalogTest;
import com.podcastcatalog.api.response.PodCastCategoryTest;
import com.podcastcatalog.api.response.PodCastCategoryTypeTest;
import com.podcastcatalog.api.response.PodCastEpisodeDurationTest;
import com.podcastcatalog.api.response.PodCastEpisodeFileSizeTest;
import com.podcastcatalog.api.response.PodCastEpisodeTest;
import com.podcastcatalog.api.response.PodCastEpisodeTypeTest;
import com.podcastcatalog.builder.PodCastBundle1TaskTest;
import com.podcastcatalog.builder.PodCastBundleBuilderTest;
import com.podcastcatalog.builder.PodCastCatalogBuilderServiceTest;
import com.podcastcatalog.builder.collector.okihika.PodCastCategoryCollectorOkihikaTest;
import com.podcastcatalog.builder.podcastfether.ItunesSearchAPITest;
import com.podcastcatalog.builder.podcastfether.PodCastCollectorOkihikaTest;
import com.podcastcatalog.builder.podcastfether.PodCastFeedParserTest;
import com.podcastcatalog.builder.podcastfether.PodCastID;
import com.podcastcatalog.builder.podcastfether.PodCastIDTest;
import com.podcastcatalog.store.StorageTest;
import org.apache.commons.lang.ClassUtils;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.List;

public class TestUtil {

    public static final String SLOW_TEST = "slow";

    private static final Gson GSON = new Gson();


    public static void assertSerializable(Object obj) {
        List classes = ClassUtils.getAllInterfaces(obj.getClass());
        Assert.assertTrue(classes.contains(Serializable.class));
    }

    public static void assertToJSONNotNull(Object obj) {
        Assert.assertNotNull(GSON.toJson(obj));
    }
    public static void assertToJSONContains(Object obj, String json) {
        Assert.assertTrue(GSON.toJson(obj).contains(json));
    }

//    @Test
    public void quick() {

        TestListenerAdapter testListenerAdapter = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{PodCastBundleTest.class,
                PodCastCatalogLanguageTest.class,
                PodCastCatalogLanguageTest.class,
                PodCastCatalogTest.class,
                PodCastCategoryTest.class,
                PodCastCategoryTypeTest.class,
                PodCastEpisodeDurationTest.class,
                PodCastEpisodeFileSizeTest.class,
                PodCastEpisodeTest.class,
                PodCastEpisodeTypeTest.class,
                PodCastTest.class,
                PodCastCategoryCollectorOkihikaTest.class,
                ItunesSearchAPITest.class,
                PodCastCollectorOkihikaTest.class,
                PodCastFeedParserTest.class,
                PodCastID.class,
                PodCastIDTest.class,
                PodCastBundle1TaskTest.class,
                PodCastBundleBuilderTest.class,
                PodCastCatalogBuilderServiceTest.class,
                StorageTest.class,
                DataProviderTestData.class,
                PodCastCatalogServiceTest.class,
                TestUtil.class});


        testng.setExcludedGroups(SLOW_TEST);
        testng.addListener(testListenerAdapter);

        testng.run();

        List<ITestResult> failedTests = testListenerAdapter.getFailedTests();
        Assert.assertTrue(failedTests.isEmpty(), String.valueOf(failedTests));
        Assert.assertTrue(testListenerAdapter.getPassedTests().size() == 15, String.valueOf(failedTests));
    }

    @Test
    public void slow() {

        TestListenerAdapter testListenerAdapter = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{PodCastBundleTest.class});
        testng.setGroups(SLOW_TEST);

        testng.addListener(testListenerAdapter);

        testng.run();

        List<ITestResult> failedTests = testListenerAdapter.getFailedTests();
        Assert.assertTrue(failedTests.isEmpty(), String.valueOf(failedTests));
        Assert.assertTrue(testListenerAdapter.getPassedTests().size() == 1, String.valueOf(failedTests));

    }
}
