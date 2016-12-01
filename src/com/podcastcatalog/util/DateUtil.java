package com.podcastcatalog.util;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.*;

public class DateUtil {

    public static String parse(LocalDateTime localDateTime) {
        return null;
    }

    public static class  DateTimeParserException extends Exception{
        DateTimeParserException() {
            super("Unable to parse null date");
        }
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .optionalStart()
            .appendPattern(".SSS")
            .optionalEnd()
            .optionalStart()
            .appendZoneOrOffsetId()
            .optionalEnd()
            .optionalStart()
            .appendOffset("+HHMM", "0000")
            .optionalEnd()
            .toFormatter();


    public static Optional<LocalDateTime> parse(String dateTime)throws DateTimeParserException {

        String trimmeddateTime = StringUtils.trimToNull(dateTime);
        if (trimmeddateTime ==null) {
            throw new DateTimeParserException();
        }

        try {
            return Optional.ofNullable(LocalDateTime.parse(trimmeddateTime));
        } catch (Exception e) {
            for (FormatStyle formatStyle : FormatStyle.values()) {
                try {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(formatStyle);
                    return Optional.ofNullable(LocalDateTime.parse(trimmeddateTime, dateTimeFormatter));
                } catch (Exception e1) {
                    // Ignore
                }}
            try{
                return Optional.ofNullable(LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER));
            }catch (Exception w){
                List<DateTimeFormatter> dateTimeFormatters = Arrays.asList(DateTimeFormatter.BASIC_ISO_DATE, DateTimeFormatter.ISO_DATE,
                        DateTimeFormatter.ISO_DATE_TIME, DateTimeFormatter.ISO_INSTANT, DateTimeFormatter.ISO_LOCAL_DATE,
                        DateTimeFormatter.ISO_LOCAL_TIME, DateTimeFormatter.ISO_OFFSET_DATE, DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME, DateTimeFormatter.ISO_ORDINAL_DATE, DateTimeFormatter.ISO_WEEK_DATE,
                        DateTimeFormatter.ISO_ZONED_DATE_TIME, DateTimeFormatter.ISO_WEEK_DATE,DateTimeFormatter.RFC_1123_DATE_TIME);

                for (DateTimeFormatter formatter : dateTimeFormatters) {
                    try{
                        return Optional.ofNullable(LocalDateTime.parse(trimmeddateTime, formatter));
                    }catch (Exception ww){
                        //Ignore
                    }
                }

                String pattern = "EEE, dd MMM yyyy HH:mm:ss";
                LocalDateTime localDate = LocalDateTime.parse(trimmeddateTime,
                        DateTimeFormatter.ofPattern(pattern).withLocale(Locale.ENGLISH));
                return Optional.ofNullable(localDate);
            }
        }
    }

    public static Optional<LocalDateTime> parse(Date date) {
        if(date==null){
            return Optional.empty();
        }
        return Optional.ofNullable(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    public static Date tillDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();

        return Date.from(instant);
    }

    public static LocalDateTime tillLocalDateTime(Date date) {
        if (date == null) {
            date = new Date();
        }

        // java.sql.Date have no time information
        if (date instanceof java.sql.Date) {
            date = new Date(date.getTime());
        }

        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date tillDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();

        return Date.from(instant);
    }

    public static LocalDate tillLocalDate(Date datum) {
        if (datum == null) {
            datum = new Date();
        }

        // java.sql.Date have no time information
        if (datum instanceof java.sql.Date) {
            datum = new Date(datum.getTime());
        }

        return datum.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
