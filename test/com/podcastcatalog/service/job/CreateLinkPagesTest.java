package com.podcastcatalog.service.job;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by miguelkrantz on 2018-06-11.
 */
public class CreateLinkPagesTest {


    @Test
    public void testCreateShortLink() throws Exception {


        CreateLinkPages s = new CreateLinkPages();
        String shortLink = s.createShortLink("trtr", "ytyt", "gggg", "ghhg", "ghghg");

        System.out.println("shortLink=" + shortLink);


    }

}