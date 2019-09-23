package com.podcastcatalog.modelbuilder.collector;

import com.icosillion.podengine.models.Episode;
import com.icosillion.podengine.models.Podcast;
import com.podcastcatalog.model.podcastcatalog.*;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastEpisodeProcessor;
import com.podcastcatalog.util.DateUtil;
import com.podcastcatalog.util.ServerInfo;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

/**
 * Parsing a source feed URL, all episodes + PodCasts are parsed
 */
public class PodCastFeedParser {

    private static final int MAX_FEED_COUNT = 400; //ALL?
    private final static Logger LOG = Logger.getLogger(PodCastFeedParser.class.getName());

    public static Optional<PodCast> parse(URL feedURL, String artworkUrl600, String collectionId) {
        return PodCastFeedParser.parse(feedURL, artworkUrl600, collectionId, MAX_FEED_COUNT);
    }

    private static URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            LOG.warning("Unable to create URL " + url + " message=" + e.getMessage());
        }
        return null;
    }

    public static Optional<PodCastEpisode> parseLatestPodCastEpisode(PodCast url) {

        URL feedURL = toURL(url.getFeedURL());

        Optional<PodCast> parse = PodCastFeedParser.parse(feedURL, url.getArtworkUrl600(), url.getCollectionId(), 1000); //FIXME
        if (parse.isPresent()) {
            return Optional.ofNullable(parse.get().getLatestPodCastEpisode());
        }

        return Optional.empty();
    }

    public static Optional<PodCast> tryParseFailOver(URL feedURL, String artworkUrl600,
                                                      String collectionId, int maxFeedCount) {


        // StopWatch stopWatch = new StopWatch();
        //  stopWatch.start();


        PodCast.Builder podCastBuilder = PodCast.newBuilder()
                .collectionId(collectionId).setArtworkUrl600(artworkUrl600);

        try {
            Podcast podcast = new Podcast(feedURL);
            LocalDateTime createdDate = DateUtil.parse(podcast.getPubDate()).orElse(LocalDateTime.now());

            String webMaster = podcast.getManagingEditor();
            if(webMaster==null){
                webMaster = "Unknown";
            }

            List<PodCastCategoryType> categoryTypes = PodCastCategoryType.fromString(podcast.getCategories());
            if(categoryTypes.isEmpty()){
                categoryTypes = Collections.singletonList(PodCastCategoryType.NEWS);
            }

            String description = podcast.getDescription();
            if(StringUtils.isEmpty(description)) {
                description = "Unknown";
            }
            podCastBuilder.description(description).setPodCastCategories(categoryTypes)
                    .title(podcast.getTitle()).publisher(webMaster).createdDate(createdDate).feedURL(feedURL.toString());

            List<PodCastEpisodeProcessor> tasks = new ArrayList<>();

            int size = podcast.getEpisodes().size();
            List<Episode> episodesMax = size > maxFeedCount ? podcast.getEpisodes().subList(0, maxFeedCount) : podcast.getEpisodes();

            for (Episode episode : episodesMax) {
                    PodCastEpisodeProcessor podCastEpisodeProcessor = new PodCastEpisodeProcessor(podcast, episode, collectionId);
                    podCastEpisodeProcessor.fork();//FIXME
                //LOG.info("Fork episode " + episode.getTitle());

                    tasks.add(podCastEpisodeProcessor);
                }

            for (PodCastEpisodeProcessor task : tasks) {
                PodCastEpisode podCastEpisode = task.join();//FIXME
                if(podCastEpisode!=null){
                    podCastBuilder.addPodCastEpisode(podCastEpisode);
                }
            }


            // stopWatch.stop();
           // LOG.info("PodCast Time=" + stopWatch.getTime());
            return Optional.of(podCastBuilder.build());

        } catch (Exception e) {

           if(ServerInfo.isLocalDevMode()) {
               LOG.info("PodCast parse fail: Level 2 feed=" + feedURL + " message=" + e.getMessage());
           }
        }

        return Optional.empty();
    }

    private static Optional<PodCast> parse(URL feedURL, String artworkUrl600, String collectionId, int maxFeedCount) {

        return tryParseFailOver(feedURL, artworkUrl600, collectionId, maxFeedCount);

    /*    PodCast.Builder podCastBuilder = PodCast.newBuilder();

        int expectedEpisodeCount = -1;
        try {
            Feed feed = FeedParser.parse(feedURL);

            PodCastFeedHeader feedHeader = new PodCastFeedHeader(feed.getHeader());

            podCastBuilder.title(feedHeader.getTitle()).setArtworkUrl600(artworkUrl600).
                    description(feedHeader.getDescription()).collectionId(collectionId).
                    setPodCastCategories(feedHeader.getCategories())
                    .createdDate(feedHeader.getCreatedDate())
                    .publisher(feedHeader.getPublisher())
                    .feedURL(feedHeader.getFeedURL());

            boolean max = feed.getItemCount() > maxFeedCount; //1400
            expectedEpisodeCount = max ?  maxFeedCount : feed.getItemCount();
            //expectedEpisodeCount = offset; //feed.getItemCount(); // > maxFeedCount ? maxFeedCount : feed.getItemCount(); //FIXME

            List<PodCastEpisodeProcessor> tasks = new ArrayList<>();
            for (int i = 0; i < expectedEpisodeCount; i++) {
                FeedItem item = feed.getItem(i);
                PodCastEpisodeProcessor podCastEpisodeProcessor = new PodCastEpisodeProcessor(item, collectionId);
                podCastEpisodeProcessor.fork();//FIXME
                tasks.add(podCastEpisodeProcessor);
                if (i % 10 == 0) {
               //     LOG.info("Parsing Episode=" + i + " of expectedEpisodeCount=" + expectedEpisodeCount);
                }
            }

            for (PodCastEpisodeProcessor task : tasks) {
                PodCastEpisode podCastEpisode = task.join();//FIXME
                if(podCastEpisode!=null){
                    podCastBuilder.addPodCastEpisode(podCastEpisode);
                }
            }


        }  catch (Exception e) {
         LOG.info("PodCast parse fail: Level 1, from feed=" + feedURL + ",expectedEpisodeCount=" + expectedEpisodeCount + " message=" + e.getMessage());
           // return tryParseFailOver(feedURL, artworkUrl600, collectionId, maxFeedCount);
        }


        if (!podCastBuilder.isValid()) {
            return tryParseFailOver(feedURL, artworkUrl600, collectionId, maxFeedCount);
        }

        return Optional.of(podCastBuilder.build());*/
    }

}
