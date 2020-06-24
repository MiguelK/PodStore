package com.podcastcatalog.service.radio;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.radio.RadioStationValidator;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RadioStationsBuilder {

    private final static Logger LOG = Logger.getLogger(RadioStationsBuilder.class.getName());
    public static final File RADIO_STATIONS_FILE = new File("/Users/miguelkrantz/Documents/temp/radioStations.txt");
    public static final File RADIO_STATIONS_FILE_VALIDATED = new File("/Users/miguelkrantz/Documents/temp/validated-radioStations.txt");

    private static class RadioGuideFMContext {
        String language;
        String languageDomain;

        public RadioGuideFMContext(String language, String languageDomain) {
            this.language = language;
            this.languageDomain = languageDomain;
        }
    }

    public void parse() {

        List<String> radioStations = new ArrayList<>();
        List<String> radioGuideFM = parsRadioGuideFM();
        radioStations.addAll(radioGuideFM);

        RadioStationService.INSTANCE.saveStations(radioStations, RADIO_STATIONS_FILE);
    }

    private List<String> parsRadioGuideFM() {
        List<RadioGuideFMContext> parserContexts = createParserContexts();

        List<String> radioStations = new ArrayList<>();

        for (RadioGuideFMContext parserContext : parserContexts) {

        String languageDomain = parserContext.languageDomain;
        String baseURL = "https://www.radioguide.fm";
        String url = baseURL + "/" + languageDomain;

        try {
            int TIMEOUT_MILLIS = 5000;
            Document doc = Jsoup.parse(new URL(url), TIMEOUT_MILLIS);

            Elements elements = doc.getElementsByAttribute("href");
            List<Element> collect = elements.stream().filter(element -> {
                return element.attr("href").contains(languageDomain + "/");
            }).collect(Collectors.toList());//.subList(0,8); //FIXME

            for (Element element : collect) {
                String subPageURL = baseURL + element.attr("href");

                Document subPage = Jsoup.parse(new URL(subPageURL), TIMEOUT_MILLIS);

                String name = subPage.getElementsByTag("h1").text();
                String description = subPage.getElementsByClass("radiostation-content").text();
                String imageURL = baseURL + "/" + subPage.getElementsByClass("img-responsive").get(1).attr("src");
                int start = subPage.html().indexOf("var stream =");
                String streamURL = parseURL(subPage.html().substring(start, start + 350));


                LOG.info("Radio Station: " + name + ",streamURL=" + streamURL +
                        ", description=" + description + ",imageURL=" + imageURL);

                if(!RadioStationValidator.isValid(parserContext.language,name,imageURL,description,streamURL)) {
                    continue;
                }


                String row = parserContext.language +
                        ";;" + name + ";;" + imageURL + ";;" + description + ";;" + streamURL + ";#";
                radioStations.add(row);

                Thread.sleep(1000);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to parse id(s) from " + url, e);
        }

        }
        return radioStations;
    }


    private List<RadioGuideFMContext> createParserContexts() {
        //https://www.radioguide.fm"
        List<RadioGuideFMContext> contexts = new ArrayList<>();
     //   contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.SE.name().toLowerCase(), "internet-radio-sverige"));
        //   contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.FR.name().toLowerCase(), "internet-radio-france"));
        //   contexts.add(new RadioGuideFMContext("nl", "internet-radio-nederland"));

        //  contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.DE.name().toLowerCase(), "internet-radio-deutschland"));
        //  contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.IT.name().toLowerCase(), "internet-radio-italia"));

        //US
        // contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-alabama"));
        //contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-arkansas"));

//        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-connecticut"));
        //      contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-georgia"));
        // contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-illinois"));
        // contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-kansas"));
        // contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-maine"));
        // contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-michigan"));
       /*  contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-missouri"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-nevada"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-new-mexico"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-north-dakota"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-oregon"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-south-carolina"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-texas"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-virginia"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-wisconsin"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-alaska"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-california"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-delaware"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-hawaii"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-indiana"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-kentucky"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-maryland"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-minnesota"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-new-hampshire"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-new-york"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-ohio"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-pennsylvania"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-south-dakota"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-utah"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-washington"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-wyoming"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-arizona"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-colorado"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-florida"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-idaho"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-iowa"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-louisiana"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-massachusetts"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-mississippi"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-nebraska"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-new-jersey"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-north-carolina"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-oklahoma"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-rhode-island"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-tennessee"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-usa-vermont"));
         contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.US.name().toLowerCase(), "internet-radio-west-virginia"));*/

        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.GB.name().toLowerCase(), "internet-radio-england"));
        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.CA.name().toLowerCase(), "internet-radio-canada"));
        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.ES.name().toLowerCase(), "internet-radio-espana"));
        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.BR.name().toLowerCase(), "internet-radio-brasil"));
        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.CN.name().toLowerCase(), "internet-radio-hong-kong"));

        contexts.add(new RadioGuideFMContext("iran", "internet-radio-iran"));
        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.IN.name().toLowerCase(), "internet-radio-india"));
        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.RU.name().toLowerCase(), "internet-radio-russia"));
        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.NO.name().toLowerCase(), "internet-radio-norway"));
        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.JP.name().toLowerCase(), "internet-radio-japan"));
        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.TH.name().toLowerCase(), "internet-radio-thailand"));
        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.FI.name().toLowerCase(), "internet-radio-suomi"));
        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.AU.name().toLowerCase(), "internet-radio-australia"));
        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.DK.name().toLowerCase(), "internet-radio-danmark"));
        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.IE.name().toLowerCase(), "internet-radio-ireland"));
        contexts.add(new RadioGuideFMContext("belgie", "internet-radio-belgie"));
        contexts.add(new RadioGuideFMContext("south-korea", "internet-radio-south-korea"));
        contexts.add(new RadioGuideFMContext("indonesia", "internet-radio--indonesia"));
        contexts.add(new RadioGuideFMContext("new-zealand", "internet-radio-zealand"));
        contexts.add(new RadioGuideFMContext("colombia", "internet-radio-colombia"));
        contexts.add(new RadioGuideFMContext("greece", "internet-radio-greece"));
        contexts.add(new RadioGuideFMContext("island", "internet-radio-island"));
        contexts.add(new RadioGuideFMContext("turkiye", "internet-radio-turkiye"));
        contexts.add(new RadioGuideFMContext("south-africa", "internet-radio-south-africa"));
        contexts.add(new RadioGuideFMContext("mexico", "internet-radio-mexico"));
        contexts.add(new RadioGuideFMContext("tunisia", "internet-radio-tunisia"));
        contexts.add(new RadioGuideFMContext("polska", "internet-radio-polska"));
        contexts.add(new RadioGuideFMContext("suisse", "internet-radio-suisse"));
        contexts.add(new RadioGuideFMContext("portugal", "internet-radio-portugal"));
        contexts.add(new RadioGuideFMContext("romania", "internet-radio-romania"));
        contexts.add(new RadioGuideFMContext("uruguay", "internet-radio-uruguay"));
        contexts.add(new RadioGuideFMContext("chile", "internet-radio-chile"));

        //FIXME ADD more

        return contexts;
    }

    private String parseURL(String streamURLRow) {
        if (StringUtils.isEmpty(streamURLRow)) {
            return null;
        }

        String startPattern = "stream' : '";
        int startOffset = streamURLRow.indexOf(startPattern);
        if(startOffset != -1) {
            startOffset = startOffset + startPattern.length();
        }

        int endOffset = streamURLRow.lastIndexOf("'");
        if(startOffset == -1 || endOffset == -1 || streamURLRow.length() <= endOffset) {
            LOG.info(streamURLRow + " FAIL startOffset=" + startOffset + ", endOffset=" +endOffset);

            return null;
        }
        if(startOffset> endOffset) {
            LOG.info(streamURLRow + "FAIL startOffset=" + startOffset + ", endOffset=" +endOffset);

            return null;
        }

        return streamURLRow.substring(startOffset, endOffset);
    }
}
