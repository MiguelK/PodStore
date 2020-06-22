package com.podcastcatalog.model.radio;

/**
 * Created by miguelkrantz on 2020-06-12.
 */
public class RadioStation {
    private String name;
    private String imageURL;
    private String streamURL;
    private String shortDescription;

    // //fr;;NRJ Radio;;https://www.radioguide.fm//uploads/images/radiostation/NRJ-Logo-2014-170-4.png;;NRJ radio, Hit Music Only. radio FM en ligne, NRJ Music Awards, 6-9 ...;;http://cdn.nrjaudio.fm/audio1/fr/30001/mp3_128.mp3?origine=fluxradios;#
    public RadioStation(String name, String imageURL,
                        String shortDescription, String streamURL) {
        this.name = name;
        this.imageURL = imageURL;
        this.streamURL = streamURL;
        this.shortDescription = shortDescription;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getStreamURL() {
        return streamURL;
    }

    public String getShortDescription() {
        return shortDescription;
    }
}
