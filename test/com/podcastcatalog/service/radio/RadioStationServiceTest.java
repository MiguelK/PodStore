package com.podcastcatalog.service.radio;

import org.testng.annotations.Test;

import java.io.FileNotFoundException;

import static org.testng.Assert.*;

public class RadioStationServiceTest {

    @Test
    public void testLoadStations() throws Exception {
        RadioStationService.INSTANCE.loadStations();
    }

    @Test(enabled = false)
    public void cleanFile() throws Exception {
        RadioStationService.INSTANCE.cleanStationsFile();
    }

    @Test(enabled = false)
    public void removeDuplicatedRows() throws FileNotFoundException {
        RadioStationService.INSTANCE.removeDuplicatedRows();
    }

}