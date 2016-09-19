package com.podcastcatalog.builder.collector.itunes;

import com.google.gson.Gson;
import com.podcastcatalog.builder.collector.PodCastCollector;
import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.builder.collector.PodCastFeedParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

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

    public static ItunesSearchAPI lookup(List<Integer> ids) {

        String join = StringUtils.join(ids, ",");

        return new ItunesSearchAPI(BASE_URL + join ); //FIXME
    }

    public static ItunesSearchAPI search(String parameters) {
        return new ItunesSearchAPI(BASE_URL_SEARCH + parameters);
    }

    private ItunesSearchAPI(String url) {
        this.request= buildURL(url);
    }

    private URL buildURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw  new IllegalArgumentException(e); //Only if schema is missing (https:)
        }
    }

    @Override
    public List<PodCast> collect() {
        PodCastSearchResult podCastSearchResult = performSearch();

        if(podCastSearchResult ==null){
            return Collections.emptyList();
        }

        List<PodCast> podCasts = new ArrayList<>();

        LOG.info("Start parsing " + podCastSearchResult.resultCount + " podcast(s)");

        for (PodCastSearchResult.Row podCastRow : podCastSearchResult.results) {
            URL feedURL = toURL(podCastRow.feedUrl);
            if(feedURL!=null){
                Optional<PodCast> podCast = PodCastFeedParser.parse(feedURL);
                if(podCast.isPresent()){
                    podCasts.add(podCast.get());
                }
            }
        }

        LOG.info("Done parsing " + podCasts.size() + " podCast(s), expected=" + podCastSearchResult.resultCount);

        return podCasts;
    }

    private PodCastSearchResult performSearch(){

        HttpsURLConnection con;
        try {
            con = (HttpsURLConnection)request.openConnection();
            String content = getResultString(con);
            return GSON.fromJson(content, PodCastSearchResult.class);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to get Itunes Search API result using query=" + request, e);
        }

        return null;
    }

    private String getResultString(HttpsURLConnection con){

        StringBuilder stringBuilder = new StringBuilder();
        if(con!=null){
            BufferedReader bufferedReader=null;
            try {
                 bufferedReader =
                        new BufferedReader(
                                new InputStreamReader(con.getInputStream()));

                String input;

                while ((input = bufferedReader.readLine()) != null){
                    System.out.println(input);
                    stringBuilder.append(input);
                }
                bufferedReader.close();

            } catch (IOException e) {
                LOG.warning("Unable to read content from search API " + e.getMessage());
                IOUtils.closeQuietly(bufferedReader);
            }
        }

        return stringBuilder.toString();
    }

    private class PodCastSearchResult {
        int resultCount;
        List<Row> results = new ArrayList<>();

        private class Row{
            String kind;
            String collectionName;
            String feedUrl;
            String artworkUrl30;
            String artworkUrl100;

            @Override
            public String toString() {
                return "Row{" +
                        "kind='" + kind + '\'' +
                        ", collectionName='" + collectionName + '\'' +
                        ", feedUrl='" + feedUrl + '\'' +
                        ", artworkUrl30='" + artworkUrl30 + '\'' +
                        ", artworkUrl100='" + artworkUrl100 + '\'' +
                        '}';
            }
        }
    }

    private URL toURL(String url){

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            LOG.warning("Unable to create URL " + url + " message=" + e.getMessage());
        }
        return null;
    }


}
