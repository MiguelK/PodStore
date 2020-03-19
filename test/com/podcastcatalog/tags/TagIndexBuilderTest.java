package com.podcastcatalog.tags;

import com.icosillion.podengine.models.Podcast;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.modelbuilder.collector.PodCastFeedParser;
import com.podcastcatalog.util.PodCastURLParser;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Optional;

import static org.testng.Assert.*;

public class TagIndexBuilderTest {


    @BeforeMethod
    public void setUp() throws Exception {
    }


    @Test
    public void testName() throws Exception {

        System.out.println("4eerer");

        TagIndexBuilder tagIndexBuilder = new TagIndexBuilder();

        String rss = "http://api.sr.se/api/rss/pod/itunes/3966";
        String artworkUrl600 = "http://is4.mzstatic.com/image/thumb/Music62/v4/50/78/30/507830d2-568c-86e2-ecd8-ce61b6444770/source/100x100bb.jpg";
        Optional<PodCast> podCast1 = PodCastFeedParser.parse(new URL(rss), artworkUrl600, "4444", 400);

        //Podcast podcast = new Podcast(new URL(rss));

        System.out.println("podcast=" + podCast1.get().getTitle());
        PodCast podCast = PodCastFeedParser.parse(new URL(rss), artworkUrl600, "22233", 100).get();

        for (PodCastEpisode podCastEpisode : podCast.getPodCastEpisodesInternal()) {
            System.out.println(podCastEpisode.getTitle() + " ID= " + podCastEpisode.getId());
            //  Assert.assertNotNull(podCastEpisode.getTitle());
        }
    }
}