package com.podcastcatalog.service.radio;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class RadioStationServiceTest {

    @Test
    public void testLoadStations() throws Exception {
        RadioStationService.INSTANCE.loadStations();
    }

}