package com.podcastcatalog.model.podcastcatalog;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.podcastcatalog.DataProviderTestData;
import com.podcastcatalog.TestUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PodCastEpisodeFileSizeTest {


    Gson gson = new Gson();

    @Test
    public void toJSON() {

        PodCastEpisodeFileSize s = PodCastEpisodeFileSize.parse("4444441219");
        TestUtil.assertToJSONNotNull(s);

        JsonElement jsonElement = TestUtil.GSON.toJsonTree(s, PodCastEpisodeFileSize.class);

        Assert.assertNotNull(jsonElement);

        String s1 = gson.toJson(s);
        System.out.println(s1);
        System.out.println(s);


    }

    @Test(dataProvider = "valid_file_size", dataProviderClass = DataProviderTestData.class)
    public void testName(String size,String expected) {
        PodCastEpisodeFileSize s = PodCastEpisodeFileSize.parse(size);

        if(s==null){
            Assert.assertNull(expected);
        } else {
            Assert.assertTrue(Long.valueOf(size) == s.getFileSizeInBytes() );
            Assert.assertEquals(s.getFileSizeFormatted(),expected,"expected=" + expected);
        }
    }

    @Test
    public void invalid_file_size_null() {
        Assert.assertNull(PodCastEpisodeFileSize.parse(""));
        Assert.assertNull(PodCastEpisodeFileSize.parse(" h "));
        Assert.assertNull(PodCastEpisodeFileSize.parse(null));
        Assert.assertNull(PodCastEpisodeFileSize.parse("12:44"));
        Assert.assertNull(PodCastEpisodeFileSize.parse(" 12:4 4 "));
    }
}
