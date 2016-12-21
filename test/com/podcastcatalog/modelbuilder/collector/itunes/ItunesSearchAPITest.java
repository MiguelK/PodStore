package com.podcastcatalog.modelbuilder.collector.itunes;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

public class ItunesSearchAPITest {

    @Test(groups = TestUtil.SLOW_TEST)
    public void lookup_2() {
        ItunesSearchAPI lookup = ItunesSearchAPI.lookup(Arrays.asList(895602289L, 1032687266L));
        List<PodCast> podCasts = lookup.collectPodCasts();
        Assert.assertTrue(podCasts.size() == 2);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void parse_25_podcasts() {
        ItunesSearchAPI query = ItunesSearchAPI.search("term=a&entity=podcast&limit=25");
        int podCasts = query.collectPodCasts().size();
        System.out.println(podCasts);

//        Assert.assertTrue(episodes >= 100, "episodes=" + episodes);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void episoed_p3() {
        ItunesSearchAPI query = ItunesSearchAPI.search("term=p3&entity=podcast&limit=1");
        int episodes = query.collectPodCasts().get(0).getPodCastEpisodes().size();

        Assert.assertTrue(episodes >= 100, "episodes=" + episodes);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void search_result_1() {
        ItunesSearchAPI query = ItunesSearchAPI.search("term=sommar&entity=podcast&limit=1");

        List<PodCast> fetch = query.collectPodCasts();
        Assert.assertTrue(fetch.size() == 1);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void episode_id_unique() {
        ItunesSearchAPI query = ItunesSearchAPI.search("term=sommar&entity=podcast&limit=3");

        List<PodCast> podCasts = query.collectPodCasts();

        List<PodCastEpisode> episodes = new ArrayList<>();

        podCasts.forEach(p -> episodes.addAll(p.getPodCastEpisodes()));

        Set<String> ids = new HashSet<>();
        for (PodCastEpisode episode : episodes) {
            Assert.assertFalse(ids.contains(episode.getId()));
            ids.add(episode.getId());
        }

        Assert.assertFalse(episodes.isEmpty());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void empty_fetch_if_failing_query() {
        Assert.assertTrue(ItunesSearchAPI.search("http://www.x.*").collectPodCasts().isEmpty());
        Assert.assertTrue(ItunesSearchAPI.search(".x.*").collectPodCasts().isEmpty());
        Assert.assertTrue(ItunesSearchAPI.search("             ").collectPodCasts().isEmpty());
        Assert.assertTrue(ItunesSearchAPI.search("    /         ").collectPodCasts().isEmpty());
        Assert.assertTrue(ItunesSearchAPI.search(" ??????&ö-., zsasa'/\\-    ").collectPodCasts().isEmpty());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void fetch_success() {
        ItunesSearchAPI searchAPI = ItunesSearchAPI.search("term=p3&entity=podcast");
        List<PodCast> podCasts = searchAPI.collectPodCasts();
        Assert.assertFalse(podCasts.isEmpty());
        Assert.assertTrue(podCasts.size() == 41, "Actual=" + podCasts.size());
    }

}