package com.podcastcatalog.util;

import com.icosillion.podengine.exceptions.DateFormatException;
import com.icosillion.podengine.exceptions.InvalidFeedException;
import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Episode;
import com.icosillion.podengine.models.Podcast;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastCategoryType;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeDuration;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeFileSize;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeType;
import com.podcastcatalog.modelbuilder.collector.PodCastFeedParser;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.podcastcatalog.util.PodCastURLParser.parsePodCastEpisodeId;
import static org.testng.Assert.*;

public class PodCastURLParserTest {
    @Test
    public void testParsePodCastEpisodeId()  {

        PodCastURLParser.Parameters parameters3 = PodCastURLParser.parsePodCastEpisodeId("pid%3D308339623&isi=1209200428&ibi=com.app.Pods");
        PodCastURLParser.Parameters parameters1 = PodCastURLParser.parsePodCastEpisodeId("pid%3D308339623%26eid%3D-1f2c4e48+79d72f26&isi=1209200428&ibi=com.app.Pods");
        PodCastURLParser.Parameters parameters2 = PodCastURLParser.parsePodCastEpisodeId("pid%3D1219271980%26eid%3D+7ce425ca-62ff7def&isi=1209200428&ibi=com.app.Pods");

        Assert.assertEquals(parameters1.getPodCastEpisodeId(), "-1f2c4e48+79d72f26");
        Assert.assertEquals(parameters2.getPodCastEpisodeId(), "+7ce425ca-62ff7def");
        Assert.assertEquals(parameters3.getPodCastId(), "308339623");
        Assert.assertNull(parameters3.getPodCastEpisodeId());


    }

    @Test
    public void parser() throws MalformedURLException {

        String feedUrlString = "https://rss.art19.com/dirty-john";
         feedUrlString = "http://feeds.feedburner.com/DougLovesMovies";
        feedUrlString = "https://rss.art19.com/american-history-tellers";//, collectionId=1313596069
        feedUrlString = "https://www.npr.org/rss/podcast.php?id=510298";//, collectionId=523121474
        feedUrlString = "http://feeds.megaphone.fm/MRM9077223370";//, collectionId=1264843400
        feedUrlString = "http://feeds.megaphone.fm/watergate";//, collectionId=1315040130
        feedUrlString = "https://www.npr.org/rss/podcast.php?id=510313";
        feedUrlString = "https://www.npr.org/rss/podcast.php?id=510253";
        //feedUrlString = "https://www.npr.org/rss/podcast.php?id=510313";//, collectionId=1150510297
        //feedUrlString = "https://rss.art19.com/the-daily-show-with-trevor-noah";//, collectionId=1334878780
        //feedUrlString = "https://www.npr.org/rss/podcast.php?id=344098539";//, collectionId=121493804
        //feedUrlString = "https://www.npr.org/rss/podcast.php?id=510308"; //, collectionId=1028908750

        // URL feedURL = new URL("http://feeds.feedburner.com/DougLovesMovies");
        String artworkUrl600 = "http://www.image.jpg";
        String collectionId = "19999999";

        //URL feedURL = new URL(feedUrlString);

       // PodCastFeedParser.tryParseFailOver(feedURL, artworkUrl600, collectionId);

        PodCast.Builder podCastBuilder = PodCast.newBuilder()
                .collectionId(collectionId).setArtworkUrl600(artworkUrl600);

        URL feedURL = new URL(feedUrlString);

        try {
            Podcast podcast = new Podcast(feedURL);

            LocalDateTime createdDate = DateUtil.parse(podcast.getPubDate()).orElse(LocalDateTime.now());

            String webMaster = podcast.getManagingEditor();
            if(webMaster==null){
                webMaster = "Unknown";
            }
           // System.out.println("webMaster=" + webMaster);
            // System.out.println("getCopyright=" + podcast.getCategories());


            List<PodCastCategoryType> categoryTypes = PodCastCategoryType.fromString(podcast.getCategories());
            if(categoryTypes.isEmpty()){
                categoryTypes = Collections.singletonList(PodCastCategoryType.NEWS_POLITICS);
            }

            podCastBuilder.description(podcast.getDescription()).setPodCastCategories(categoryTypes)
                    .title(podcast.getTitle()).publisher(webMaster).createdDate(createdDate).feedURL(feedURL.toString());

            System.out.println("Episodes==" + podcast.getEpisodes().size());
            System.out.println(podcast.getTitle());
            System.out.println(podcast.getCopyright());
            System.out.println(podcast.getDescription());
            System.out.println(podcast.getFeedURL());
            System.out.println(podcast.getCategories());
            System.out.println(podcast.getITunesInfo().getNewFeedURL());
            System.out.println(podcast.getITunesInfo().getOwner().getName());
            System.out.println(podcast.getITunesInfo().getType());
            System.out.println(podcast.getITunesInfo().getAuthor());
            System.out.println(podcast.getITunesInfo().getExplicit());
            System.out.println(podcast.getITunesInfo().getSubtitle());
            System.out.println(podcast.getITunesInfo().getSummary());
            System.out.println(podcast.getLink());
            System.out.println(podcast.getPubDate());
            System.out.println(podcast.getCloud());
            System.out.println(podcast.getDocsString());
            System.out.println(podcast.getImageURL());
            System.out.println(podcast.getLanguage());
            System.out.println(podcast.getLastBuildDateString());
            System.out.println(podcast.getWebMaster());
         //   System.out.println(podcast.getXMLData());
            System.out.println(podcast.getLastBuildDateString());

            Set<String> uniqueGuids = new HashSet<>();

            for (Episode episode : podcast.getEpisodes()) {

                PodCastEpisode.Builder episodeBuilder = PodCastEpisode.newBuilder();

                //FIX for Radioplay Nemo missing description.
                if(StringUtils.isEmpty(episode.getDescription())) {
                    episodeBuilder.description(podcast.getTitle());
                } else {
                    episodeBuilder.description(episode.getDescription());
                }

                LocalDateTime pubDate = DateUtil.parse(episode.getPubDate()).orElse(LocalDateTime.now());
                String guid = episode.getGUID();

                if(uniqueGuids.contains(guid)) {
                    System.out.println("NOT UNIQUE GUID= " + guid);
                }
                uniqueGuids.add(guid);

                URL targetUrl = episode.getLink();

                if (targetUrl== null) {
                    targetUrl =  episode.getEnclosure().getURL();
                    System.out.println("TEST " + targetUrl);
                }

                String targetUrlString = targetUrl.toString();
                String duration = episode.getITunesInfo().getDuration();

                PodCastEpisodeDuration parsedDuration = PodCastEpisodeDuration.parse(duration);
                //episode.getITunesInfo().
                //PodCastEpisodeFileSize.parse()
                //FXIEM fileSizeInMegaByte(podCastFeedItem.getFileSizeInMegaByte())

                episodeBuilder.title(episode.getTitle()).podCastCollectionId(collectionId).
                        createdDate(pubDate).podCastType(PodCastEpisodeType.Audio).
                        duration(parsedDuration).
                        targetURL(targetUrlString); //.podCastType(podCastFeedItem.getPodCastType()); //FIXME type?

                //if (episodeBuilder.isValid()){ // && podCastFeedItem.getPodCastType() == PodCastEpisodeType.Audio) { //FIXME anly audio?
                    PodCastEpisode podCastEpisode = episodeBuilder.build();
                    podCastBuilder.addPodCastEpisode(podCastEpisode);
                //}



                System.out.println(episode.getTitle());
                System.out.println(episode.getLink());
                System.out.println("*** ItunesInfo ***");
                System.out.println(episode.getITunesInfo().getTitle());
                System.out.println(episode.getITunesInfo().getSummary());
                System.out.println(episode.getITunesInfo().getEpisodeNumber());
                System.out.println(episode.getITunesInfo().getEpisodeType());
                System.out.println(episode.getITunesInfo().getOrder());
                System.out.println(episode.getITunesInfo().getSeasonNumber());
                System.out.println(episode.getITunesInfo().getAuthor());
                System.out.println(episode.getITunesInfo().getExplicit());
                System.out.println(episode.getITunesInfo().getImage());
                System.out.println(episode.getITunesInfo().getSubtitle());
                System.out.println(episode.getITunesInfo().getSeasonNumber());
                System.out.println(episode.getITunesInfo().getDuration());
                System.out.println("****************");
            }


            PodCast podCast = podCastBuilder.build();
            System.out.println("Created PodCast " + podCast);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}