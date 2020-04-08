package com.podcastcatalog.util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.util.logging.Logger;

public class PodCastURLParser {

    private final static Logger LOG = Logger.getLogger(PodCastURLParser.class.getName());

    public static class Parameters {

        private final String podCastEpisodeId;

        private final String podCastId;

         Parameters(String podCastEpisodeId, String podCastId) {
            this.podCastEpisodeId = podCastEpisodeId;
            this.podCastId = podCastId;
        }

        public String getPodCastEpisodeId() {
            return podCastEpisodeId;
        }

        public String getPodCastId() {
            return podCastId;
        }
    }

    public static URL parseFeedUrl(String url) {
        String trimToNull = StringUtils.trimToNull(url);
        if (trimToNull == null) {
            return null;
        }

        try {
            Document doc = Jsoup.parse(new URL(trimToNull), 3000);

            Element contentString = doc.getElementById("shoebox-ember-data-store");
            if (contentString == null) {
                return null;
            }

            String htmlContent = contentString.toString();
            if (htmlContent == null) {
                return null;
            }
            int feedUrl2 = htmlContent.indexOf("feedUrl");
            if (feedUrl2 <= 0) {
                return null;
            }

            String feedUrlSubstring = htmlContent.substring(feedUrl2);
            int startIndex = feedUrlSubstring.indexOf(":") + 1;
            if (startIndex <= 0) {
                return null;
            }
            int endIndex = feedUrlSubstring.indexOf(",");
            if (endIndex <= 0) {
                return null;
            }

            String feedUrl = feedUrlSubstring.substring(startIndex, endIndex);
            String replace = StringUtils.trimToEmpty(feedUrl).replace("\"", "");
            return new URL(replace);

        } catch (Exception e) {
            LOG.info("parseFeedUrl() " + e.getMessage());
            return null;
        }
    }

    public static Parameters parsePodCastEpisodeId(String queryString)  {
        if(queryString== null || queryString.isEmpty()){
            return null;
        }

        //eid can include + that will be decoded to empty space
        String podCastEpisodeId = null;
        String podCastId = null;

        queryString = queryString.replace("%26","&").replace("%3D","=");

            String[] andSplitted = queryString.split("&"); //&

            if(andSplitted.length>0) { //Always > 2 pis and optional eid plus isi

                for (String s : andSplitted) {
                    String[] keyValue = s.split("=");

                    if(keyValue[0].equals("eid")) {
                        podCastEpisodeId = keyValue[1];
                    }
                    if(keyValue[0].equals("pid")) {
                        podCastId = keyValue[1];
                    }
                }

               /* String[] pidKeyValue= andSplitted[0].split("&");

                if(pidKeyValue.length > 0 && pidKeyValue[0].equals("pid")){
                    podCastId = pidKeyValue[1];
                }

                String[] eidKeyValue = andSplitted[1].split("="); // =

                if(eidKeyValue.length == 2 && eidKeyValue[0].equals("eid")){
                    podCastEpisodeId =  eidKeyValue[1];
                }*/

    }
        return new Parameters(podCastEpisodeId, podCastId);
    }

}
