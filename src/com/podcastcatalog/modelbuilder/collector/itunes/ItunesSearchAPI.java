package com.podcastcatalog.modelbuilder.collector.itunes;

import com.google.gson.Gson;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastsearch.PodCastResultItem;
import com.podcastcatalog.modelbuilder.collector.PodCastCollector;
import com.podcastcatalog.util.IOUtil;
import com.podcastcatalog.util.ServerInfo;
import org.apache.commons.lang3.StringUtils;

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

    public static Optional<PodCast> lookupPodCast(String id) {

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

        PodCastSearchResult podCastSearchResult = performSearch();

        if (podCastSearchResult == null) {
            return Collections.emptyList();
        }

        List<PodCastProcessor> tasks = new ArrayList<PodCastProcessor>();

        List<PodCast> podCasts = new ArrayList<>();

        //LOG.info("Start parsing " + podCastSearchResult.resultCount + " podcast(s)");

        for (PodCastSearchResult.Row podCastRow : podCastSearchResult.getResults()) {
            String feedUrl = podCastRow.getFeedUrl();
            if(feedUrl == null) {
                LOG.info("feedUrl is null " + podCastRow);
                continue;
            }
            URL feedURL = toURL(feedUrl);

            if (feedURL != null) {
                PodCastProcessor podCastProcessor = new PodCastProcessor(feedURL, podCastRow.getArtworkUrl600(), podCastRow.getCollectionId());
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

            public String getCollectionName() {
                return collectionName;
            }

            public String getCollectionId() {
                return collectionId;
            }

            public String getFeedUrl() {
                if(feedUrl== null){
                    return null;
                }
                if(ServerInfo.isLocalDevMode()){
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
