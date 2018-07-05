package com.podcastcatalog.modelbuilder.collector.itunes;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.collector.PodCastCollector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by miguelkrantz on 2018-07-05.
 */

//PodCastIdCollector
public class ItunesGenreCollector implements PodCastCollector {

    private final static Logger LOG = Logger.getLogger(ItunesGenreCollector.class.getName());


    private static final int TIMEOUT_MILLIS = 5000;


    private final PodCastCatalogLanguage language;
    private final int resultSize;
    private final String url;


    public ItunesGenreCollector(PodCastCatalogLanguage language, int resultSize, String url) {
        this.language = language;
        this.resultSize = resultSize;
        this.url = url;
    }

    @Override
    public List<PodCast> collectPodCasts() {
        List<Long> podCastIds = getPodCastIds();

        if (podCastIds.isEmpty()) {
            return Collections.emptyList();
        }

        if (podCastIds.size() > resultSize) {
            podCastIds = podCastIds.subList(0, resultSize);
        }

        LOG.info("Start searchAPI of " + podCastIds.size() + " podcast(s)");

        return ItunesSearchAPI.lookupPodCasts(podCastIds);

    }

    public List<Long> getPodCastIds() {
        try {
            Document doc = Jsoup.parse(toURL(url), TIMEOUT_MILLIS);

           // Element table = doc.getElementById("itunes_result_table");
           // Elements elements = table.getElementsByAttribute("href");

            Elements elements = doc.getElementsByAttribute("href");


            //  <li><a href="https://itunes.apple.com/se/podcast/sigges-netflixpodd/id1354405336?mt=2">Sigges Netflixpodd</a> </li>

           // System.out.println(elements);

           List<Long> longList = elements.stream().filter(isValidItunesPodCastURL()).mapToLong((e) -> {
                        String toString = e.toString();
                        return parseID(toString);
                    }
            ).boxed().collect(Collectors.toList());

            if (longList.size() > resultSize) {
                longList = longList.subList(0, resultSize);
            }

            return longList;

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to parse id(s) from " + url, e);
            return Collections.emptyList();
        }}

    Long parseID(String value) {

        //="https://itunes.apple.com/se/podcast/ze-shows-anime-pulse/id95185416?mt=2">

        int start = value.indexOf("/id") + 3;
        int end = value.lastIndexOf("?mt");

        String substring = value.substring(start, end); // value.length());

       // System.out.println("value=" + value + " ======= substring=" +substring);

        //StringBuilder result = new StringBuilder();

        /*for (char c : substring.toCharArray()) {
            if (Character.isDigit(c)) {
                result.append(c);
            } else {
                break;
            }
        }*/

        try {
            return Long.parseLong(substring ) ; //result.toString());
        } catch (Exception e) {
            LOG.info(getClass().getSimpleName() + " failed to parse Itunes id=" + value + ", id=" + substring);
            return null;
        }
    }

    private Predicate<Element> isValidItunesPodCastURL() {
        //https://itunes.apple.com/se/genre/podcaster-konst/id1301?mt=2"
        //https://itunes.apple.com/se/podcast/m%C3%A5sskit-med-spoilers/id1390385030?mt=2">Måsskit med spoilers</a> </li>
        //="https://itunes.apple.com/se/podcast/ze-shows-anime-pulse/id95185416?mt=2">
        String itunesPath = "itunes.apple.com/" + language.name().toLowerCase() + "/podcast";
        return e -> {
            String toString = e.toString();
            System.out.println("HREF====== " + toString);
            boolean containsPath = toString.contains(itunesPath);
            return e.getElementsByAttribute("href") != null &&
                    containsPath &&
                    parseID(toString) != null;
        };
    }

    private URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(url, e);
        }
    }
}
