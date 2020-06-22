package com.podcastcatalog.service.radio;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by miguelkrantz on 2020-06-22.
 */
public class RadioStationsBuilderTest {
    @Test
    public void testParse() throws Exception {
        RadioStationsBuilder radioStationsBuilder = new RadioStationsBuilder();
        radioStationsBuilder.parse();
    }

}