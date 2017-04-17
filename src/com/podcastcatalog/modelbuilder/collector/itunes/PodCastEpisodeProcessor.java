package com.podcastcatalog.modelbuilder.collector.itunes;


import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeType;
import com.podcastcatalog.modelbuilder.collector.PodCastFeedParser;
import it.sauronsoftware.feed4j.bean.FeedItem;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.RecursiveTask;

public class PodCastEpisodeProcessor extends RecursiveTask<PodCastEpisode> {
    private static final long serialVersionUID = 1L;
    private final FeedItem feedItem;
    private final String collectionId;

    //Implement the constructor of the class to initialize its attributes
    public PodCastEpisodeProcessor(FeedItem feedItem, String collectionId) {
        this.feedItem = feedItem;
        this.collectionId = collectionId;
    }

    //Implement the compute() method. As you parameterized the RecursiveTask class with the List<String> type,
    //this method has to return an object of that type.
    @Override
    protected PodCastEpisode compute() {

        PodCastFeedParser.PodCastFeedItem podCastFeedItem = new PodCastFeedParser.PodCastFeedItem(feedItem);

        String guid = feedItem.getGUID();
        //FIXME createdDate

        PodCastEpisode.Builder episodeBuilder = PodCastEpisode.newBuilder();

        //FIX for Radioplay Nemo missing description.
        if(StringUtils.isEmpty(podCastFeedItem.getDescription())) {
            episodeBuilder.description(podCastFeedItem.getTitle());
        } else {
            episodeBuilder.description(podCastFeedItem.getDescription());
        }

        episodeBuilder.title(podCastFeedItem.getTitle()).podCastCollectionId(collectionId).
                createdDate(podCastFeedItem.getCreatedDate()).id(guid).
                duration(podCastFeedItem.getDuration()).fileSizeInMegaByte(podCastFeedItem.getFileSizeInMegaByte()).
                targetURL(podCastFeedItem.getTargetURL()).podCastType(podCastFeedItem.getPodCastType()); //FIXME type?

        if (episodeBuilder.isValid() && podCastFeedItem.getPodCastType() == PodCastEpisodeType.Audio) { //FIXME anly audio?
            return episodeBuilder.build();
        }

        return null;
    }
}