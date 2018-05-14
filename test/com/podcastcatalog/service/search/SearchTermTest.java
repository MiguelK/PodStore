package com.podcastcatalog.service.search;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by miguelkrantz on 2018-05-11.
 */
public class SearchTermTest {


    @Test
    public void sortList()  {

        SearchTerm s1 = new SearchTerm(12,"Mord");
        SearchTerm s2 = new SearchTerm(1,"Politik");
        SearchTerm s3 = new SearchTerm(88,"Trest ");

        List<SearchTerm> test = new ArrayList<>();
        test.add(s1);
        test.add(s2);
        test.add(s3);

        Assert.assertTrue(test.get(0) == s1);
        Assert.assertTrue(test.get(1) == s2);
        Assert.assertTrue(test.get(2) == s3);

        Collections.sort(test);

        Assert.assertTrue(test.get(0) == s3);
        Assert.assertTrue(test.get(1) == s1);
        Assert.assertTrue(test.get(2) == s2);
    }

}