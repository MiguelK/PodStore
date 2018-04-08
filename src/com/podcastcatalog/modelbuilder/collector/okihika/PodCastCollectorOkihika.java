package com.podcastcatalog.modelbuilder.collector.okihika;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastcatalog.PodCastCategoryType;
import com.podcastcatalog.modelbuilder.collector.PodCastCollector;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PodCastCollectorOkihika implements PodCastCollector {

    private final static Logger LOG = Logger.getLogger(PodCastCollectorOkihika.class.getName());
    private static final int TIMEOUT_MILLIS = 5000;

    /*public enum Language{
        SE("SE"),//Swedish
        CN("CN"),//China
        ES("ES"), //Spain
        US("US");//US-English

        private final String rootUrl;

        Language(String lang) {
            this.rootUrl = "http://podcast.okihika.com/" + lang + "/";
        }

        public String getRootUrl() {
            return rootUrl;
        }
    }
*/

    private final String url;
    private final int resultSize;
    private final PodCastCatalogLanguage language;

    public enum TopList {
        TOPLIST_COUNTRY("0"),
        ARTS("1301"),
        DESIGN("1402"),
        FASHION_BEAUTY("1459"),
        FOOD("1306"),
        LITERATURE("1401"),
        PERFORMING_ARTS("1405"),
        VISUAL_ARTS("1406"),
        COMEDY("1303"),
        EDUCATION("1304"),
        EDUCATIONAL_TECHNOLOGY("1468"),
        HIGHER_EDUCATION("1416"),
        K_12("1415"),//K_12
        LANGUAGE_COURSES("1469"),
        TRAINING("1470"),
        KIDS_FAMILY("1305"),
        HEALTH("1307"),
        ALTERNATIVE_HEALTH("1481"),
        FITNESS_NUTRITION("1417"),
        SELF_HELP("1420"),
        SEXUALITY("1421"),
        TV_FILM("1309"),
        MUSIC("1310"),
        NEWS_POLITICS("1311"),
        RELIGION_SPIRITUALITY("1314"),
        BUDDHISM("1438"),
        CHRISTIANITY("1439"),
        HINDUISM("1463"),
        ISLAM("1440"),
        JUDAISM("1441"),
        OTHER("1464"),
        SPIRITUALITY("1444"),
        SCIENCE_MEDICINE("1315"),
        MEDICINE("1478"),
        NATURAL_SCIENCES("1477"),
        SOCIAL_SCIENCES("1479"),
        SPORTS_RECREATION("1316"),
        AMATEUR("1467"),
        COLLEGE_HIGH_SCHOOL("1466"),
        OUTDOOR("1456"),
        PROFESSIONAL("1465"),
        TECHNOLOGY("1318"),
        GADGETS("1446"),
        PODCASTING("1450"),
        SOFTWARE_HOW_TO("1480"),
        TECH_NEWS("1448"),
        BUSINESS("1321"),
        BUSINESS_NEWS("1471"),
        CAREERS("1410"),
        INVESTING("1412"),
        MANAGEMENT_MARKETING("1413"),
        SHOPPING("1472"),
        GAMES_HOBBIES("1323"),
        AUTOMOTIVE("1454"),
        AVIATION("1455"),
        HOBBIES("1460"),
        OTHER_GAMES("1461"),
        VIDEO_GAMES("1404"),
        SOCIETY_CULTURE("1324"),
        HISTORY("1462"),
        PERSONAL_JOURNALS("1302"),
        PHILOSOPHY("1443"),
        PLACES_TRAVEL("1320"),
        GOVERNMENT_ORGANIZATIONS("1325"),
        LOCAL("1475"),
        NATIONAL("1473"),
        NON_PROFIT("1476"),
        REGIONAL("1474");
        private final String categoryId;

        TopList(String categoryId) {
            this.categoryId =  categoryId;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public PodCastCategoryType toPodCastCategoryType() {
            return PodCastCategoryType.valueOf(this.name());
        }
    }


    public PodCastCollectorOkihika(PodCastCatalogLanguage language, TopList toplist, int resultSize) {
        this.url = "http://podcast.okihika.com/" + language.name() + "/" +  toplist.getCategoryId();
        this.resultSize = resultSize;
        this.language = language;
    }

    @Override
    public List<PodCast> collectPodCasts() {
        return getPodCasts();
    }


    List<PodCast> getPodCasts() {
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

            Element table = doc.getElementById("itunes_result_table");
            Elements elements = table.getElementsByAttribute("href");

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
        }
    }

    Long parseID(String value) {
        int start = value.indexOf("/id") + 3;
        String substring = value.substring(start, value.length());

        StringBuilder result = new StringBuilder();

        for (char c : substring.toCharArray()) {
            if (Character.isDigit(c)) {
                result.append(c);
            } else {
                break;
            }
        }

        try {
            return Long.parseLong(result.toString());
        } catch (Exception e) {
            LOG.info(getClass().getSimpleName() + " failed to parse Itunes id=" + value + ", id=" + result.toString());
            return null;
        }
    }

    private URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(url, e);
        }
    }

    private Predicate<Element> isValidItunesPodCastURL() {
        String itunesPath = "itunes.apple.com/" + language.name().toLowerCase() + "/podcast";
        return e -> {
            String toString = e.toString();
            boolean contains = toString.contains(itunesPath);
            return e.getElementsByAttribute("href") != null &&
                    contains &&
                    parseID(toString) != null;
        };
    }


    public static PodCastCollectorOkihika parseSWE(TopList toplist, int resultSize) {
        return new PodCastCollectorOkihika(PodCastCatalogLanguage.SE,toplist, resultSize);
    }
    public static PodCastCollectorOkihika parse(PodCastCatalogLanguage lang, TopList toplist, int resultSize) {
        return new PodCastCollectorOkihika(lang, toplist, resultSize);
    }
}
