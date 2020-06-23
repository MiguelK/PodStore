package com.podcastcatalog.service.radio;

import com.podcastcatalog.model.radio.RadioStation;
import com.podcastcatalog.service.subscription.FtpOneClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RadioStationService {

    public static final RadioStationService INSTANCE = new RadioStationService();
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(RadioStationService.class.getName());

    private List<RadioStation> radioStations = new ArrayList<>();

    public void loadStations() {
        LOG.info("Start loading Radio Stations...");
        List<RadioStation> newRadioStations = new ArrayList<>();

        writeLock.lock();

        try {
            try {
                try (InputStream is = new URL(FtpOneClient.RADIO_STATION_FILE).openConnection().getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                     Stream<String> stream = reader.lines()) {
                    for (String line : (Iterable<String>) stream::iterator) {
                        String[] split = line.split(";;");
                        if(split.length == 5) {
                            newRadioStations.add(new RadioStation(split[0], split[1], split[2], split[3], split[4]));
                        }
                    }
                }

                LOG.info("Done loading Radio Stations " + newRadioStations.size());

                radioStations = newRadioStations;

            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Failed loading Radio Stations " + e.getMessage());
            }

        } finally {
            writeLock.unlock();
        }
    }

    public void cleanStationsFile() {
        //FIXME
        //Remove duplicates
        //sort per lang
        //validate URL's not 404 etc
    }

    void saveStations(List<String> radioStations) {
        writeLock.lock();
        try {
            //Save to local , upöoad manually to one.com
            File radioStationsFile = new File("/Users/miguelkrantz/Documents/temp/radioStations.txt");
            Files.write(radioStationsFile.toPath(), radioStations, Charset.forName("UTF-8"));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "saveStations failed " + radioStations.size(), e);
        } finally {
            writeLock.unlock();
        }
    }

    public List<RadioStation> search(String query) {
        readLock.lock();
        try {
            List<RadioStation> result = radioStations.stream().filter(radioStation ->
                    radioStation.getName().toLowerCase().startsWith(query.toLowerCase())).collect(Collectors.toList());
            if(result.size() > 20) {
                return result.subList(0,20);
            }
            return result;
        }finally {
            readLock.unlock();
        }
    }
}
