package com.podcastcatalog.api.response;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.Serializable;
import java.text.DecimalFormat;

public class PodCastEpisodeFileSize implements Serializable {

    private static final int INVALID_VALUE = -1;
    private final long fileSizeInBytes;
    private final String fileSizeFormatted;

    private PodCastEpisodeFileSize(String fileSize) {
        this.fileSizeInBytes = parseValue(StringUtils.trimToNull(fileSize));
        this.fileSizeFormatted = format(fileSizeInBytes);
    }

    private static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
         String x= new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];

        return x.replaceAll("\\.",",");
    }

    private String format(long fileSizeInBytes) {
        if(fileSizeInBytes <= 1048576){
            return null;
        }

        return readableFileSize(fileSizeInBytes);
    }

    private long parseValue(String fileSize) {

        if(fileSize==null){
            return INVALID_VALUE;
        }

        return NumberUtils.toLong(fileSize, INVALID_VALUE);
    }

    public static PodCastEpisodeFileSize parse(String fileSize) {
        PodCastEpisodeFileSize podCastEpisodeFileSize = new PodCastEpisodeFileSize(fileSize);
        return  podCastEpisodeFileSize.isValid() ? podCastEpisodeFileSize : null;
    }

    private boolean isValid() {
        return fileSizeInBytes!=INVALID_VALUE;
    }

    public long getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public String getFileSizeFormatted() {
        return fileSizeFormatted;
    }
}
