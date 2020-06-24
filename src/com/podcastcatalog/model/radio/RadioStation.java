package com.podcastcatalog.model.radio;

public class RadioStation {
    private String lang;
    private String name;
    private String imageURL;
    private String streamURL;
    private String shortDescription;

    public RadioStation(String lang,
                        String name, String imageURL,
                        String shortDescription, String streamURL) {
        this.lang = lang;
        this.name = name;
        this.imageURL = imageURL;
        this.streamURL = streamURL;
        this.shortDescription = shortDescription;
    }

    public String getLang() {
        return lang;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RadioStation that = (RadioStation) o;

        if (!name.equals(that.name)) return false;
        if (!imageURL.equals(that.imageURL)) return false;
        return streamURL.equals(that.streamURL);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + imageURL.hashCode();
        result = 31 * result + streamURL.hashCode();
        return result;
    }
}
