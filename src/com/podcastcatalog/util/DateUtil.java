package com.podcastcatalog.util;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DateUtil {

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


    public static Optional<LocalDateTime> parse(String dateTime) {

        String trimmeddateTime = StringUtils.trimToNull(dateTime);
        if (trimmeddateTime ==null) {
            return  Optional.empty();
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

                //Sun, 07 Aug 2016 12:05:26 EST
                List<String> patterns = Arrays.asList("EEE, dd MMM yyyy HH:mm:ss", "EEE, dd MMM yyyy HH:mm:ss EST");
//                String pattern = "dd MMM yyyy HH:mm:ss";
//                LocalDateTime localDate = null;

                for (String pattern : patterns) {
                    try{

                        SimpleDateFormat format =
                                new SimpleDateFormat(pattern);

                        Date parse = format.parse(trimmeddateTime);
                        //System.out.println(parse);

                        if(parse!=null){
                            return Optional.ofNullable(toLocalDateTime(parse));
                        }

//                        System.out.println(trimmeddateTime);
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

//                        ZonedDateTime parse1 = ZonedDateTime.parseSWE(trimmeddateTime);
//                        LocalDateTime localDateTime = parse1.toLocalDateTime();
//                        System.out.println(localDateTime);

//                        System.out.println("10. " + DateFormat.getDateTimeInstance(
//                                DateFormat.LONG, DateFormat.LONG).parseSWE(trimmeddateTime));

//                        DateTime date = formatter.withZone(DateTimeZone.forID("Europe/Paris")).parseDateTime(str);

//                        LocalDateTime parseSWE = LocalDateTime.parseSWE(trimmeddateTime,
//                                formatter);//.withLocale(Locale.ENGLISH));

                        return Optional.empty();//Optional.ofNullable(parseSWE);

                    }catch (Exception x){
                     x.printStackTrace();
                    }
                }

                return Optional.empty();
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

    private static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            date = new Date();
        }

        // java.sql.Date have no time information
        if (date instanceof java.sql.Date) {
            date = new Date(date.getTime());
        }

        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();

        return Date.from(instant);
    }

    public static LocalDate toLocalDate(Date datum) {
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
