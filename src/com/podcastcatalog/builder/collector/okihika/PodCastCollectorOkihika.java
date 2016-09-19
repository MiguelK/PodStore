package com.podcastcatalog.builder.collector.okihika;

import com.podcastcatalog.builder.collector.PodCastCollector;
import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.builder.collector.itunes.ItunesSearchAPI;
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

public class PodCastCollectorOkihika implements PodCastCollector {

    private final static Logger LOG = Logger.getLogger(PodCastCollectorOkihika.class.getName());

    private final String url;
    private final int resultSize;

    public enum TopList{
        ALL("0"),
        ARTS("1301"),
        DESIGN("1402"),
        FACHIN_BEAUTY("1459"),
        FOOD("1306"),
        LITERATURE("1401"),
        PERFORMING_ARTS("1405"),
        VISUAL_ART("1406"),
        COMEDY("1303"),
        EDUCATION("1304"),
        EDUCATIONAL_TECHNOLOGY("1468"),
        Higher_Education("1416"),
        K12_SCHOOL("1415"),
        Language_Courses("1469"),
        Training("1470"),
        Kids_Family("1305"),
        Health("1307"),
        Alternative_Health("1481"),
        Fitness_Nutrition("1417"),
        SELF_HELP("1420"),
        SEXUALITY("1421"),
        TV_FILM("1309"),
        MUSIC("1310"),
        NEWS_POLITICS("1311"),
        Religion_Spirituality("1314"),
        Buddhism("1438"),
        Christianity("1439"),
        Hinduism("1463"),
        ISLAM("1440"),
        Judaism("1441"),
        OTHER("1464"),
        Spirituality("1444"),
        Science_Medicine("1315"),
        Medicine("1478"),
        Natural_Sciences("1477"),
        Social_Sciences("1479"),
        Sports_Recreation("1316"),
        Amateur("1467"),
        College_High_School("1466"),
        Outdoor("1456"),
        Professional("1465"),
        Technology("1318"),
        Gadgets("1446"),
        Podcasting("1450"),
        Software_How_To("1480"),
        Tech_News("1448"),
        Business("1321"),
        Business_News("1471"),
        Careers("1410"),
        Investing("1412"),
        Management_Marketing("1413"),
        Shopping("1472"),
        Games_Hobbies("1323"),
        Automotive("1454"),
        AVIATION("1455"),
        Hobbies("1460"),
        OTHER_GAMES("1461"),
        VIDEO_GAMES("1404"),
        SOCIETY_CULTURE("1324"),
        HISTORY("1462"),
        Personal_Journals("1302"),
        Philosophy("1443"),
        Places_Travel("1320"),
        Government_Organizations("1325"),
        LOCAL("1475"),
        NATIONAL("1473"),
        NON_PROFIT("1476"),
        REGIONAL("1474");
        private final String url;

        TopList(String url) {
            this.url = "https://podcast.okihika.com/SE/" +  url;
        }

        public String getUrl() {
            return url;
        }
    }


     PodCastCollectorOkihika(TopList toplist, int resultSize) {
        this.url = toplist.getUrl();
        this.resultSize = resultSize;
    }

    @Override
    public List<PodCast> collect() {
        return getPodCasts();
    }


     List<PodCast> getPodCasts() {
        List<Integer> podCastIds = getPodCastIds();

        if(podCastIds.isEmpty()){
            return Collections.emptyList();
        }

        if(podCastIds.size()>resultSize){
            podCastIds = podCastIds.subList(0, resultSize);
        }

        LOG.info("Start searchAPI of " + podCastIds.size() + " podcast(s)");

        ItunesSearchAPI searchAPI = ItunesSearchAPI.lookup(podCastIds);

        return searchAPI.collect();
    }

    private List<Integer> getPodCastIds() {
        try {

            Document doc = Jsoup.parse(toURL(url), 5000);

            Element table = doc.getElementById("itunes_result_table");
            Elements elements = table.getElementsByAttribute("href");

            List<Integer> a = new ArrayList<>();

            List<Integer> collect = elements.stream().filter(isItunesPodCastURL()).mapToInt((e) -> {
                        String toString = e.toString();
                        int start = toString.indexOf("/id") + 3;
                        String substring = toString.substring(start, toString.length());
                        int end = substring.indexOf("?");
                        String id = substring.substring(0, end);
                        a.add(Integer.parseInt(id));
                        return Integer.parseInt(id);
                    }
            ).boxed().collect(Collectors.toList());


            System.out.println(a);
            System.out.println(collect);
            return collect;

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to parse id from " + url,e);
            return Collections.emptyList();
        }
    }

    private URL toURL(String url){
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(url,e);
        }
    }

    private Predicate<Element> isItunesPodCastURL() {
        return e->e.getElementsByAttribute("href")!=null && e.toString().contains("itunes.apple.com/se/podcast");
    }

    public static PodCastCollectorOkihika parse(TopList toplist, int resultSize) {
        return new PodCastCollectorOkihika(toplist,resultSize);
    }

}
