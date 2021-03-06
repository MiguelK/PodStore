package com.podcastcatalog.modelbuilder.collector.itunes;

import com.icosillion.podengine.models.Podcast;
import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ItunesSearchAPITest {

    @Test(groups = TestUtil.SLOW_TEST)
    public void lookupPodCast() {

        Assert.assertNotNull(ItunesSearchAPI.lookupPodCast("534137041", 1).get());
        Assert.assertNotNull(ItunesSearchAPI.lookupPodCast("1477956385", 1).get());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void lookup_2() {
        List<PodCast> podCasts =  ItunesSearchAPI.lookupPodCasts(Arrays.asList( 1032687266L));
        Assert.assertTrue(podCasts.size() == 1);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void lookup_invalid_params() {
        Assert.assertTrue(ItunesSearchAPI.lookupPodCasts((List<Long>) null).isEmpty());
        Assert.assertTrue(ItunesSearchAPI.lookupPodCasts(Collections.singletonList(-232L)).isEmpty());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void lookup_success() {
        Assert.assertTrue(ItunesSearchAPI.lookupPodCast("895602289").isPresent());
    }


    @Test(groups = TestUtil.SLOW_TEST)
    public void getLatestEpisodeIdForPodCast() {
        Assert.assertNotNull(ItunesSearchAPI.getFeedURLFromPodCast("377218713"));
        Assert.assertNotNull(ItunesSearchAPI.getFeedURLFromPodCast("1477956385"));
        Assert.assertNotNull(ItunesSearchAPI.getFeedURLFromPodCast("676144099"));
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void fetchLatestEpisode() {

        try {
            Document doc = Jsoup.parse(new URL("https://podcasts.apple.com/us/podcast/le-cours-de-lhistoire/id390164336?uo=4"), 3000);

            // Element table = doc.getElementById("itunes_result_table");
            // Elements elements = table.getElementsByAttribute("href");
            Element elementById = doc.getElementById("shoebox-ember-data-store");
            //System.out.println("doc " + elementById.toString());

            String htmlContent = elementById.toString();
            int feedUrl2 = htmlContent.indexOf("feedUrl");

            String substring = htmlContent.substring(feedUrl2);
            int startIndex = substring.indexOf(":") + 1;
            int endIndex = substring.indexOf(",");

            String substring1 = substring.substring(startIndex, endIndex);
            //System.out.println(substring1);
            String s = StringUtils.trimToNull(substring1);
            String replace = s;//s.replace("\"", "");

            URL feedURL = new URL(replace);
            // System.out.println(feedURL);

            Podcast podcast = new Podcast(feedURL);

            System.out.println("podcast=" + podcast.getTitle());

            // htmlContent.sub

            //feedUrl":"https://rss.podplaystudio.com/447.xml",

            //     System.out.println("feedUrl1 " + feedUrl1);


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    @Test(groups = TestUtil.SLOW_TEST)
    public void serial() {
        PodCast podCast = ItunesSearchAPI.lookupPodCast("1509518970").get();
        System.out.println(podCast.getTitle());
    }
        @Test(groups = TestUtil.SLOW_TEST)
    public void lookup_check_episodes() {
        PodCast podCast = ItunesSearchAPI.lookupPodCast("270572701").get();

        //308339623-shakenbabyskandalen
        PodCastEpisode latestPodCastEpisode = podCast.getLatestPodCastEpisode();
        System.out.println(podCast.getTitle() + " episodes=" + podCast.getPodCastEpisodesInternal().size());
        System.out.println("latestPodCastEpisode=" + latestPodCastEpisode.getTitle() + ", id=" + latestPodCastEpisode.getId());
    }


    @Test
    public void testPodCastSmall() throws Exception {
        ItunesSearchAPI.PodCastSmall podCastSmall = ItunesSearchAPI.getLatestEpisodeIdFromPodCast("270572701", new URL("https://www.spanishpod101.com/wp-feed-audio-video.php"));

        System.out.println(podCastSmall);
    }

    @Test
    public void isLatestEpisodeUpdated_false() throws Exception {

        String latest=   "308339623-shakenbabyskandalen";
        String  remote=  "308339623-shakenbabyskandalen";

        boolean isLatestEpisodeUpdated = !remote.equals(latest);
        System.out.println("isLatestEpisodeUpdated= " + isLatestEpisodeUpdated);

        Assert.assertFalse(isLatestEpisodeUpdated);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void parse_1_podcasts() {
        ItunesSearchAPI query = ItunesSearchAPI.createCollector("term=Spår&entity=podcast&limit=1&country=SE");
        int podCasts = query.collectPodCasts().size();

        Assert.assertTrue(podCasts==1);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void episoed_p3() {
        ItunesSearchAPI query = ItunesSearchAPI.createCollector("term=p3&entity=podcast&limit=1");
        int episodes = query.collectPodCasts().get(0).getPodCastEpisodesInternal().size();

        Assert.assertTrue(episodes >= 100, "episodes=" + episodes);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void search_result_1() {
        ItunesSearchAPI query = ItunesSearchAPI.createCollector("term=sommar&entity=podcast&limit=1");

        List<PodCast> fetch = query.collectPodCasts();
        Assert.assertTrue(fetch.size() == 1);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void episode_id_unique() {
        ItunesSearchAPI query = ItunesSearchAPI.createCollector("term=sommar&entity=podcast&limit=3");

        List<PodCast> podCasts = query.collectPodCasts();

        List<PodCastEpisode> episodes = new ArrayList<>();

        podCasts.forEach(p -> episodes.addAll(p.getPodCastEpisodesInternal()));

        Set<PodCastEpisode> ids = new HashSet<>();
        for (PodCastEpisode episode : episodes) {
            boolean contains = ids.contains(episode);
            if(contains) {
                System.out.println("Duplicate=" + episode.getId());
            }
            Assert.assertFalse(contains);
            ids.add(episode);
        }

        Assert.assertFalse(episodes.isEmpty());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void empty_fetch_if_failing_query() {
        Assert.assertTrue(ItunesSearchAPI.searchPodCasts("http://www.x.*").isEmpty());
        Assert.assertTrue(ItunesSearchAPI.searchPodCasts(".x.*").isEmpty());
        Assert.assertTrue(ItunesSearchAPI.searchPodCasts("             ").isEmpty());
        Assert.assertTrue(ItunesSearchAPI.searchPodCasts("    /         ").isEmpty());
        Assert.assertTrue(ItunesSearchAPI.searchPodCasts(" ??????&ö-., zsasa'/\\-    ").isEmpty());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void fetch_success() {
        ItunesSearchAPI searchAPI = ItunesSearchAPI.createCollector("term=p3&entity=podcast");
        List<PodCast> podCasts = searchAPI.collectPodCasts();
        Assert.assertFalse(podCasts.isEmpty());
        Assert.assertTrue(podCasts.size() >= 42, "Actual=" + podCasts.size());
    }

}