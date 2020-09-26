package com.podcastcatalog.modelbuilder.collector.itunes;

import com.google.gson.Gson;
import com.icosillion.podengine.models.Episode;
import com.icosillion.podengine.models.Podcast;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeDuration;
import com.podcastcatalog.model.podcastsearch.PodCastResultItem;
import com.podcastcatalog.modelbuilder.collector.PodCastCollector;
import com.podcastcatalog.modelbuilder.collector.PodCastFeedParser;
import com.podcastcatalog.util.IOUtil;
import com.podcastcatalog.util.IdGenerator;
import com.podcastcatalog.util.PodCastURLParser;
import com.podcastcatalog.util.ServerInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItunesSearchAPI implements PodCastCollector {

    public static class PodCastSmall {
        String podCastTitle;
        String podCastPid;
        String latestPodCastEpisodeId;

        String podCastEpisodeTitle;
        String podCastEpisodeDescription;
        PodCastEpisodeDuration podCastEpisodeDuration;


        public PodCastSmall(String podCastPid, String podCastTitle, String latestPodCastEpisodeId, String podCastEpisodeTitle,
                            String podCastEpisodeDescription,
                            PodCastEpisodeDuration podCastEpisodeDuration) {
            this.podCastPid = podCastPid;
            this.podCastTitle = podCastTitle;
            this.latestPodCastEpisodeId = latestPodCastEpisodeId;
            this.podCastEpisodeTitle = podCastEpisodeTitle;
            this.podCastEpisodeDescription = podCastEpisodeDescription;
            this.podCastEpisodeDuration = podCastEpisodeDuration;
        }

        public String getPodCastPid() {
            return podCastPid;
        }

        public String getPodCastEpisodeTitle() {
            return podCastEpisodeTitle;
        }

        public String getPodCastEpisodeDescription() {
            return podCastEpisodeDescription;
        }

        public PodCastEpisodeDuration getPodCastEpisodeDuration() {
            return podCastEpisodeDuration;
        }

        public String getPodCastTitle() {
            return podCastTitle;
        }

        public String getLatestPodCastEpisodeId() {
            return latestPodCastEpisodeId;
        }

        @Override
        public String toString() {
            return "PodCastSmall{" +
                    "podCastTitle='" + podCastTitle + '\'' +
                    ", podCastPid='" + podCastPid + '\'' +
                    ", latestPodCastEpisodeId='" + latestPodCastEpisodeId + '\'' +
                    ", podCastEpisodeTitle='" + podCastEpisodeTitle + '\'' +
                    ", podCastEpisodeDescription='" + podCastEpisodeDescription + '\'' +
                    ", podCastEpisodeDuration=" + podCastEpisodeDuration +
                    '}';
        }
    }

    private final static Logger LOG = Logger.getLogger(ItunesSearchAPI.class.getName());

    private static final Gson GSON = new Gson();

    private static final String BASE_URL_SEARCH = "https://itunes.apple.com/search?";
    private static final String BASE_URL_LOOKUP = "https://itunes.apple.com/lookup?id=";

    private final URL request;

    private ItunesSearchAPI(String url) {
        this.request = buildURL(url);
    }

    public static ItunesSearchAPI createCollector(String parameters) {
        return new ItunesSearchAPI(BASE_URL_SEARCH + parameters);
    }

    public static Optional<PodCast> lookupPodCast(String id, int maxEpisodes) {

        if(!NumberUtils.isDigits(id)) {
            return  Optional.empty();
        }

        List<PodCast> podCasts = new ItunesSearchAPI(BASE_URL_LOOKUP + id).collectPodCasts(maxEpisodes);
        if (podCasts.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(podCasts.get(0));
    }

    public static Optional<PodCast> lookupPodCast(String id) {

        if(!NumberUtils.isDigits(id)) {
            return  Optional.empty();
        }

        List<PodCast> podCasts = new ItunesSearchAPI(BASE_URL_LOOKUP + id).collectPodCasts();
        if (podCasts.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(podCasts.get(0));
    }

    public static List<PodCast> lookupPodCasts(List<Long> ids) {
        String podCastIds = StringUtils.join(ids, ",");

        if (podCastIds == null) {
            return Collections.emptyList();
        }

        ItunesSearchAPI itunesSearchAPI = new ItunesSearchAPI(BASE_URL_LOOKUP + podCastIds);//FIXME

        return itunesSearchAPI.collectPodCasts();
    }

    public static PodCastSmall getLatestEpisodeIdForPodCast(String pid) {

        String trimmedPid = StringUtils.trimToNull(pid);
        if (trimmedPid == null || !NumberUtils.isDigits(trimmedPid) ) {
            LOG.warning("getLatestEpisodeIdForPodCast() pid is null pid=" + pid);
            return null;
        }


        ItunesSearchAPI itunesSearchAPI = new ItunesSearchAPI(BASE_URL_LOOKUP + trimmedPid);

        PodCastSearchResult podCastSearchResult = itunesSearchAPI.performSearch();
        if (podCastSearchResult == null || podCastSearchResult.getResults().size() != 1) {
            return null;
        }

        PodCastSearchResult.Row row = podCastSearchResult.getResults().get(0);
        URL feedURL;
        if (StringUtils.isEmpty(row.getFeedUrl())) {
            feedURL = PodCastURLParser.parseFeedUrl(row.getCollectionViewUrl());
        } else {
            feedURL = itunesSearchAPI.buildURL(row.getFeedUrl());
        }

        if (feedURL == null) {
            LOG.warning("getLatestEpisodeIdForPodCast() feedURL is null pid=" + pid);
            return null;
        }

        String podCastTitle;
        String latestPodCastEpisodeId;
        String podCastEpisodeTitle = "";
        String podCastEpisodeDescription = "";
        PodCastEpisodeDuration podCastEpisodeDuration;

        try {
            Podcast podcast = new Podcast(feedURL);
            podCastTitle = podcast.getTitle();
            Episode episode = podcast.getEpisodes().get(0);
            podCastEpisodeTitle = episode.getTitle();
            podCastEpisodeDescription = episode.getDescription();
            latestPodCastEpisodeId = IdGenerator.generate(podCastEpisodeTitle, pid);

            String duration = episode.getITunesInfo().getDuration();
            podCastEpisodeDuration = PodCastEpisodeDuration.parse(duration);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "getLatestEpisodeIdForPodCast() pid=" + pid + ", msg=" + e.getMessage());
            return null;
        }

        if(podCastTitle == null) {
            LOG.warning("getLatestEpisodeIdForPodCast() podCastTitle is null pid=" + pid);
            return null;
        }

        return new PodCastSmall(pid, podCastTitle, latestPodCastEpisodeId, podCastEpisodeTitle,
                podCastEpisodeDescription, podCastEpisodeDuration);

    }

    public static List<PodCastResultItem> searchPodCasts(String parameters) {
        return new ItunesSearchAPI(BASE_URL_SEARCH + parameters).searchPodCast();
    }

    public static List<PodCastSearchResult.Row> lookupPodCastsByIds(List<Long> ids) {
        String podCastIds = StringUtils.join(ids, ",");

        if (podCastIds == null) {
            return Collections.emptyList();
        }

        ItunesSearchAPI itunesSearchAPI = new ItunesSearchAPI(BASE_URL_LOOKUP + podCastIds);

        return itunesSearchAPI.lookupPodCastsByIds();
    }

    private List<PodCastSearchResult.Row> lookupPodCastsByIds() {

        PodCastSearchResult podCastSearchResult = performSearch();

        if (podCastSearchResult == null) {
            return Collections.emptyList();
        }

        return podCastSearchResult.getResults();
    }

    @Override
    public List<PodCast> collectPodCasts() {
        return fetchPodCasts(PodCastFeedParser.MAX_FEED_COUNT);
    }

    private List<PodCast> fetchPodCasts(int maxEpisodes) {
        PodCastSearchResult podCastSearchResult = performSearch();

        if (podCastSearchResult == null) {
            return Collections.emptyList();
        }

        List<PodCastProcessor> tasks = new ArrayList<PodCastProcessor>();

        List<PodCast> podCasts = new ArrayList<>();

        //LOG.info("Start parsing " + podCastSearchResult.resultCount + " podcast(s)");

        for (PodCastSearchResult.Row podCastRow : podCastSearchResult.getResults()) {
            String feedUrl = podCastRow.getFeedUrl();
            if (feedUrl == null) {
                if (ServerInfo.isLocalDevMode()) {
                    LOG.info("feedUrl is null " + podCastRow + ", maxEpisodes=" + maxEpisodes);
                }
                continue;
            }
            URL feedURL = toURL(feedUrl);

            if (feedURL != null) {
                PodCastProcessor podCastProcessor = new PodCastProcessor(feedURL, podCastRow.getArtworkUrl600(),
                        podCastRow.getCollectionId(),maxEpisodes);
                podCastProcessor.fork();//Joins current ForkJoinPool :)
                tasks.add(podCastProcessor);
            }
        }

        //LOG.info("Done parsing " + podCasts.size() + " podCast(s), expected=" + podCastSearchResult.resultCount);

        for (PodCastProcessor task : tasks) {
            PodCast join = task.join();
            if (join != null) {
                podCasts.add(join);
            }
        }

        return podCasts;
    }

    @Override
    public List<PodCast> collectPodCasts(int maxEpisodes) {
        return fetchPodCasts(maxEpisodes);
    }

    private PodCastSearchResult performSearch() {

        HttpsURLConnection connection;
        try {
            connection = (HttpsURLConnection) request.openConnection();
            String content = IOUtil.getResultContent(connection);
            return GSON.fromJson(content, PodCastSearchResult.class);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to get Itunes Search API result using query=" + request, e);
        }

        return null;
    }


    public static class PodCastSearchResult {
        int resultCount;
        private final List<Row> results = new ArrayList<>();

        List<Row> getResults() {
            return results;
        }

        public static class Row {
            private String kind;
            private String collectionName;
            private String feedUrl;
            private String artworkUrl600;
            private String artworkUrl100;
            private String collectionId;
            private String collectionViewUrl;

            public String getCollectionViewUrl() {
                return collectionViewUrl;
            }

            public String getCollectionName() {
                return collectionName;
            }

            public String getCollectionId() {
                return collectionId;
            }

            public String getFeedUrl() {
                if (feedUrl == null) {
                    return null;
                }
                if (ServerInfo.isLocalDevMode()) {
                    return feedUrl.replace("https", "http"); //Certifcate problems running locally
                }
                return feedUrl;
            }

            public String getArtworkUrl600() {
                return artworkUrl600;
            }

            public String getArtworkUrl100() {
                return artworkUrl100;
            }

            @Override
            public String toString() {
                return "Row{" +
                        "kind='" + kind + '\'' +
                        ", collectionName='" + collectionName + '\'' +
                        ", feedUrl='" + feedUrl + '\'' +
                        ", artworkUrl600='" + artworkUrl600 + '\'' +
                        '}';
            }
        }
    }

    private URL toURL(String url) {

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            LOG.warning("Unable to create URL " + url + " message=" + e.getMessage());
        }
        return null;
    }

    private URL buildURL(String url) {
        try {
            return new URL(url);
        } catch (Exception e) {
            throw new IllegalArgumentException(e); //Only if schema is missing (https:)
        }
    }

    private List<PodCastResultItem> searchPodCast() {
        PodCastSearchResult podCastSearchResult = performSearch();

        if (podCastSearchResult == null) {
            return Collections.emptyList();
        }

        List<PodCastResultItem> result = new ArrayList<>();
        for (PodCastSearchResult.Row podCastRow : podCastSearchResult.getResults()) {
            result.add(new PodCastResultItem(podCastRow.getCollectionId(), podCastRow.getCollectionName(),
                    podCastRow.getArtworkUrl100()));
        }

        return result;
    }

}
