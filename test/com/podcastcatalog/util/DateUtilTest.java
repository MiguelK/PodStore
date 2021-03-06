package com.podcastcatalog.util;

import com.podcastcatalog.DataProviderTestData;
import com.podcastcatalog.TestUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Optional;

public class DateUtilTest {


    @Test(dataProvider = "invalid_dates", dataProviderClass = DataProviderTestData.class, groups = TestUtil.SLOW_TEST)
    public void parse_invaliud_dates(String date) {

        Optional<LocalDateTime> parse = DateUtil.parse(date);

        Assert.assertFalse(parse.isPresent());
    }

   //FIXME ?? @Test(groups = TestUtil.SLOW_TEST)
    public void parse_valid_dates() {
        Assert.assertTrue(DateUtil.parse("Sun, 07 Aug 2017 12:05:26 EST").isPresent());
        Assert.assertTrue(DateUtil.parse("Sun, 07 Aug 2017 12:05:26 PST").isPresent());
        Assert.assertTrue(DateUtil.parse("Sun, 07 Aug 2017 12:05:26").isPresent());
        Assert.assertTrue(DateUtil.parse("2017-09-01T06:09:04.447").isPresent());
    }

}