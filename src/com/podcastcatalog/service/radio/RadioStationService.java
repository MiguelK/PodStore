package com.podcastcatalog.service.radio;

import com.podcastcatalog.model.radio.RadioStation;
import com.podcastcatalog.service.subscription.FtpOneClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.podcastcatalog.service.radio.RadioStationsBuilder.RADIO_STATIONS_FILE_VALIDATED;

public class RadioStationService {

    public static final RadioStationService INSTANCE = new RadioStationService();
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(RadioStationService.class.getName());

    private List<RadioStation> radioStations = new ArrayList<>();

    public void loadStations() {
        LOG.info("Start loading Radio Stations...");

        writeLock.lock();
        try {
            InputStream inputStream = new URL(FtpOneClient.RADIO_STATION_FILE).openConnection().getInputStream();
            radioStations = parseFile(inputStream);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed loadStations from=" + FtpOneClient.RADIO_STATION_FILE + ", message=" + e.getMessage());
        } finally {
            writeLock.unlock();
        }
    }

    private  List<RadioStation> parseFile(InputStream inputStream) {
        List<RadioStation> newRadioStations = new ArrayList<>();
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 Stream<String> stream = reader.lines()) {
                for (String line : (Iterable<String>) stream::iterator) {
                    String[] split = line.replace(";#", "").split(";;");
                    if(split.length == 5) {
                        newRadioStations.add(new RadioStation(split[0], split[1], split[2], split[3], split[4]));
                    }
                }
            }

            LOG.info("Done loading Radio Stations " + newRadioStations.size());

        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed loading Radio Stations " + e.getMessage());
        }

        return newRadioStations;
    }

    public void removeDuplicatedRows() throws FileNotFoundException {
        List<RadioStation> radioStations = parseFile(new FileInputStream(RadioStationsBuilder.RADIO_STATIONS_FILE));
        Set<RadioStation> unique = new HashSet<>();
        unique.addAll(radioStations);

        List<String> validRadioStations = new ArrayList<>();

        for (RadioStation radioStation : unique) {
            validRadioStations.add(createRow(radioStation));
        }

        int removed = radioStations.size() - unique.size();
        LOG.info("Removed " + removed + " Duplicated Rows ");

        if(removed > 0) {
            saveStations(validRadioStations, RADIO_STATIONS_FILE_VALIDATED);
        }



    }
    public void cleanStationsFile() throws FileNotFoundException {
        List<RadioStation> radioStations = parseFile(new FileInputStream(RadioStationsBuilder.RADIO_STATIONS_FILE));

        List<String> validRadioStations = new ArrayList<>();

        for (RadioStation radioStation : radioStations) {
            try {
                LOG.info("Validating: " + radioStation.getName());

                URL url = new URL(radioStation.getStreamURL());
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setConnectTimeout(3000);
                huc.setReadTimeout(3000);

                int responseCode = huc.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK ||
                        responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                        responseCode == HttpURLConnection.HTTP_MOVED_PERM) {

                    //FIXME Duplicated code
                    String row = createRow(radioStation);
                    validRadioStations.add(row);
                } else {
                    LOG.info("Error responseCode=" + responseCode + " " + radioStation.getName() + ", stream=" + radioStation.getStreamURL());
                }

            } catch (Exception e) {
                LOG.info("Failed validation " + radioStation.getName() + ", stream=" + radioStation.getStreamURL());
            }
        }

        int invalidCount = radioStations.size() -  validRadioStations.size();
        LOG.info("Done cleaning removed " + invalidCount + " RadioStations");
        saveStations(validRadioStations, RADIO_STATIONS_FILE_VALIDATED);

    }

    private String createRow(RadioStation radioStation) {
        return radioStation.getLang() +
                                ";;" + radioStation.getName() + ";;" + radioStation.getImageURL() + ";;" + radioStation.getShortDescription()
                                + ";;" + radioStation.getStreamURL() + ";#";
    }

    void saveStations(List<String> radioStations, File radioStationsFile) {
        writeLock.lock();
        try {
            LOG.info("Saving " + radioStations.size() + " Radio stations");
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
