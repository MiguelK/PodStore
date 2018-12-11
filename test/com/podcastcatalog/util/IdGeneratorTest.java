package com.podcastcatalog.util;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by miguelkrantz on 2018-12-11.
 */
public class IdGeneratorTest {


    @Test
    public void testGenerate()  {


        String id1 = IdGenerator.generate("S07E03 Dödsskjutningen", "676767");
        String id2 = IdGenerator.generate("S07E03 Dödsskjutningen", "676767");

        System.out.println(id1);
        Assert.assertEquals(id1, id2);
        Assert.assertNotNull(id1);
        Assert.assertNotNull(id2);
        Assert.assertFalse(StringUtils.isEmpty(id1));
        Assert.assertFalse(StringUtils.isEmpty(id2));
    }

}