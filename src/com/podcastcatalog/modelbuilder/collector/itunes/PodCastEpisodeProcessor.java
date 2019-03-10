package com.podcastcatalog.modelbuilder.collector.itunes;


import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Episode;
import com.icosillion.podengine.models.Podcast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeDuration;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeType;
import com.podcastcatalog.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Logger;

public class PodCastEpisodeProcessor extends RecursiveTask<PodCastEpisode> {
    private static final long serialVersionUID = 1L;
    private final Podcast podCast;
    private final Episode episode;
    private final String collectionId;

    private final static Logger LOG = Logger.getLogger(PodCastEpisodeProcessor.class.getName());

    //Implement the constructor of the class to initialize its attributes
    public PodCastEpisodeProcessor(Podcast podCast, Episode episode, String collectionId) {
        this.podCast = podCast;
        this.episode = episode;
        this.collectionId = collectionId;
    }

    //Implement the compute() method. As you parameterized the RecursiveTask class with the List<String> type,
    //this method has to return an object of that type.
    @Override
    protected PodCastEpisode compute() {

        //    StopWatch stopWatch = new StopWatch();
        //    stopWatch.start();

        PodCastEpisode.Builder episodeBuilder = PodCastEpisode.newBuilder();
        String description;
        try {
             description = episode.getDescription();
        } catch (MalformedFeedException e) {
            //Ignore e.printStackTrace();
            description = "Missing field";
        }

        if(StringUtils.isEmpty(description)) {
            description = "Missing field";
        }

        //FIX for Radioplay Nemo missing description.
        try {
            episodeBuilder.description(description);

        LocalDateTime pubDate = DateUtil.parse(episode.getPubDate()).orElse(LocalDateTime.now());

        URL targetUrl = null;
        try {
            targetUrl = episode.getLink();
        } catch (MalformedURLException ignored) {
        }

        if (targetUrl== null && episode.getEnclosure() != null) {
            targetUrl =  episode.getEnclosure().getURL();
        }
        if(targetUrl==null){
            return null;
        }

        String targetUrlString = targetUrl.toString();
        String duration = episode.getITunesInfo().getDuration();

        PodCastEpisodeDuration parsedDuration = PodCastEpisodeDuration.parse(duration);
        //FXIEM fileSizeInMegaByte(podCastFeedItem.getFileSizeInMegaByte()) //FIXME no size
        episodeBuilder.title(episode.getTitle()).podCastCollectionId(collectionId).
                createdDate(pubDate).podCastType(PodCastEpisodeType.Audio).
                duration(parsedDuration).
                targetURL(targetUrlString); //.podCastType(podCastFeedItem.getPodCastType()); //FIXME type?
        //if (episodeBuilder.isValid()){ // && podCastFeedItem.getPodCastType() == PodCastEpisodeType.Audio) { //FIXME anly audio?

            // stopWatch.stop();
            //   LOG.info("Episode Time=" + stopWatch.getTime());

            return episodeBuilder.build();

        } catch (Exception e) {
             LOG.info("Failed  parse Episode podCast=" + podCast.getFeedURL() + " ,message="+ e.getMessage());
        }


        return null;
    }
}