package com.podcastcatalog.storage;

import com.google.gson.Gson;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataStorage {
    private final static Logger LOG = Logger.getLogger(DataStorage.class.getName());

    private static final Gson GSON = new Gson();

    private final File podDataHomeDir;
    private final File catalogVersionHomeDir;
    private static final String SUBSCRIPTION_DATA_FILE_NAME = "SubscriptionData.dat";

    private final File subscriptionDataFile;

    public SubscriptionData loadSubscriptionData() {
        return load(subscriptionDataFile, SubscriptionData.class);
    }

    public void save(SubscriptionData subscriptionData) {
        saveAsObject(subscriptionData, subscriptionDataFile);
    }

    public File getPodDataHomeDir() {
        return podDataHomeDir;
    }

    public File getCatalogVersionHomeDir() {
        return catalogVersionHomeDir;
    }

    public File getSubscriptionDataFile() {
        return subscriptionDataFile;
    }

    public void save(PodCastCatalog podCastCatalog) {

        PodCastCatalogVersion versionDirectory = createNewVersionDirectory();
        saveAsObject(podCastCatalog, versionDirectory.getSweDat());
        File json = saveAsJSON(podCastCatalog, versionDirectory);

        ZipFile.zip(json, versionDirectory.getSweJSONZipped());
    }

    public void deleteAll() {
        if (!catalogVersionHomeDir.exists() || !catalogVersionHomeDir.isDirectory()) {
            return;
        }

        try {
            FileUtils.deleteDirectory(catalogVersionHomeDir);
        } catch (IOException e) {
            e.printStackTrace();//FIXME
        }
    }

    public DataStorage(File podDataHomeDir) {
        if (podDataHomeDir == null) {
            throw new IllegalArgumentException("podDataHomeDir is null");
        }
        if (!podDataHomeDir.isDirectory()) {
            throw new IllegalArgumentException("podDataHomeDir is not a dir " + podDataHomeDir.getAbsolutePath());
        }
        this.podDataHomeDir = podDataHomeDir;

        this.catalogVersionHomeDir = new File(podDataHomeDir, "PodCastCatalogVersions");
        File subscriptionService = new File(podDataHomeDir, "SubscriptionService");

        if (!this.catalogVersionHomeDir.exists()) {
            this.catalogVersionHomeDir.mkdirs();
        }
        if (!subscriptionService.exists()) {
            subscriptionService.mkdirs();
        }

        subscriptionDataFile = new File(subscriptionService, SUBSCRIPTION_DATA_FILE_NAME);
    }

    public Optional<PodCastCatalogVersion> getCurrentVersion() {
        List<PodCastCatalogVersion> allVersions = getAllVersions();

        if (allVersions.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(allVersions.get(0));
    }

    public DataStorage() {
        this(new HomeDirectoryLocator().locateDataDirectory());
    }

    List<PodCastCatalogVersion> getAllVersions() {

        List<PodCastCatalogVersion> allVersions = new ArrayList<>();
        List<File> latestVersionDirectories = getVersionDirectories();

        allVersions.addAll(latestVersionDirectories.stream()
                .map(PodCastCatalogVersion::load).collect(Collectors.toList()));

        return allVersions;
    }


    private List<File> getVersionDirectories() {
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

    private PodCastCatalogVersion createNewVersionDirectory() {
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

        return PodCastCatalogVersion.create(file);
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
                LOG.log(Level.SEVERE, "Unable to load object=" + sourceType + " from=" + sourceFile.getAbsolutePath(), e);
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

    private File saveAsJSON(PodCastCatalog podCastCatalog, PodCastCatalogVersion versionDirectory) {

        try {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(versionDirectory.getSweJSON()), "UTF-8")) {
                GSON.toJson(podCastCatalog, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return versionDirectory.getSweJSON();
    }


    public static class PodCastCatalogVersion {
        private int version;
        private final File sweJSON;
        private final File sweJSONZipped;
        private final File sweDat;

        private PodCastCatalogVersion(File versionRoot) {
            sweDat = new File(versionRoot, PodCastCatalogLanguage.Sweden.name() + ".dat");
            sweJSON = new File(versionRoot, PodCastCatalogLanguage.Sweden.name() + ".json");
            sweJSONZipped = new File(versionRoot, PodCastCatalogLanguage.Sweden.name() + "_json.zip");

            make(versionRoot);
        }

        private void make(File versionRoot) {
            try {
                version = Integer.parseInt(versionRoot.getName());
                sweDat.createNewFile();
                sweJSON.createNewFile();
                sweJSONZipped.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static PodCastCatalogVersion create(File versionRoot) {
            PodCastCatalogVersion podCastCatalogVersion = new PodCastCatalogVersion(versionRoot);
            podCastCatalogVersion.make(versionRoot);
            return podCastCatalogVersion;
        }

        static PodCastCatalogVersion load(File versionRoot) {
            PodCastCatalogVersion podCastCatalogVersion = new PodCastCatalogVersion(versionRoot);

            podCastCatalogVersion.readFromDisc();

            return podCastCatalogVersion;
        }

        private PodCastCatalog podCastCatalog;

        private void readFromDisc() {

            ObjectInputStream in = null;
            FileInputStream fileIn = null;
            try {
                try {
                    fileIn = new FileInputStream(sweDat);
                    in = new ObjectInputStream(fileIn);
                    podCastCatalog = ((PodCastCatalog) in.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    LOG.log(Level.SEVERE, "Unable to load PodCastCatalog=" + sweDat.getAbsolutePath(), e);
                }

            } finally {
                if (in != null) {
                    IOUtils.closeQuietly(in);
                }
                if (fileIn != null) {
                    IOUtils.closeQuietly(fileIn);
                }
            }
        }

        File getSweJSON() {
            return sweJSON;
        }

        public File getSweJSONZipped() {
            return sweJSONZipped;
        }

        File getSweDat() {
            return sweDat;
        }

        public int getVersion() {
            return version;
        }

        public PodCastCatalog getPodCastCatalogSwedish() {
            return podCastCatalog;
        }

        @Override
        public String toString() {
            return "PodCastCatalogVersion{" +
                    "version=" + version +
                    ", sweJSON=" + sweJSON +
                    ", sweJSONZipped=" + sweJSONZipped +
                    ", sweDat=" + sweDat +
                    ", podCastCatalog=" + podCastCatalog +
                    '}';
        }
    }
}
