package com.podcastcatalog.api.response;


import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.logging.Logger;

public class PodCastEpisodeDuration implements Serializable{

    private final static Logger LOG = Logger.getLogger(PodCastEpisodeDuration.class.getName());
    private static final int INVALID_VALUE = -1;

    private final int totalSeconds;
    private final int hours;
    private final int minutes;
    private final int seconds;

    private PodCastEpisodeDuration(String duration) {
        Optional<LocalTime> localTime = toSeconds(duration);

        this.hours = localTime.isPresent() ? localTime.get().getHour() : INVALID_VALUE;
        this.minutes = localTime.isPresent() ? localTime.get().getMinute() : INVALID_VALUE;
        this.seconds = localTime.isPresent() ? localTime.get().getSecond() : INVALID_VALUE;
        this.totalSeconds = localTime.isPresent() ? localTime.get().toSecondOfDay() : INVALID_VALUE;
    }

    public static PodCastEpisodeDuration parse(String duration) {
        PodCastEpisodeDuration podCastEpisodeDuration = new PodCastEpisodeDuration(duration);
        return  podCastEpisodeDuration.isValid() ? podCastEpisodeDuration : null;
    }

    private boolean isValid() {
        return totalSeconds != INVALID_VALUE;
    }

    public int getHour() {
        return hours;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getTotalTimeInSeconds() {
        return totalSeconds;
    }

    private static Optional<LocalTime> toSeconds(final String input) {

        String trimmedInput = StringUtils.trimToEmpty(input);
        if(trimmedInput.chars().filter(c->c==':').count()!=2){
            return Optional.empty();
        }


        String substring = trimmedInput.substring(0, 2);
//        System.out.println(substring);
        if(substring.contains(":")){
            trimmedInput = "0" + trimmedInput;
        }

        LocalTime date;
        try {
            date = LocalTime.parse(trimmedInput, DateTimeFormatter.ISO_LOCAL_TIME);
        }
        catch (DateTimeParseException e) {
            LOG.info("Failed to convert " + input + " to int value. Returning -1");
            return Optional.empty();
        }

        return Optional.ofNullable(date);
    }
}
