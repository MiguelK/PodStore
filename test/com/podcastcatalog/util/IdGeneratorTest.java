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
    public void chineseCharacters() throws Exception {
        String id1 = IdGenerator.generate("㑁", "676767");
        String id2 = IdGenerator.generate("㑁㑉", "676767");

        System.out.println(id1);
        System.out.println(id2);
        Assert.assertNotEquals(id1, id2);

    }

    @Test
    public void testGenerate()  {

        String id1 = IdGenerator.generate("Del 2/2: Mikael Ljungberg – Från Sydney till självmord", "676767");
        String id2 = IdGenerator.generate("Del 1/2: Mikael Ljungberg – Från Sydney till självmord", "676767");

        System.out.println(id1);
        System.out.println(id2);
        Assert.assertNotEquals(id1, id2);
        Assert.assertNotNull(id1);
        Assert.assertNotNull(id2);
        Assert.assertFalse(StringUtils.isEmpty(id1));
        Assert.assertFalse(StringUtils.isEmpty(id2));
    }

}