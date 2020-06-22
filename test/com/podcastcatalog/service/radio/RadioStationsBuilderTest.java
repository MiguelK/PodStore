package com.podcastcatalog.service.radio;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class RadioStationsBuilderTest {
    @Test
    public void buildRadioStations() throws Exception {
        RadioStationsBuilder radioStationsBuilder = new RadioStationsBuilder();
        radioStationsBuilder.parse();
    }

}