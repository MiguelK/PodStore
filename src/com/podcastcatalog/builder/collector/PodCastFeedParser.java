package com.podcastcatalog.builder.collector;

import com.podcastcatalog.api.response.*;
import it.sauronsoftware.feed4j.FeedParser;
import it.sauronsoftware.feed4j.bean.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PodCastFeedParser {

    private final URL feedURL;
    private final static Logger LOG = Logger.getLogger(PodCastFeedParser.class.getName());

    public static Optional<PodCast> parse(URL feedURL, String artworkUrl100) {
        PodCastFeedParser podCastFeedParser = new PodCastFeedParser(feedURL);
        return podCastFeedParser.parse(artworkUrl100);
    }

    private PodCastFeedParser(URL feedURL) {
        this.feedURL = feedURL;
    }

    private Optional<PodCast> parse(String artworkUrl100) {
        PodCast.Builder podCastBuilder = PodCast.newBuilder();

        int expectedEpisodeCount = -1;
        try {
            Feed feed = FeedParser.parse(feedURL);
            LOG.info("Feed=" + feed);
            PodCastFeedHeader feedHeader = new PodCastFeedHeader(feed.getHeader());

            podCastBuilder.title(feedHeader.getTitle()).setArtworkUrl100(artworkUrl100).
                    artworkUrlLarge(feedHeader.getArtworkUrlLarge()).
                    description(feedHeader.getDescription()).id(122).
                    setPodCastCategories(feedHeader.getCategories())
                    .createdDate(feedHeader.getCreatedDate())
                    .publisher(feedHeader.getPublisher())
                    .feedURL(feedHeader.getFeedURL());//FIXME id?

            expectedEpisodeCount = feed.getItemCount() > 100 ? 100 : feed.getItemCount(); //FIXME

            for (int i = 0; i < expectedEpisodeCount; i++) {
                FeedItem item = feed.getItem(i);
                PodCastFeedItem podCastFeedItem = new PodCastFeedItem(item);

                //FIXME createdDate
                PodCastEpisode.Builder episodeBuilder = PodCastEpisode.newBuilder();
                episodeBuilder.title(podCastFeedItem.getTitle()).podCastId(9999).artworkUrl100(artworkUrl100). //Same as PodCast ok?
                        createdDate(podCastFeedItem.getCreatedDate()).description(podCastFeedItem.getDescription()).id(3434).
                        duration(podCastFeedItem.getDuration()).fileSizeInMegaByte(podCastFeedItem.getFileSizeInMegaByte()).
                        targetURL(podCastFeedItem.getTargetURL()).podCastType(podCastFeedItem.getPodCastType()); //FIXME type?

                if (episodeBuilder.isValid() && podCastFeedItem.getPodCastType() == PodCastEpisodeType.Audio) { //FIXME
                    podCastBuilder.addPodCastEpisode(episodeBuilder.build());
                }

                if (i % 10 == 0) {
                    LOG.info("Parsing Episode=" + i + " of expectedEpisodeCount=" + expectedEpisodeCount);
                }

            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Faild to parse PodCast from feed=" + feedURL + ",expectedEpisodeCount=" + expectedEpisodeCount, e);
            return Optional.empty();
        }

        if (!podCastBuilder.isValid()) {
            LOG.log(Level.SEVERE, "PodCast from feed=" + feedURL + " is not valid, expectedEpisodeCount=" + expectedEpisodeCount);
            return Optional.empty();
        }

        PodCast podCast = podCastBuilder.build();
        LOG.info("Parsed New PodCast=" + podCast.getTitle());

        return Optional.of(podCast);
    }

    private static class PodCastFeedHeader {
        private final FeedHeader feedHeader;
        private List<RawElement> rawElements = new ArrayList<>();

        PodCastFeedHeader(FeedHeader feedHeader) {
            rawElements = getRawElements(feedHeader);
            this.feedHeader = feedHeader;
        }

        private List<PodCastCategoryType> getCategories() {
            Optional<RawElement> categoryElement = rawElements.stream().filter(r -> "category".equalsIgnoreCase(r.getName())).findFirst();

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

        public LocalDateTime getCreatedDate() {
            Optional<String> first = rawElements.stream().filter(r -> "lastBuildDate".equalsIgnoreCase(r.getName())).map(RawElement::getValue).findFirst();

            if (first.isPresent()) {
                LOG.info("FIXME Implement: CreatedDate from node " + first.get());
                //2016-09-01T06:09:04.447
            }
            return LocalDateTime.now();
        }

        String getPublisher() {
            Optional<String> first = rawElements.stream().filter(r -> "author".equalsIgnoreCase(r.getName())).map(RawElement::getValue).findFirst();
            return first.isPresent() ? first.get() : "Unknown";
        }

        public String getDescription() {
            return feedHeader.getDescription();
        }

        public String getTitle() {
            return feedHeader.getTitle();
        }

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

        public String getFeedURL() {
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

    private static class PodCastFeedItem {

        private final FeedItem feedItem;
        private List<RawElement> rawElements = new ArrayList<>();
        private List<RawAttribute> audioEnclosureAttributes = new ArrayList<>();

        PodCastFeedItem(FeedItem feedItem) {
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
            return toLocalDateTime(feedItem.getPubDate());
        }

        public PodCastEpisodeDuration getDuration() {

            Optional<String> durationOptional = rawElements.stream().filter(r -> "duration".equalsIgnoreCase(r.getName())).map(RawElement::getValue).findFirst();

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
            Optional<RawAttribute> first = audioEnclosureAttributes.stream().filter(a -> "length".equalsIgnoreCase(a.getName())).findFirst();

            if (first.isPresent()) {
                return PodCastEpisodeFileSize.parse(first.get().getValue()); //FIXME if null value?
            }

            return null;
        }

        public String getTargetURL() {

            Optional<RawAttribute> first = audioEnclosureAttributes.stream().filter(a -> "url".equalsIgnoreCase(a.getName())).findFirst();

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
