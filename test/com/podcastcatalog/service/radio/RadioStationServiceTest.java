package com.podcastcatalog.service.radio;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by miguelkrantz on 2020-06-22.
 */
public class RadioStationServiceTest {

    @Test
    public void testLoadStations() throws Exception {

        RadioStationService.INSTANCE.loadStations();
    }

}