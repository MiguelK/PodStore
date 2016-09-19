package com.podcastcatalog.builder.podcastfether;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.builder.collector.itunes.ItunesSearchAPI;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class ItunesSearchAPITest {

    @Test(groups = TestUtil.SLOW_TEST)
    public void lookup_2() {
        ItunesSearchAPI lookup = ItunesSearchAPI.lookup(Arrays.asList(895602289, 1032687266));
        List<PodCast> podCasts = lookup.collect();
        System.out.println(podCasts);
        Assert.assertTrue(podCasts.size()==2);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void episoed_p3() {
                ItunesSearchAPI query = ItunesSearchAPI.search("term=p3&entity=podcast&limit=1");
        int episodes = query.collect().get(0).getPodCastEpisodes().size();

        Assert.assertTrue(episodes>=260);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void search_result_1() {
//       ItunesSearchAPI query = ItunesSearchAPI.create("term=java&entity=podcast&limit=1");
       ItunesSearchAPI query = ItunesSearchAPI.search("term=sommar&entity=podcast&limit=1");

        List<PodCast> fetch = query.collect();
        System.out.println(fetch);
        Assert.assertTrue(fetch.size()==1);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void empty_fetch_if_failing_query() {
        Assert.assertTrue(ItunesSearchAPI.search("http://www.x.*").collect().isEmpty());
        Assert.assertTrue(ItunesSearchAPI.search(".x.*").collect().isEmpty());
        Assert.assertTrue(ItunesSearchAPI.search("             ").collect().isEmpty());
        Assert.assertTrue(ItunesSearchAPI.search("    /         ").collect().isEmpty());
        Assert.assertTrue(ItunesSearchAPI.search(" ??????&ö-., zsasa'/\\-    ").collect().isEmpty());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void fetch_success() {
        ItunesSearchAPI searchAPI = ItunesSearchAPI.search("term=p3&entity=podcast");
        List<PodCast> fetch = searchAPI.collect();
        System.out.println("Fetch=" + fetch);
        Assert.assertFalse(fetch.isEmpty());
        Assert.assertTrue(fetch.size()==42, "Actual=" + fetch.size());
    }

}