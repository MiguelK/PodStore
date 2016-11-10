package com.podcastcatalog.store;

import com.google.gson.Gson;
import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataStorage {

    private final static Logger LOG = Logger.getLogger(DataStorage.class.getName());

    private static final Gson GSON = new Gson();

    private final File dataDirectory;

    public DataStorage(File dataDirectory) {
        if (dataDirectory == null) {
            throw new IllegalArgumentException("dataDirectory is null");
        }
        if (!dataDirectory.isDirectory()) {
            throw new IllegalArgumentException("dataDirectory is not a dir " + dataDirectory.getAbsolutePath());
        }

        this.dataDirectory = dataDirectory;
    }

    public DataStorage() {
        this.dataDirectory = new HomeDirectoryLocator().locateDataDir();//FIXME
    }

    private File getFileName(PodCastCatalogLanguage podCastCatalogLanguage) {
        String fileName = podCastCatalogLanguage.name() + ".dat";
        return new File(dataDirectory, fileName);
    }

    public Optional<PodCastCatalog> load(PodCastCatalogLanguage podCastCatalogLanguage) {

        File file = getFileName(podCastCatalogLanguage);

        if (!file.exists() || !file.canRead()) {
            return Optional.empty();
        }

        ObjectInputStream in = null;
        FileInputStream fileIn = null;
        try {
            try {
                fileIn = new FileInputStream(file);
                in = new ObjectInputStream(fileIn);
                return Optional.of((PodCastCatalog) in.readObject());
            } catch (IOException | ClassNotFoundException e) {
                LOG.log(Level.SEVERE, "Unable to load PodCastCatalog=" + file.getAbsolutePath(), e);
            }

        } finally {
            if (in != null) {
                IOUtils.closeQuietly(in);
            }
            if (fileIn != null) {
                IOUtils.closeQuietly(fileIn);
            }
        }

        return Optional.empty();
    }


    public void delete(PodCastCatalogLanguage podCastCatalogLanguage) {
        File file = getFileName(podCastCatalogLanguage);

        file.delete(); //FIXME
    }

    public void save(PodCastCatalog podCastCatalog) {
        saveAsObject(podCastCatalog);
        saveAsJSON(podCastCatalog);

//        LOG.info("Saved " + podCastCatalog.getPodCastCatalogLanguage() + " to " + file.getAbsolutePath());
    }

    private void saveAsObject(PodCastCatalog podCastCatalog) {
        File file = getFileName(podCastCatalog.getPodCastCatalogLanguage());

        FileOutputStream fileOut = null;
        ObjectOutputStream out = null;
        try {

            if (!file.exists()) {
                file.createNewFile();
            }

            fileOut =
                    new FileOutputStream(file);
            out = new ObjectOutputStream(fileOut);
            out.writeObject(podCastCatalog);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Unable to save PodCastCatalog " + file.getAbsolutePath(), e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(fileOut);
        }
    }

    private void saveAsJSON(PodCastCatalog podCastCatalog) {
        String s1 = podCastCatalog.getPodCastCatalogLanguage().name() + ".json";//FIXME
        File targetFile = new File(dataDirectory, s1);

        try {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8")) {
                GSON.toJson(podCastCatalog, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File get() {
        File zipFile = new File(dataDirectory, "Sweden.json.zip");
        return zipFile;
    }

    public Optional<PodCastCatalogVersion> getCurrentVersion() {

        return null;
    }

    public static class PodCastCatalogVersion{

        public int getVersion() {
            return 0;
        }
    }

   /* public Optional<PodCastCatalog> loadJSON(PodCastCatalogLanguage podCastCatalogLanguage) {

        File targetFile = getTargetFile(podCastCatalogLanguage);

        try {
            try(Reader reader = new InputStreamReader(new FileInputStream(targetFile), "UTF-8")){
                PodCastCatalog p = GSON.fromJson(reader, PodCastCatalog.class);
                return Optional.ofNullable(p);
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }*/
}
