package com.podcastcatalog.builder.collector.itunes;

import com.google.gson.Gson;
import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.api.response.search.PodCastSearchResponse;
import com.podcastcatalog.builder.collector.PodCastCollector;
import com.podcastcatalog.builder.collector.PodCastFeedParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private static final String BASE_URL = "https://itunes.apple.com/lookup?id=";

    private final URL request;

    public static ItunesSearchAPI lookup(List<Long> ids) {

        String join = StringUtils.join(ids, ",");

        return new ItunesSearchAPI(BASE_URL + join); //FIXME
    }

    //FIXME Refactor searchPodCasts()
    public static ItunesSearchAPI search(String parameters) {
        return new ItunesSearchAPI(BASE_URL_SEARCH + parameters);
    }

    private ItunesSearchAPI(String url) {
        this.request = buildURL(url);
    }

    private URL buildURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e); //Only if schema is missing (https:)
        }
    }

    public List<PodCastSearchResponse> searchPodCast() {
        PodCastSearchResult podCastSearchResult = performSearch();

        if (podCastSearchResult == null) {
            return Collections.emptyList();
        }

        List<PodCastSearchResponse> result = new ArrayList<>();
        for (PodCastSearchResult.Row podCastRow : podCastSearchResult.getResults()) {

            result.add(new PodCastSearchResponse(podCastRow.getCollectionId(), podCastRow.getCollectionName(),
                    podCastRow.getArtworkUrl100()));
        }

        return result;
    }


    @Override
    public List<PodCast> collectPodCasts() {
        PodCastSearchResult podCastSearchResult = performSearch();

        if (podCastSearchResult == null) {
            return Collections.emptyList();
        }

        List<PodCast> podCasts = new ArrayList<>();

        LOG.info("Start parsing " + podCastSearchResult.resultCount + " podcast(s)");

        for (PodCastSearchResult.Row podCastRow : podCastSearchResult.getResults()) {
            URL feedURL = toURL(podCastRow.getFeedUrl());
            if (feedURL != null) {
                Optional<PodCast> podCast = PodCastFeedParser.parse(feedURL, podCastRow.getArtworkUrl600(), podCastRow.getCollectionId());
                if (podCast.isPresent()) {
                    PodCast e = podCast.get();

                    podCasts.add(e);
                }
            }
        }

        LOG.info("Done parsing " + podCasts.size() + " podCast(s), expected=" + podCastSearchResult.resultCount);

        return podCasts;
    }

    private PodCastSearchResult performSearch() {

        HttpsURLConnection connection;
        try {
            connection = (HttpsURLConnection) request.openConnection();
            String content = getResultContent(connection);
            return GSON.fromJson(content, PodCastSearchResult.class);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to get Itunes Search API result using query=" + request, e);
        }

        return null;
    }

    private String getResultContent(HttpsURLConnection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("connection is null");
        }

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));

            String input;

            while ((input = bufferedReader.readLine()) != null) {
                stringBuilder.append(input);
            }

        } catch (IOException e) {
            LOG.warning("Unable to read content from search API " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(bufferedReader);
        }

        return stringBuilder.toString();
    }

    private class PodCastSearchResult {
        int resultCount;
        private final List<Row> results = new ArrayList<>();

        List<Row> getResults() {
            return results;
        }

        private class Row {
            private String kind;
            private String collectionName;
            private String feedUrl;
            private String artworkUrl600;
//            private String artworkUrl100;
            private String collectionId;

            public String getCollectionName() {
                return collectionName;
            }

            public String getCollectionId() {
                return collectionId;
            }

            public String getFeedUrl() {
                return feedUrl;
            }

            public String getArtworkUrl600() {
                return artworkUrl600;
            }

            public String getArtworkUrl100() {
                return artworkUrl600;
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


}
