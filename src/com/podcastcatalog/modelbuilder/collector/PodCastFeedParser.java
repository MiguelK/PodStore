package com.podcastcatalog.modelbuilder.collector;

import com.icosillion.podengine.exceptions.InvalidFeedException;
import com.icosillion.podengine.exceptions.MalformedFeedException;
import com.icosillion.podengine.models.Episode;
import com.icosillion.podengine.models.ITunesItemInfo;
import com.icosillion.podengine.models.Podcast;
import com.podcastcatalog.model.podcastcatalog.*;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastEpisodeProcessor;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastEpisodeProcessor2;
import com.podcastcatalog.util.DateUtil;
import it.sauronsoftware.feed4j.FeedParser;
import it.sauronsoftware.feed4j.FeedXMLParseException;
import it.sauronsoftware.feed4j.bean.*;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

        Optional<PodCast> parse = PodCastFeedParser.parse(feedURL, url.getArtworkUrl600(), url.getCollectionId(), 1000);
        if (parse.isPresent()) {
            return Optional.ofNullable(parse.get().getLatestPodCastEpisode());
        }

        return Optional.empty();
    }

    public static Optional<PodCast> tryParseFailOver(URL feedURL, String artworkUrl600,
                                                      String collectionId, int maxFeedCount) {


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
                categoryTypes = Collections.singletonList(PodCastCategoryType.NEWS_POLITICS);
            }

            podCastBuilder.description(podcast.getDescription()).setPodCastCategories(categoryTypes)
                    .title(podcast.getTitle()).publisher(webMaster).createdDate(createdDate).feedURL(feedURL.toString());

            List<PodCastEpisodeProcessor2> tasks = new ArrayList<>();

            int size = podcast.getEpisodes().size();
            List<Episode> episodesMax = size > maxFeedCount ? podcast.getEpisodes().subList(0, maxFeedCount) : podcast.getEpisodes();

            for (Episode episode : episodesMax) {
                    PodCastEpisodeProcessor2 podCastEpisodeProcessor = new PodCastEpisodeProcessor2(podcast, episode, collectionId);
                    podCastEpisodeProcessor.fork();//FIXME
                    tasks.add(podCastEpisodeProcessor);
                }

            for (PodCastEpisodeProcessor2 task : tasks) {
                PodCastEpisode podCastEpisode = task.join();//FIXME
                if(podCastEpisode!=null){
                    podCastBuilder.addPodCastEpisode(podCastEpisode);
                }
            }


            return Optional.of(podCastBuilder.build());

        } catch (Exception e) {
           //FIXME LOG.info("PodCast parse fail: Level 2 feed=" + feedURL + ", message=" + e.getMessage());
        }

        return Optional.empty();
    }

    private static Optional<PodCast> parse(URL feedURL, String artworkUrl600, String collectionId, int maxFeedCount) {
        PodCast.Builder podCastBuilder = PodCast.newBuilder();

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


               /* PodCastFeedItem podCastFeedItem = new PodCastFeedItem(item);
                String guid = item.getGUID();
                //FIXME createdDate
                PodCastEpisode.Builder episodeBuilder = PodCastEpisode.newBuilder();
                episodeBuilder.title(podCastFeedItem.getTitle()).podCastCollectionId(collectionId).
                        createdDate(podCastFeedItem.getCreatedDate()).description(podCastFeedItem.getDescription()).id(guid).
                        duration(podCastFeedItem.getDuration()).fileSizeInMegaByte(podCastFeedItem.getFileSizeInMegaByte()).
                        targetURL(podCastFeedItem.getTargetURL()).podCastType(podCastFeedItem.getPodCastType()); //FIXME type?

                if (episodeBuilder.isValid() && podCastFeedItem.getPodCastType() == PodCastEpisodeType.Audio) { //FIXME anly audio?
                    podCastBuilder.addPodCastEpisode(episodeBuilder.build());
                }*/

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
        //    LOG.info("PodCast parse fail: Level 1, from feed=" + feedURL + ",expectedEpisodeCount=" + expectedEpisodeCount + " message=" + e.getMessage());
            return tryParseFailOver(feedURL, artworkUrl600, collectionId, maxFeedCount);
        }


        if (!podCastBuilder.isValid()) {
            return tryParseFailOver(feedURL, artworkUrl600, collectionId, maxFeedCount);
        }

        return Optional.of(podCastBuilder.build());
    }

    private static class PodCastFeedHeader {
        private final FeedHeader feedHeader;
        private List<RawElement> rawElements = new ArrayList<>();

        PodCastFeedHeader(FeedHeader feedHeader) {
            rawElements = getRawElements(feedHeader);
            this.feedHeader = feedHeader;
        }

        private List<PodCastCategoryType> getCategories() {
            Optional<RawElement> categoryElement = rawElements.stream().filter(r -> "category".equalsIgnoreCase(r.getName())
                    && StringUtils.trimToNull(r.getValue()) != null).findFirst();

            List<PodCastCategoryType> podCastCategories = new ArrayList<>();
            if (categoryElement.isPresent()) {
                RawElement rawElement = categoryElement.get();
                int attributeCount = rawElement.getAttributeCount();
                if (attributeCount > 1) {
                    LOG.warning("What to do? " + rawElement.getAttributeCount() + " rawElement=" + rawElement);
                }
                if (attributeCount > 0) {
                    RawAttribute attribute = rawElement.getAttribute(0);//FIXME
                    String value = attribute.getValue();
                    podCastCategories = PodCastCategoryType.fromString(value);
                }
            }

            return podCastCategories.isEmpty() ? PodCastCategoryType.AMATEUR.getCategories() : podCastCategories;

        }

        LocalDateTime getCreatedDate() {
            Optional<String> first = rawElements.stream().filter(r -> "lastBuildDate".equalsIgnoreCase(r.getName())
                    && StringUtils.trimToNull(r.getValue()) != null).map(RawElement::getValue).findFirst();

            //2016-09-01T06:09:04.447
          //  first.ifPresent(s -> LOG.info("FIXME Implement: CreatedDate from node " + s));



            return LocalDateTime.now();
        }

        String getPublisher() {
            Optional<String> first = rawElements.stream().filter(r -> "author".equalsIgnoreCase(r.getName())
                    && StringUtils.trimToNull(r.getValue()) != null).map(RawElement::getValue).findFirst();

            return first.isPresent() ? first.get() : null;
        }

        public String getDescription() {
            return feedHeader.getDescription();
        }

        public String getTitle() {
            return feedHeader.getTitle();
        }

        //FIXME remove?
        public String getArtworkUrlLarge() {
            if (feedHeader == null) {
                return null;
            }

            FeedImage image = feedHeader.getImage();
            if (image == null) {
                return null;
            }

            URL url = image.getURL();
            if (url == null) {
                return null;
            }

            return url.toString();
        }

        String getFeedURL() {
            return feedHeader.getURL().toString();
        }

        @Override
        public String toString() {
            return "PodCastFeedHeader{" +
                    "feedURL=" + getFeedURL() +
                    ", publisher=" + getPublisher() +
                    ", createdDate=" + getCreatedDate() +
                    ", title=" + getTitle() +
                    ", description=" + getDescription() +
                    ", categories=" + getCategories() +
                    '}';
        }
    }

    //TEST
    public static class PodCastFeedItem {

        private final FeedItem feedItem;
        private List<RawElement> rawElements = new ArrayList<>();
        private List<RawAttribute> audioEnclosureAttributes = new ArrayList<>();

        public PodCastFeedItem(FeedItem feedItem) {
            this.feedItem = feedItem;
            rawElements = getRawElements(feedItem);
            audioEnclosureAttributes = getAudioEnclosureAttributes(feedItem);
        }

        private List<RawAttribute> getAudioEnclosureAttributes(FeedItem feedItem) {

            int enclosureCount = feedItem.getEnclosureCount();

            for (int i = 0; i < enclosureCount; i++) {
                FeedEnclosure enclosure = feedItem.getEnclosure(i);
                if (PodCastEpisodeType.fromString(enclosure.getMimeType()) == PodCastEpisodeType.Audio) {
                    List<RawAttribute> attributes = new ArrayList<>();
                    for (int j = 0; j < enclosure.getAttributeCount(); j++) {
                        RawAttribute attribute = enclosure.getAttribute(j);
                        attributes.add(attribute);
                    }
                    return attributes;
                }
            }
            return Collections.emptyList();
        }

        public String getTitle() {

            return feedItem.getTitle();
        }

        public LocalDateTime getCreatedDate() {
            Optional<String> publishDate = rawElements.stream().filter(r -> "pubDate".equalsIgnoreCase(r.getName())
                    && r.getValue() != null).map(RawElement::getValue).findFirst();

            if (publishDate.isPresent()) {
                Optional<LocalDateTime> parse = DateUtil.parse(publishDate.get());

                return parse.orElseGet(() -> toLocalDateTime(feedItem.getPubDate()));
            } else {
               // LOG.warning("Will be eror in push...?//FIXME"); //FIXME
            }


            return toLocalDateTime(feedItem.getPubDate());
        }

        public PodCastEpisodeDuration getDuration() {

            Optional<String> durationOptional = rawElements.stream().filter(r -> "duration".equalsIgnoreCase(r.getName())
                    && r.getValue() != null).map(RawElement::getValue).findFirst();

            PodCastEpisodeDuration duration = null;
            if (durationOptional.isPresent()) {
                duration = PodCastEpisodeDuration.parse(durationOptional.get());
            }
            return duration;
        }

        public String getDescription() {
            return feedItem.getDescriptionAsText();//FIXME trim html?
        }

        public PodCastEpisodeFileSize getFileSizeInMegaByte() {
            Optional<RawAttribute> first = audioEnclosureAttributes.stream().filter(a -> "length".equalsIgnoreCase(a.getName()) &&
                    StringUtils.trimToNull(a.getValue()) != null).findFirst();

            //FIXME if null value?
            return first.map(rawAttribute -> PodCastEpisodeFileSize.parse(rawAttribute.getValue())).orElse(null);

        }

        public String getTargetURL() {

            Optional<RawAttribute> first = audioEnclosureAttributes.stream().filter(a -> "url".equalsIgnoreCase(a.getName()) &&
                    StringUtils.trimToNull(a.getValue()) != null).findFirst();

            if (first.isPresent()) {
                return first.get().getValue(); //FIXME if null value?
            }

            return feedItem.getLink().toString(); //FIXME
        }

        public PodCastEpisodeType getPodCastType() {

            PodCastEpisodeType podCastEpisodeType = PodCastEpisodeType.Unknown;
            int enclosureCount = feedItem.getEnclosureCount();
            if (enclosureCount > 0) {
                FeedEnclosure enclosure = feedItem.getEnclosure(0);
                for (int i = 0; i < enclosure.getAttributeCount(); i++) {
                    if ("type".equalsIgnoreCase(enclosure.getAttribute(i).getName())) {
                        podCastEpisodeType = PodCastEpisodeType.fromString(enclosure.getAttribute(i).getValue());
                    }
                }
            }

            return podCastEpisodeType; //FIXME
        }

        @Override
        public String toString() {
            return "PodCastFeedItem{" +
                    " title=" + getTitle() +
                    ", description=" + getDescription() +
                    ", targetURL=" + getTargetURL() +
                    ", creaedDate=" + getCreatedDate() +
                    ", fileSizeInMegaByte=" + getFileSizeInMegaByte() +
                    ", duration=" + getDuration() +
                    ", podCastType=" + getPodCastType() +
                    ", rawElements=" + rawElements +
                    '}';
        }
    }


    private static List<RawElement> getRawElements(RawElement rawElement) {
        List<RawNode> nodes = new ArrayList<>();

        int nodeCount = rawElement.getNodeCount();
        for (int i = 0; i < nodeCount; i++) {
            RawNode node = rawElement.getNode(i);
            nodes.add(node);
        }
        return nodes.stream().filter(n -> n instanceof RawElement).map(r -> (RawElement) r).collect(Collectors.toList());
    }

    private static LocalDateTime toLocalDateTime(Date datum) {
        if (datum == null) {
            datum = new Date();
        }

        return datum.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
