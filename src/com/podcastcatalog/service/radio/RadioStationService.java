package com.podcastcatalog.service.radio;

import com.podcastcatalog.model.radio.RadioStation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
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
        LOG.info("Loading Radio Stations...");
        writeLock.lock();

        try {
            //FIXME
            File radioStationsFile = new File("/Users/miguelkrantz/Documents/temp/radioStations.txt");
            Path path = radioStationsFile.toPath();
            try {
                try (Stream<String> stream = Files.lines(path,Charset.forName("UTF-8"))) {
                    for (String line : (Iterable<String>) stream::iterator) {
                        String[] split = line.split(";;");
                        System.out.println("LINE: " + line + " " + split.length);
                        if(split.length == 5) {
                            radioStations.add(new RadioStation(split[1], split[2], split[3], split[4]));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void cleanStationsFile() {
        //Remove duplicates
        //sort per lang
        //validate URL's not 404 etc
    }

    public void saveStations(List<String> radioStations) {
        writeLock.lock();
        try {
            //FIXME
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

            List<RadioStation> collect = radioStations.stream().filter(radioStation ->
                    radioStation.getName().toLowerCase().startsWith(query.toLowerCase())).collect(Collectors.toList());

            return collect;
        }finally {
            readLock.unlock();
        }


    }
}
