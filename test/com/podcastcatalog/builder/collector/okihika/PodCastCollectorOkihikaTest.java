package com.podcastcatalog.builder.collector.okihika;

import com.podcastcatalog.api.response.PodCastCategoryType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PodCastCollectorOkihikaTest {
    @Test
    public void testParseID() {


        for (PodCastCollectorOkihika.TopList list : PodCastCollectorOkihika.TopList.values()) {

            if(list== PodCastCollectorOkihika.TopList.All){
                continue;
            }

            PodCastCategoryType a = list.toPodCastCategoryType();

            Assert.assertNotNull(a,list.name());
//            list.to
        }

    }

}