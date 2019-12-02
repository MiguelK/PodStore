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

    public static final int MAX_FEED_COUNT = 400; //ALL?
    private final static Logger LOG = Logger.getLogger(PodCastFeedParser.class.getName());

    public static Optional<PodCast> parse(URL feedURL, String artworkUrl600,
                                                      String collectionId, int maxFeedCount) {

        if(maxFeedCount <= 0) {
            maxFeedCount = MAX_FEED_COUNT;
        }

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

            return Optional.of(podCastBuilder.build());

        } catch (Exception e) {

           if(ServerInfo.isLocalDevMode()) {
               LOG.info("PodCast parse fail: Level 2 feed=" + feedURL + " message=" + e.getMessage());
           }
        }

        return Optional.empty();
    }
}
