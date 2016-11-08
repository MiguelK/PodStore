package com.podcastcatalog.store;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiscStorage implements Storage {

    private final static Logger LOG = Logger.getLogger(DiscStorage.class.getName());

    private final File dataDirectory;

    public DiscStorage(File dataDirectory) {
        if (dataDirectory == null) {
            throw new IllegalArgumentException("dataDirectory is null");
        }
        if (!dataDirectory.isDirectory()) {
            throw new IllegalArgumentException("dataDirectory is not a dir " + dataDirectory.getAbsolutePath());
        }

        this.dataDirectory = dataDirectory;
    }

    private File getFileName(PodCastCatalogLanguage podCastCatalogLanguage) {
        String fileName = podCastCatalogLanguage.name() + ".dat";
        return new File(dataDirectory, fileName);
    }

    @Override
    public Optional<PodCastCatalog> load(PodCastCatalogLanguage podCastCatalogLanguage) {

        File file = getFileName(podCastCatalogLanguage);

        if(!file.exists() || !file.canRead()){
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


    @Override
    public void delete(PodCastCatalogLanguage podCastCatalogLanguage) {
        File file = getFileName(podCastCatalogLanguage);

        file.delete(); //FIXME
    }

    @Override
    public void save(PodCastCatalog podCastCatalog) {
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

        LOG.info("Saved " + podCastCatalog.getPodCastCatalogLanguage() + " to " + file.getAbsolutePath());
    }
}
