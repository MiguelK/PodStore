package com.podcastcatalog.modelbuilder.collector;

import com.icosillion.podengine.exceptions.InvalidFeedException;
import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Podcast;
import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PodCastFeedParserTest {

    private static final String artworkUrl600 = "http://is4.mzstatic.com/image/thumb/Music62/v4/50/78/30/507830d2-568c-86e2-ecd8-ce61b6444770/source/100x100bb.jpg";

    @Test(groups = TestUtil.SLOW_TEST)
    public void parse_p3_dokumentar() throws MalformedURLException {
        String collectionId = "22233"; //962159150 //22233


        //-1f2c4e48+0c560102

        Set<String> ids= new HashSet<>();

        for(int i=0; i<10; i++) {

            String x = "https://media.acast.com/breakit-daily/glomvantrummen-blidinegenlakare-kommerexplodera/media.mp3";
            PodCast podCast = PodCastFeedParser.parse(new URL("http://api.sr.se/api/rss/pod/3966"), artworkUrl600, collectionId).get();

            String id = podCast.getPodCastEpisodesInternal().get(0).getId();
            System.out.println("ID===== " +id);
            if(ids.contains(id)) {
                Assert.fail("Duplicate " + id);
            }
        }
    }


    @Test(groups = TestUtil.SLOW_TEST)
    public void validURL() throws MalformedURLException, InvalidFeedException, MalformedFeedException {

        String rss = "http://scriptnotes.net/rss";
        rss = "http://audioboom.com/channels/4829841.rss";
        rss = "http://rss.art19.com/the-daily";
        rss = "http://howtofupanairport.libsyn.com/rss,expectedEpisodeCount=-1";
        rss = "http://rss.acast.com/forklart";
        rss = "http://www.ximalaya.com/album/13535262.xml"; //is mandatory
        rss = "http://feeds.adknit.com/app-search/cnn/cnn/all/5/1/";
        rss = "http://www.npr.org/rss/podcast.php?id=510298";
        rss =  "http://api.sr.se/api/rss/pod/itunes/3966";
        //rss = "http://api.audioteca.rac1.cat/rss/no-ho-se";
        //http://www.ximalaya.com/album/3882669.xmldescription is mandatory
        //http://www.netrevo.net/assets/media/itune/promotion.rssInvalid id null mus be > 0
        //http://rss.acast.com/ulost

        //podcast.getEpisodes().get(0).getEnclosure().getURL()
        Optional<PodCast> podCast1 = PodCastFeedParser.tryParseFailOver(new URL(rss), artworkUrl600, "4444", 400);

        Podcast podcast = new Podcast(new URL(rss));

        System.out.println("GGGG "  + podCast1.get() + ", podcast=" + podcast);
        //http://scriptnotes.net/rss
        PodCast podCast = PodCastFeedParser.parse(new URL(rss), artworkUrl600, "22233").get();

        for (PodCastEpisode podCastEpisode : podCast.getPodCastEpisodesInternal()) {
            Assert.assertNotNull(podCastEpisode.getTitle());
        }

        System.out.println("Episodes " + podCast.getPodCastEpisodesInternal().size());
    }


    @Test(groups = TestUtil.SLOW_TEST)
    public void parse_episode_duration() throws MalformedURLException {
        PodCast podCast = PodCastFeedParser.parse(new URL("http://api.sr.se/api/rss/pod/3966"), artworkUrl600, "22233").get();

        for (PodCastEpisode podCastEpisode : podCast.getPodCastEpisodesInternal()) {
            Assert.assertNotNull(podCastEpisode.getDuration().getDisplayValue());
        }

        System.out.println("Episodes " + podCast.getPodCastEpisodesInternal().size());
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