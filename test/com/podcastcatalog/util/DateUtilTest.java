package com.podcastcatalog.util;

import com.podcastcatalog.DataProviderTestData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Optional;

public class DateUtilTest {


    @Test(dataProvider = "invalid_dates", dataProviderClass = DataProviderTestData.class)
    public void parse_invaliud_dates(String date) {

        Optional<LocalDateTime> parse = DateUtil.parse(date);

        Assert.assertFalse(parse.isPresent());
    }

    @Test
    public void parse_valid_dates() {
        Assert.assertTrue(DateUtil.parse("Sun, 07 Aug 2016 12:05:26 EST").isPresent());
        Assert.assertTrue(DateUtil.parse("Sun, 07 Aug 2016 12:05:26 PST").isPresent());
        Assert.assertTrue(DateUtil.parse("Sun, 07 Aug 2016 12:05:26").isPresent());
        Assert.assertTrue(DateUtil.parse("2016-09-01T06:09:04.447").isPresent());
    }

}