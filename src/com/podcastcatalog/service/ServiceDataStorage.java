package com.podcastcatalog.service;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalog;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.subscription.SubscriptionData;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Optional;

public interface ServiceDataStorage {

    static ServiceDataStorage useDefault() {
        return new ServiceDataStorageDisk();
    }

    SubscriptionData loadSubscriptionData();

    void save(SubscriptionData subscriptionData);

    void save(PodCastCatalog podCastCatalog);

    File getCatalogVersionHomeDir();

    File getSubscriptionDataFile();

    void deleteAll();

    Optional<ServiceDataStorage.PodCastCatalogVersion> getCurrentVersion();

    List<ServiceDataStorage.PodCastCatalogVersion> getAllVersions();

    class PodCastCatalogVersion {
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
                    e.printStackTrace();//FIXME
//                    LOG.log(Level.SEVERE, "Unable to load PodCastCatalog=" + sweDat.getAbsolutePath(), e);
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

        public File getSweJSON() {
            return sweJSON;
        }

        public File getSweJSONZipped() {
            return sweJSONZipped;
        }

        public File getSweDat() {
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
