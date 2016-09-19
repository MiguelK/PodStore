package com.podcastcatalog.api.response;

import com.podcastcatalog.DataProviderTestData;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PodCastEpisodeFileSizeTest {

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
