package com.podcastcatalog.builder.podcastfether;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.builder.collector.PodCastFeedParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

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
    public void parse_invalid_url() throws MalformedURLException {
        Assert.assertFalse(PodCastFeedParser.parse(new URL("http://api.sr.se/api/rssx/pod/396676"), artworkUrl600, "erere").isPresent());
    }

}