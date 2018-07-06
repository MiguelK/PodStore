package com.podcastcatalog.modelbuilder.collector.itunes;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastcatalog.PodCastCategory;
import com.podcastcatalog.model.podcastcatalog.PodCastCategoryType;
import com.podcastcatalog.modelbuilder.collector.PodCastCategoryCollector;
import com.podcastcatalog.modelbuilder.collector.PodCastCollector;
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

/**
 * Created by miguelkrantz on 2018-07-05.
 */

//
public class PodCastIdCollector implements PodCastCollector, PodCastCategoryCollector  {

    private final static Logger LOG = Logger.getLogger(PodCastIdCollector.class.getName());

    private static final int TIMEOUT_MILLIS = 5000;

    private final PodCastCatalogLanguage language;
    private final int resultSize;
    private final String url;
    private final String categoryTitle;


    private final Category category;
    public enum Category {
        TOPLIST_COUNTRY("not used"),
        ARTS("https://itunes.apple.com/{LANGUAGE}/genre/podcaster-konst/id1301?mt=2"),
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
        private final String categoryUrl;

        Category(String categoryUrl) {
            this.categoryUrl =  categoryUrl;
        }

        public String getCategoryUrl() {
            return categoryUrl;
        }

        public PodCastCategoryType toPodCastCategoryType() {
            return PodCastCategoryType.valueOf(this.name());
        }
    }

    public PodCastIdCollector(PodCastCatalogLanguage language, Category category, String categoryTitle) {
        this.language = language;
        this.resultSize = 50;
        this.category = category;
        this.categoryTitle = categoryTitle;
        this.url = category.getCategoryUrl().replace("{LANGUAGE}", language.name().toLowerCase());
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

    @Override
    public PodCastCategory collectCategories() {
        List<PodCast> podCasts = collectPodCasts();

        String artworkUrl600 = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/Color_icon_red.svg/220px-Color_icon_red.svg.png";//FIXME Default error image?

        if (!podCasts.isEmpty()) {
            artworkUrl600 = podCasts.get(0).getArtworkUrl600();
        }

        return new PodCastCategory(categoryTitle, "", artworkUrl600, podCasts, category.toPodCastCategoryType());
    }
}
