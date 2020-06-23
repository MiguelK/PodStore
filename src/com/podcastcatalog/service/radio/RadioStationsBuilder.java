package com.podcastcatalog.service.radio;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.radio.RadioStationValidator;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RadioStationsBuilder {

    private final static Logger LOG = Logger.getLogger(RadioStationsBuilder.class.getName());

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

        RadioStationService.INSTANCE.saveStations(radioStations);
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
        List<RadioGuideFMContext> contexts = new ArrayList<>();
        //contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.SE.name().toLowerCase(), "internet-radio-sverige"));
        //contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.FR.name().toLowerCase(), "internet-radio-france"));
        //contexts.add(new RadioGuideFMContext("nl", "internet-radio-nederland"));

       // contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.DE.name().toLowerCase(), "internet-radio-deutschland"));
        contexts.add(new RadioGuideFMContext(PodCastCatalogLanguage.IT.name().toLowerCase(), "internet-radio-italia"));

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
