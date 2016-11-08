package com.podcastcatalog;

import org.testng.annotations.DataProvider;

public class DataProviderTestData {

    @DataProvider(name = "fromString_Level_3_test_data")
    public static Object[][] fromString_Level_3_test_data() {
        return new Object[][] {{"BUDDHISM"}, {" BUDDHISM"}, {" BUDDHISM "},{"buddhism"}};
    }

    @DataProvider(name = "fromString_display_name_test_data")
    public static Object[][] fromString_display_name_test_data() {
        return new Object[][] {{"ARTS","ARTS"},{"",""},{"",""},{"",""},{"",""},{"",""},
                {"",""},{"",""},{"",""},{"",""},{"",""},{"",""},{"",""},{"",""},{"",""},{"",""},
                {"",""},{"",""},{"",""},{"",""},{"",""},{"",""},{"",""},{"",""},{"",""},{"",""}};
    }
    @DataProvider(name = "valid_file_size")
    public static Object[][] valid_file_size() {
        return new Object[][] {{"4444441219","4,1 GB"},{"81349910","77,6 MB"},{"91349910","87,1 MB"},{"4444443344","4,1 GB"},{"5675",null},{"-1",null}};
    }

}
