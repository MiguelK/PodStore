package com.podcastcatalog.util;

import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


public class IOUtil {

    private final static Logger LOG = Logger.getLogger(ItunesSearchAPI.class.getName());

    public  static void dump(URL request) {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) request.openConnection();
            String content = getResultContent(connection);
            System.out.println(content);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to get Itunes Search API result using query=" + request, e);
        }

    }

    public static String getResultContent(HttpURLConnection connection) {

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));

            String input;

            while ((input = bufferedReader.readLine()) != null) {
                stringBuilder.append(input);
                stringBuilder.append(System.lineSeparator());
            }

        } catch (Exception e) {
            LOG.warning("Unable to read content from connection " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(bufferedReader);
        }

        return stringBuilder.toString();
    }
}
