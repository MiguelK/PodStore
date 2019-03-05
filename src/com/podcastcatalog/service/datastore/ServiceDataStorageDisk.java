package com.podcastcatalog.service.datastore;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalog;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.subscription.SubscriptionData;
import com.podcastcatalog.service.subscription.FtpOneClient;
import com.podcastcatalog.util.ServerInfo;
import com.podcastcatalog.util.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class ServiceDataStorageDisk  {
    private final static Logger LOG = Logger.getLogger(ServiceDataStorageDisk.class.getName());

    //private static final Gson GSON = new Gson();

    private final File podDataHomeDir;
    private static final String SUBSCRIPTION_DATA_FILE_NAME = "SubscriptionData.dat";


    ServiceDataStorageDisk() {
        this(new LocatorProduction().getPodDataHomeDirectory());
    }

    public ServiceDataStorageDisk(File podDataHomeDir) {
        if (podDataHomeDir == null) {
            throw new IllegalArgumentException("podDataHomeDir is null");
        }
        if (!podDataHomeDir.isDirectory()) {
            throw new IllegalArgumentException("podDataHomeDir is not a dir " + podDataHomeDir.getAbsolutePath());
        }
        this.podDataHomeDir = podDataHomeDir;

        for (PodCastCatalogLanguage lang : PodCastCatalogLanguage.values()) {
            File catalogPath = new File(podDataHomeDir, "PodCastCatalogVersions" + File.separator + lang.name());
            if (!catalogPath.exists()) {
                LOG.info("Creating lang directory " + catalogPath.getName());
                catalogPath.mkdirs();
            }
        }

     /*   File subscriptionService = new File(podDataHomeDir, "SubscriptionService");

        if (!subscriptionService.exists()) {
            subscriptionService.mkdirs();
        }

        subscriptionDataFile = new File(subscriptionService, SUBSCRIPTION_DATA_FILE_NAME);*/
    }


    public boolean exists(PodCastCatalogLanguage lang) {
        String name = lang.name() + "_json.zip";
        File devFile = new File(podDataHomeDir, "PodCastCatalogVersions" + File.separator + name);

        return devFile.exists();

    }



    public File getPodDataHomeDir() {
        return podDataHomeDir;
    }


    private static final Gson GSON;// = new Gson();

    static {
        List<String> fieldExclusions = new ArrayList<String>();
        fieldExclusions.add("podCastEpisodesInternal");

        List<Class<?>> classExclusions = new ArrayList<Class<?>>();
       // classExclusions.add(PodCast.class);
        GSON = GsonFactory.build(fieldExclusions, classExclusions);
    }

    public static class GsonFactory {

        public static Gson build(final List<String> fieldExclusions, final List<Class<?>> classExclusions) {
            GsonBuilder b = new GsonBuilder();
            b.addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return fieldExclusions == null ? false : fieldExclusions.contains(f.getName());
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return classExclusions == null ? false : classExclusions.contains(clazz);
                }
            });
            return b.create();

        }
    }

    private File saveAsJSON(PodCastCatalog podCastCatalog, PodCastCatalogVersion versionDirectory) {

        try {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(versionDirectory.getLangJSON()), "UTF-8")) {
                GSON.toJson(podCastCatalog, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return versionDirectory.getLangJSON();
    }

}
