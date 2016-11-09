package com.podcastcatalog.api.response;

import org.apache.commons.lang.StringUtils;

public enum PodCastEpisodeType {
    Audio("audio/mpeg"),Video(""),PDF("application/pdf"),Unknown(null);

    private String mimeType;
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
        }

        return Unknown;
    }
}
