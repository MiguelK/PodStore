package com.podcastcatalog.modelbuilder.collector.itunes;

import com.google.gson.Gson;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastcatalog.PodCastCategory;
import com.podcastcatalog.model.podcastcatalog.PodCastCategoryType;
import com.podcastcatalog.modelbuilder.collector.PodCastCategoryCollector;
import com.podcastcatalog.modelbuilder.collector.PodCastCollector;
import com.podcastcatalog.util.ServerInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PodCastIdCollector implements PodCastCollector, PodCastCategoryCollector  {

    private final static Logger LOG = Logger.getLogger(PodCastIdCollector.class.getName());

    private static final int TIMEOUT_MILLIS = 5000;

    private static final Gson GSON = new Gson();

    private final PodCastCatalogLanguage language;
    private final int resultSize;
    private final String url;
    private final String categoryTitle;
    private final Category category;
    private static final String DEFAULT_EMPTY_IMAGE = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/Color_icon_red.svg/220px-Color_icon_red.svg.png";


    //https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-arts/id1301
    public enum Category {

        TOPLIST_COUNTRY("https://rss.itunes.apple.com/api/v1/{LANGUAGE}/podcasts/top-podcasts/all/{RESULT_SIZE}/explicit.json"),

        //ARTS
        ARTS("https://podcasts.apple.com/{LANGUAGE}/genre/podcaster-konst/id1301"),
        BOOKS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-arts-books/id1482"),
        DESIGN("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-arts-design/id1402"),
        FASHION_BEAUTY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-arts-fashion-beauty/id1459"),
        FOOD("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-arts-food/id1306"),
        PERFORMING_ARTS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-arts-performing-arts/id1405"),
        VISUAL_ARTS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-arts-visual-arts/id1406"),

        //Business
        BUSINESS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-business/id1321"),
        CAREERS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-business-careers/id1410"),
        ENTREPRENEURSHIP("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-business-entrepreneurship/id1493"),
        INVESTING("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-business-investing/id1412"),
        MANAGEMENT("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-business-management/id1491"),
        MARKETING("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-business-marketing/id1492"),
        NON_PROFIT("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-business-non-profit/id1494"),

        //Comedy
        COMEDY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-comedy/id1303"),
        COMEDY_INTERVIEWS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-comedy-comedy-interviews/id1496"),
        IMPROV("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-comedy-improv/id1495"),
        STAND_UP("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-comedy-stand-up/id1497"),

        //Education
        EDUCATION("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-education/id1304"),
        COURSES("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-education-courses/id1501"),
        HOW_TO("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-education-how-to/id1499"),
        LANGUAGE_LEARNING("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-education-language-learning/id1498"),
        SELF_IMPROVEMENT("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-education-self-improvement/id1500"),

        //Fiction
        FICTION("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-fiction/id1483"),
        COMEDY_FICTION("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-fiction-comedy-fiction/id1486"),
        DRAMA("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-fiction-drama/id1484"),
        SCIENCE_FICTION("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-fiction-science-fiction/id1485"),

        //Government
        GOVERNMENT("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-government/id1511"),

        //Health-Fitness
        HEALTH_FITNESS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-health-fitness/id1512"),
        ALTERNATIVE_HEALTH_FITNESS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-health-fitness-alternative-health/id1513"),
        FITNESS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-health-fitness-fitness/id1514"),
        MEDICINE("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-health-fitness-medicine/id1518"),
        MENTAL_HEALTH_FITNESS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-health-fitness-mental-health/id1517"),
        NUTRITION("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-health-fitness-nutrition/id1515"),
        SEXUALITY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-health-fitness-sexuality/id1516"),

        //History
        HISTORY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-history/id1487"),

        //Kids & Family
        KIDS_FAMILY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-kids-family/id1305"),
        EDUCATION_FOR_KIDS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-kids-family-education-for-kids/id1519"),
        PARENTING("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-kids-family-parenting/id1521"),
        PETS_ANIMALS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-kids-family-pets-animals/id1522"),
        STORIES_FOR_KIDS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-kids-family-stories-for-kids/id1520"),

        //Leisure
        LEISURE("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-leisure/id1502"),
        ANIMATION_MANGA("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-leisure-animation-manga/id1510"),
        AUTOMOTIVE("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-leisure-automotive/id1503"),
        AVIATION("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-leisure-aviation/id1504"),
        CRAFTS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-leisure-crafts/id1506"),
        GAMES("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-leisure-games/id1507"),
        HOBBIES("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-leisure-hobbies/id1505"),
        HOME_GARDEN("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-leisure-home-garden/id1508"),
        VIDEO_GAMES("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-leisure-video-games/id1509"),

        //Music
        MUSIC("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-music/id1310"),
        MUSIC_COMMENTARY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-music-music-commentary/id1523"),
        MUSIC_HISTORY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-music-music-history/id1524"),
        MUSIC_INTERVIEWS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-music-music-interviews/id1525"),

        //News
        NEWS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-news/id1489"),
        BUSINESS_NEWS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-news-business-news/id1490"),
        DAILY_NEWS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-news-daily-news/id1526"),
        ENTERTAINMENT_NEWS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-news-entertainment-news/id1531"),
        NEWS_COMMENTARY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-news-news-commentary/id1530"),
        POLITICS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-news-politics/id1527"),
        SPORTS_NEWS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-news-sports-news/id1529"),
        TECH_NEWS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-news-tech-news/id1528"),

        //Religion & Spirituality
        RELIGION_SPIRITUALITY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-religion-spirituality/id1314"),
        BUDDHISM("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-buddhism/id1438"),
        CHRISTIANITY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-christianity/id1439"),
        HINDUISM("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-hinduism/id1463"),
        ISLAM("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-islam/id1440"),
        JUDAISM("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-judaism/id1441"),
        RELIGION("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-religion/id1532"),
        SPIRITUALITY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-spirituality/id1444"),

        //Science
        SCIENCE("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-science/id1533"),
        ASTRONOMY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-science-astronomy/id1538"),
        CHEMISTRY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-science-chemistry/id1539"),
        EARTH_SCIENCES("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-science-earth-sciences/id1540"),
        LIFE_SCIENCES("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-science-life-sciences/id1541"),
        MATHEMATICS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-science-mathematics/id1536"),
        NATURAL_SCIENCES("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-science-natural-sciences/id1534"),
        NATURE("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-science-nature/id1537"),
        PHYSICS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-science-physics/id1542"),
        SOCIAL_SCIENCES("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-science-social-sciences/id1535"),


        //Society & Culture
        SOCIETY_CULTURE("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-society-culture/id1324"),
        DOCUMENTARY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-society-culture-documentary/id1543"),
        PERSONAL_JOURNALS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-society-culture-personal-journals/id1302"),
        PHILOSOPHY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-society-culture-philosophy/id1443"),
        PLACES_TRAVEL("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-society-culture-places-travel/id1320"),
        RELATIONSHIPS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-society-culture-relationships/id1544"),

        //Sports
        SPORTS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports/id1545"),
        BASEBALL("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-baseball/id1549"),
        BASKETBALL("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-basketball/id1548"),
        CRICKET("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-cricket/id1554"),
        FANTASY_SPORTS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-fantasy-sports/id1560"),
        FOOTBALL("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-football/id1547"),
        GOLF("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-golf/id1553"),
        HOCKEY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-hockey/id1550"),
        RUGBY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-rugby/id1552"),
        RUNNING("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-running/id1551"),
        SOCCER("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-soccer/id1546"),
        SWIMMING("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-swimming/id1558"),
        TENNIS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-tennis/id1556"),
        VOLLEYBALL("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-volleyball/id1557"),
        WILDERNESS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-wilderness/id1559"),
        WRESTLING("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-sports-wrestling/id1555"),

        //TV & Film
        TV_FILM("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-tv-film/id1309"),
        AFTER_SHOWS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-tv-film-after-shows/id1562"),
        FILM_HISTORY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-tv-film-film-history/id1564"),
        FILM_INTERVIEWS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-tv-film-film-interviews/id1565"),
        FILM_REVIEWS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-tv-film-film-reviews/id1563"),
        TV_REVIEWS("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-tv-film-tv-reviews/id1561"),

        //Technology
        TECHNOLOGY("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-technology/id1318"),
        TRUE_CRIME("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-true-crime/id1488");

        private final String categoryUrl;

        Category(String categoryUrl) {
            this.categoryUrl =  categoryUrl;
        }


        public PodCastCategoryType toPodCastCategoryType() {
            return PodCastCategoryType.valueOf(this.name());
        }
    }

    public static PodCastIdCollector createPodCastIdCollector(PodCastCatalogLanguage language, Category category) {
        return new PodCastIdCollector(language, category);
    }

    public PodCastIdCollector(PodCastCatalogLanguage language, Category category, String categoryTitle) {
        this.language = language;
        this.resultSize = 50;
        this.category = category;
        this.categoryTitle = categoryTitle;
        this.url = category.categoryUrl.replace("{LANGUAGE}",
                language.name().toLowerCase()).replace("{RESULT_SIZE}",String.valueOf(resultSize));
    }

    private PodCastIdCollector(PodCastCatalogLanguage language, Category category) {
        this(language, category, "???");
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

        //LOG.info("PodCastIdCollector " + podCastIds.size() + " podCast(s), lang=" + language.name() + ", categoryTitle=" + categoryTitle);

        return ItunesSearchAPI.lookupPodCasts(podCastIds);

    }
    private PodCastToplist parseJSON(String url) {

        HttpsURLConnection connection;
        try {
            URL request = new URL(url);
            connection = (HttpsURLConnection) request.openConnection();
            String content = IOUtils.toString(connection.getInputStream(), "UTF-8");

           // String content = getResultContent(connection);
            return GSON.fromJson(content, PodCastToplist.class);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to get Itunes Search API result using query=" + url, e);
        }

        return null;
    }

    public static class PodCastToplist {

        private Feed feed;
        public static class Feed {
            String title;
            String id;
            private final List<PodCastToplist.Row> results = new ArrayList<>();
        }

        List<PodCastToplist.Row> getResults(){
            if(feed == null){
                return null;
            }
            return feed.results;
        }

        public static class Row {
            private String artistName;
            private String id;
            private String name;
            private String artistId;
            private String artistUrl;
            private String artworkUrl100;
        }
    }

    public List<Long> getPodCastIds() {

        if(category == Category.TOPLIST_COUNTRY) {

            PodCastToplist podCastToplist = parseJSON(url);

            if(podCastToplist != null) {

                List<Long> collect = podCastToplist.getResults().stream().filter(row -> {

                    if(row.id != null) {
                        return true;
                    } else {
                        System.out.println(row.name + " ROW=== " + row.artistName + " " + row.id);
                      return false;
                    }

                })
                        .mapToLong(value -> {
                                    return Long.parseLong(value.id);
                                }

                            ).boxed().collect(Collectors.toList());
                return  collect;
            }
            return Collections.emptyList();
        }


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

        int start = value.lastIndexOf("/id") + 3;
        int end = value.indexOf(">") - 1;

        String substring = value.substring(start, end);

        try {
            return Long.parseLong(substring ) ;
        } catch (Exception e) {
            LOG.info(getClass().getSimpleName() + " failed to parse Itunes id=" + value + ", id=" + substring);
            return null;
        }
    }

    private Predicate<Element> isValidItunesPodCastURL() {
        //https://itunes.apple.com/se/genre/podcaster-konst/id1301?mt=2"
        //https://itunes.apple.com/se/podcast/m%C3%A5sskit-med-spoilers/id1390385030?mt=2">Måsskit med spoilers</a> </li>
        //="https://itunes.apple.com/se/podcast/ze-shows-anime-pulse/id95185416?mt=2">
        //https://podcasts.apple.com/{LANGUAGE}/podcast/were-alive/id313300476
        String itunesPath = "podcasts.apple.com/" + language.name().toLowerCase() + "/podcast";
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

        String artworkUrl600 = podCasts.isEmpty() ? DEFAULT_EMPTY_IMAGE :  podCasts.get(0).getArtworkUrl600();

        return new PodCastCategory(categoryTitle, "???", artworkUrl600, podCasts, category.toPodCastCategoryType());
    }


}
