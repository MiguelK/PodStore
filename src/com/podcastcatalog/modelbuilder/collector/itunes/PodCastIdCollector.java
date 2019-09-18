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

/**
 * Created by miguelkrantz on 2018-07-05.
 */
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


    public enum Category {
        TOPLIST_COUNTRY("https://rss.itunes.apple.com/api/v1/{LANGUAGE}/podcasts/top-podcasts/all/{RESULT_SIZE}/explicit.json"),
        ARTS("https://itunes.apple.com/{LANGUAGE}/genre/podcaster-konst/id1301?mt=2"),
        DESIGN("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-arts-design/id1402?mt=2"),
        FASHION_BEAUTY("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-arts-fashion-beauty/id1459?mt=2"),
        FOOD("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-arts-food/id1306?mt=2"),
        LITERATURE("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-arts-literature/id1401?mt=2"),
        PERFORMING_ARTS("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-arts-performing-arts/id1405?mt=2"),
        VISUAL_ARTS("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-arts-visual-arts/id1406?mt=2"),
        COMEDY("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-comedy/id1303?mt=2"),
        EDUCATION("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-education/id1304?mt=2"),
        EDUCATIONAL_TECHNOLOGY("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-education-educational-technology/id1468?mt=2"),
        HIGHER_EDUCATION("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-education-higher-education/id1416?mt=2"),
        K_12("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-education-k-12/id1415?mt=2"),//K_12
        LANGUAGE_COURSES("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-education-language-courses/id1469?mt=2"),
        TRAINING("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-education-training/id1470?mt=2"),
        KIDS_FAMILY("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-kids-family/id1305?mt=2"),
        HEALTH("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-health-fitness/id1512"),
        ALTERNATIVE_HEALTH("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-health-alternative-health/id1481?mt=2"),
        FITNESS_NUTRITION("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-health-fitness-nutrition/id1417?mt=2"),
        SELF_HELP("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-health-self-help/id1420?mt=2"),
        SEXUALITY("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-health-sexuality/id1421?mt=2"),
        TV_FILM("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-tv-film/id1309?mt=2"),
        MUSIC("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-music/id1310?mt=2"),
        NEWS_POLITICS("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-news-politics/id1311?mt=2"),
        RELIGION_SPIRITUALITY("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-religion-spirituality/id1314?mt=2"),
        BUDDHISM("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-religion-spirituality-buddhism/id1438?mt=2"),
        CHRISTIANITY("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-religion-spirituality-christianity/id1439?mt=2"),
        HINDUISM("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-religion-spirituality-hinduism/id1463?mt=2"),
        ISLAM("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-religion-spirituality-islam/id1440?mt=2"),
        JUDAISM("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-religion-spirituality-judaism/id1441?mt=2"),
        OTHER("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-religion-spirituality-other/id1464?mt=2"),
        SPIRITUALITY("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-religion-spirituality-spirituality/id1444?mt=2"),
        SCIENCE_MEDICINE("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-science-medicine/id1315?mt=2"),
        MEDICINE("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-science-medicine-medicine/id1478?mt=2"),
        NATURAL_SCIENCES("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-science-medicine-natural-sciences/id1477?mt=2"),
        SOCIAL_SCIENCES("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-science-medicine-social-sciences/id1479?mt=2"),
        SPORTS_RECREATION("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-sports-recreation/id1316?mt=2"),
        AMATEUR("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-sports-recreation-amateur/id1467?mt=2"),
        COLLEGE_HIGH_SCHOOL("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-sports-recreation-college-high-school/id1466?mt=2"),
        OUTDOOR("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-sports-recreation-outdoor/id1456?mt=2"),
        PROFESSIONAL("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-sports-recreation-professional/id1465?mt=2"),
        TECHNOLOGY("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-technology/id1318?mt=2"),
        GADGETS("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-technology-gadgets/id1446?mt=2"),
        PODCASTING("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-technology-podcasting/id1450?mt=2"),
        SOFTWARE_HOW_TO("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-technology-software-how-to/id1480?mt=2"),
        TECH_NEWS("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-technology-tech-news/id1448?mt=2"),
        BUSINESS("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-business/id1321?mt=2"),
        BUSINESS_NEWS("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-business-business-news/id1471?mt=2"),
        CAREERS("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-business-careers/id1410?mt=2"),
        INVESTING("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-business-investing/id1412?mt=2"),
        MANAGEMENT_MARKETING("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-business-management-marketing/id1413?mt=2"),
        SHOPPING("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-business-shopping/id1472?mt=2"),
        GAMES_HOBBIES("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-games-hobbies/id1323?mt=2"),
        AUTOMOTIVE("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-games-hobbies-automotive/id1454?mt=2"),
        AVIATION("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-games-hobbies-aviation/id1455?mt=2"),
        HOBBIES("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-games-hobbies-hobbies/id1460?mt=2"),
        OTHER_GAMES("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-games-hobbies-other-games/id1461?mt=2"),
        VIDEO_GAMES("https://podcasts.apple.com/{LANGUAGE}/genre/podcasts-games-hobbies-video-games/id1404?mt=2"),
        SOCIETY_CULTURE("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-society-culture/id1324?mt=2"),
        HISTORY("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-society-culture-history/id1462?mt=2"),
        PERSONAL_JOURNALS("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-society-culture-personal-journals/id1302?mt=2"),
        PHILOSOPHY("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-society-culture-philosophy/id1443?mt=2"),
        PLACES_TRAVEL("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-society-culture-places-travel/id1320?mt=2"),
        GOVERNMENT_ORGANIZATIONS("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-government-organizations/id1325?mt=2"),
        LOCAL("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-local/id1475?mt=2"),
        NATIONAL("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-national/id1473?mt=2"),
        NON_PROFIT("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-non-profit/id1476?mt=2"),
        REGIONAL("https://itunes.apple.com/{LANGUAGE}/genre/podcasts-regional/id1474?mt=2");
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
            System.out.println("podCastToplist=" + podCastToplist);

            /*List<Long> longList = elements.stream().filter(isValidItunesPodCastURL()).mapToLong((e) -> {
                        String toString = e.toString();
                        return parseID(toString);
                    }
            ).boxed().collect(Collectors.toList());*/


            if(podCastToplist != null) {

                List<Long> collect = podCastToplist.getResults().stream().filter(row -> {

                    if(row.id != null) {
                   //     System.out.println(row.name +" 1-ROW=== " + row.artistId + " " + row.id);

                        return true;
                    } else {
                        System.out.println(row.name + " ROW=== " + row.artistName + " " + row.id);
                      return false;
                    }

                })
                        .mapToLong(value -> {

                                    return Long.parseLong(value.id);

                                    // NumberUtils.toLong()
                           /* try {
                                Long.parseLong(value.artistId);
                        }catch (Exception e) {
                                e.printStackTrace();
                            }
                                }*/
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
        //https://podcasts.apple.com/us/podcast/were-alive/id313300476
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
