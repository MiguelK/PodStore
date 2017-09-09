package com.podcastcatalog.model.podcastcatalog;


import org.apache.commons.lang3.StringUtils;

public enum PodCastEpisodeType {
    Audio("audio/mpeg"), Video(""),PDF("application/pdf"),Unknown(null);

    private final String mimeType;
    PodCastEpisodeType(String mimeType) {
        this.mimeType = mimeType;
    }

    private String getMimeType() {
        return mimeType;
    }

    public static PodCastEpisodeType fromString(String mimeType) {

        String mime = StringUtils.trimToNull(mimeType);
        if(mime==null){
            return Unknown;
        }

        for (PodCastEpisodeType podCastEpisodeType : values()) {
            if(mimeType.equalsIgnoreCase(podCastEpisodeType.getMimeType())){
                return  podCastEpisodeType;
            }
            if(mime.equalsIgnoreCase("audio/x-m4a")) {
                    return Audio;
            }
        }

        return Unknown;
    }
}
