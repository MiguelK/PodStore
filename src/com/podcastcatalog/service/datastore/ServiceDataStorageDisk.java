package com.podcastcatalog.service.datastore;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.podcastcatalog.model.podcastcatalog.Bundle;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalog;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.subscription.SubscriptionData;
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

public class ServiceDataStorageDisk implements ServiceDataStorage {
    private final static Logger LOG = Logger.getLogger(ServiceDataStorageDisk.class.getName());

    //private static final Gson GSON = new Gson();

    private final File podDataHomeDir;
   // private final File catalogVersionHomeDirSWE;
   // private final File catalogVersionHomeDirUS;
    private static final String SUBSCRIPTION_DATA_FILE_NAME = "SubscriptionData.dat";

    private final File subscriptionDataFile;

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


        //this.catalogVersionHomeDirSWE = new File(podDataHomeDir, "PodCastCatalogVersions" + File.separator + PodCastCatalogLanguage.SE.name());
        // this.catalogVersionHomeDirUS = new File(podDataHomeDir, "PodCastCatalogVersions" + File.separator + PodCastCatalogLanguage.US.name());
        File subscriptionService = new File(podDataHomeDir, "SubscriptionService");

       /* if (!this.catalogVersionHomeDirSWE.exists()) {
            this.catalogVersionHomeDirSWE.mkdirs();
        }

        if (!this.catalogVersionHomeDirUS.exists()) {
            this.catalogVersionHomeDirUS.mkdirs();
        }*/

        if (!subscriptionService.exists()) {
            subscriptionService.mkdirs();
        }

        subscriptionDataFile = new File(subscriptionService, SUBSCRIPTION_DATA_FILE_NAME);
    }


    @Override
    public SubscriptionData loadSubscriptionData() {
        SubscriptionData load = load(subscriptionDataFile, SubscriptionData.class);

        if (load == null) {
            return new SubscriptionData();
        }
        return load;
    }

    @Override
    public void save(SubscriptionData subscriptionData) {
        saveAsObject(subscriptionData, subscriptionDataFile);
    }

    public File getPodDataHomeDir() {
        return podDataHomeDir;
    }

    @Override
    public File getSubscriptionDataFile() {
        return subscriptionDataFile;
    }

    @Override
    public void save(PodCastCatalog podCastCatalog) {

        PodCastCatalogVersion versionDirectory = createNewVersionDirectory(podCastCatalog.getPodCastCatalogLanguage());

        LOG.info("Saving PodCastCatalog to " + versionDirectory.getLangDat().getAbsolutePath());


        saveAsObject(podCastCatalog, versionDirectory.getLangDat());
        File json = saveAsJSON(podCastCatalog, versionDirectory);

        ZipFile.zip(json, versionDirectory.getLangJSONZipped());
    }

    @Override
    public File getCatalogVersionHomeDirectory(PodCastCatalogLanguage language) {

        File catalogPath = new File(podDataHomeDir, "PodCastCatalogVersions" + File.separator + language.name());

        if(!catalogPath.exists()) {
            throw new IllegalStateException("Unknown lang " + language + " dir does not exist " + catalogPath);
        }

        return catalogPath;

       /* switch (language) {
            case SE:
                return catalogVersionHomeDirSWE;
            case US:
                return catalogVersionHomeDirUS;
            default:
                throw new IllegalStateException("Unknown lang " + language);
        }*/
    }


    @Override
    public void deleteAll() {

        for (PodCastCatalogLanguage lang : PodCastCatalogLanguage.values()) {
            File catalogPath = new File(podDataHomeDir, "PodCastCatalogVersions" + File.separator + lang.name());

            if (!catalogPath.exists()) {
                continue;
            }
            try {
                FileUtils.deleteDirectory(catalogPath);
             } catch (IOException e) {
                e.printStackTrace();//FIXME
            }
        }


        /*if (!catalogVersionHomeDirSWE.exists() || !catalogVersionHomeDirSWE.isDirectory()) {
            return;
        }

        try {
            FileUtils.deleteDirectory(catalogVersionHomeDirSWE);
            FileUtils.deleteDirectory(catalogVersionHomeDirUS);
        } catch (IOException e) {
            e.printStackTrace();//FIXME
        }*/
    }


    public Optional<PodCastCatalogVersion> getCurrentVersion(PodCastCatalogLanguage language) {
        List<PodCastCatalogVersion> allVersions = getAllVersions(language);

        if (allVersions.isEmpty()) {
            return Optional.empty();
        }

        PodCastCatalogVersion podCastCatalogVersion = allVersions.get(0);
        podCastCatalogVersion.loadPodCastCatalogFromDisc();

        return Optional.of(podCastCatalogVersion);
    }


    public List<PodCastCatalogVersion> getAllVersions(PodCastCatalogLanguage castCatalogLanguage) {

        List<PodCastCatalogVersion> allVersions = new ArrayList<>();
        List<File> latestVersionDirectories = getVersionDirectories(castCatalogLanguage);

        for (File latestVersionDirectory : latestVersionDirectories) {
            allVersions.add(PodCastCatalogVersion.load(latestVersionDirectory, castCatalogLanguage));
        }
        return allVersions;
    }


    private List<File> getVersionDirectories(PodCastCatalogLanguage language) {

        File catalogVersionHomeDir = getCatalogVersionHomeDirectory(language);

        List<File> files = new ArrayList<>();

        File[] subdirs = catalogVersionHomeDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
        if (subdirs == null) {
            return Collections.emptyList();
        }

        IntStream intStream = Arrays.stream(subdirs).filter(this::isVersionDirectory).mapToInt(m ->
                NumberUtils.toInt(m.getName()));//.sorted().toArray();

        List<Integer> v = intStream
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        Collections.reverse(v);

        for (Integer version : v) {
            File file = new File(catalogVersionHomeDir, String.valueOf(version));
            files.add(file);
        }


        return files;
    }

    private PodCastCatalogVersion createNewVersionDirectory(PodCastCatalogLanguage podCastCatalogLanguage) {
        File catalogVersionHomeDir = getCatalogVersionHomeDirectory(podCastCatalogLanguage);

        File[] subdirs = catalogVersionHomeDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);

        int nextVersionNumber = 1;
        if (subdirs != null) {

            OptionalInt max = Arrays.stream(subdirs).filter(this::isVersionDirectory).mapToInt(m ->
                    NumberUtils.toInt(m.getName())).max();

            if (max.isPresent()) {
                nextVersionNumber = max.getAsInt() + 1;
            }
        }

        File file = new File(catalogVersionHomeDir, String.valueOf(nextVersionNumber));
        file.mkdirs();//FIXME

        return PodCastCatalogVersion.create(file, podCastCatalogLanguage);
    }

    private boolean isVersionDirectory(File e) {
        int i = NumberUtils.toInt(e.getName(), -1);
        return i != -1;
    }

    private <T> T load(File sourceFile, Class<T> sourceType) {

        ObjectInputStream in = null;
        FileInputStream fileIn = null;
        try {
            try {
                fileIn = new FileInputStream(sourceFile);
                in = new ObjectInputStream(fileIn);
                return ((T) in.readObject());
            } catch (IOException | ClassNotFoundException e) {
                LOG.log(Level.INFO, "Unable to load object=" + sourceType + " from=" + sourceFile.getAbsolutePath(), e.getMessage());
            }

        } finally {
            if (in != null) {
                IOUtils.closeQuietly(in);
            }
            if (fileIn != null) {
                IOUtils.closeQuietly(fileIn);
            }
        }
        return null;
    }

    private void saveAsObject(Object object, File targetFile) {
        FileOutputStream fileOut = null;
        ObjectOutputStream out = null;
        try {
            fileOut =
                    new FileOutputStream(targetFile);
            out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Unable to save object " + object + " to = " + targetFile.getAbsolutePath(), e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(fileOut);
        }
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
