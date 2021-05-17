package com.podcastcatalog.model.podcastcatalog;


import com.podcastcatalog.util.DateUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
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

        this.hours = localTime.map(LocalTime::getHour).orElse(INVALID_VALUE);
        this.minutes = localTime.map(LocalTime::getMinute).orElse(INVALID_VALUE);
        this.seconds = localTime.map(LocalTime::getSecond).orElse(INVALID_VALUE);
        this.totalSeconds = localTime.map(LocalTime::toSecondOfDay).orElse(INVALID_VALUE);
    }

    public static PodCastEpisodeDuration parse(String duration) {
        PodCastEpisodeDuration podCastEpisodeDuration = new PodCastEpisodeDuration(duration);
        return  podCastEpisodeDuration.isValid() ? podCastEpisodeDuration : null;
    }

    private boolean isValid() {
        return totalSeconds != INVALID_VALUE;
    }

    int getHour() {
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

    public static Optional<LocalTime> toSeconds(final String input) {
        String trimmedInput = StringUtils.trimToEmpty(input);

        if (trimmedInput.isEmpty()) {
            return Optional.empty();
        }

        String[] split1 = trimmedInput.split(":");
        String filledValues = "";

        //sec
        if(split1.length == 1) {
            String hours = "00";
            String minuteFields = "";
            String secondsFields = "";
            int seconds = NumberUtils.toInt(split1[0]);

            if(seconds >= 60) {
                int minInt = seconds / 60;
                int secondsInt = seconds % 60; // 5 in this case.
                secondsFields = secondsInt < 10 ? "0" + secondsInt : "" + secondsInt;
                minuteFields = minInt < 10 ? "0" + minInt : "" + minInt;
            } else {
                minuteFields = seconds < 10 ? "0" + seconds + ":" : seconds + ":";
            }
            filledValues += hours + ":";
            filledValues += minuteFields + ":";
            filledValues += secondsFields;
        }

        //100:34 //min:sec
        if(split1.length == 2) {
            String hours = "00";
            String minuteFields = "";
            int min = NumberUtils.toInt(split1[0]);

            if(min >= 60) {
                int hoursInt = min / 60;
                int minutes = min % 60;
                hours = hoursInt < 10 ? "0" + hoursInt : "" + hoursInt;
                minuteFields = minutes < 10 ? "0" + minutes + ":" : minutes + ":";
            } else {
                minuteFields = min < 10 ? "0" + min + ":" : min + ":";
            }
            filledValues += hours + ":";
            filledValues += minuteFields;
            filledValues += split1[1].length() <= 1 ? "0" + split1[1] + ":" : split1[1] + ":";
        }

        if(split1.length == 3) {
            filledValues += split1[0].length() <= 1 ? "0" + split1[0] + ":" : split1[0] + ":";
            filledValues += split1[1].length() <= 1 ? "0" + split1[1] + ":" : split1[1] + ":";
            filledValues += split1[2].length() <= 1 ? "59:" : split1[2] + ":";
        }
        filledValues = StringUtils.removeEnd(filledValues, ":");

        LocalTime date;
        try {
            date = LocalTime.parse(filledValues, DateTimeFormatter.ISO_LOCAL_TIME);
        }
        catch (DateTimeParseException e) {
            Optional<LocalDateTime> parse =
                    DateUtil.parse(trimmedInput);

            return parse.flatMap(localDateTime -> Optional.ofNullable(localDateTime.toLocalTime()));
        }

        return Optional.ofNullable(date);
    }

    @Override
    public String toString() {
        return "PodCastEpisodeDuration{" +
                "totalSeconds=" + totalSeconds +
                ", hours=" + hours +
                ", minutes=" + minutes +
                ", seconds=" + seconds +
                '}';
    }

    public String getDisplayValue() {

        int hour = getHour();
        int minutes = getMinutes();

        String displayValue = "";
        if(hour>0){
            displayValue += hour + "h";
        }
        if(minutes>0){
            displayValue += " " + minutes + "m";
        }


        return displayValue.trim();
    }
}
