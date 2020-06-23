package com.podcastcatalog.model.radio;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class RadioStationValidator {

    public static boolean isValid(String lang,
                           String name, String imageURL,
                           String shortDescription, String streamURL) {

        if(StringUtils.isEmpty(lang) || StringUtils.isEmpty(name)
                || StringUtils.isEmpty(imageURL) || StringUtils.isEmpty(shortDescription)
                || StringUtils.isEmpty(streamURL)) {
            return false;
        }

        try {
            new URL(imageURL);
            new URL(streamURL);
        } catch (MalformedURLException e) {
            return false;
        }

        return true;
    }
}
