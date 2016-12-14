package com.podcastcatalog.modelbuilder.podcastfether;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.modelbuilder.collector.PodCastFeedParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class PodCastFeedParserTest {

    private static final String artworkUrl600 = "http://is4.mzstatic.com/image/thumb/Music62/v4/50/78/30/507830d2-568c-86e2-ecd8-ce61b6444770/source/100x100bb.jpg";

    @Test(groups = TestUtil.SLOW_TEST)
    public void parse_p3_dokumentar() throws MalformedURLException {
        String collectionId = "22233";
        PodCast podCast = PodCastFeedParser.parse(new URL("http://api.sr.se/api/rss/pod/3966"), artworkUrl600, collectionId).get();

        Assert.assertNotNull(podCast);

        Assert.assertEquals(podCast.getCollectionId(),collectionId);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void parse_episode_duration() throws MalformedURLException {
        PodCast podCast = PodCastFeedParser.parse(new URL("http://api.sr.se/api/rss/pod/3966"), artworkUrl600, "22233").get();

        for (PodCastEpisode podCastEpisode : podCast.getPodCastEpisodes()) {
            Assert.assertNotNull(podCastEpisode.getDuration().getDisplayValue());
        }
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void parse_invalid_url() throws MalformedURLException {
        Assert.assertFalse(PodCastFeedParser.parse(new URL("http://api.sr.se/api/rssx/pod/396676"), artworkUrl600, "erere").isPresent());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void missingAuthor() throws MalformedURLException {
        String missingAuthor = "http://juliafrej.libsyn.com/rss";

        Optional<PodCast> podCast = PodCastFeedParser.parse(new URL(missingAuthor), artworkUrl600, "333");

        Assert.assertFalse(podCast.isPresent());
    }
}