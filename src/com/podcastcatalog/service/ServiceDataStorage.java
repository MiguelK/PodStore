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

    File getPodDataHomeDir();

    File getCatalogVersionHomeDirSWE();

    File getSubscriptionDataFile();

    void deleteAll();

    Optional<ServiceDataStorage.PodCastCatalogVersion> getCurrentVersion(PodCastCatalogLanguage language);

    List<PodCastCatalogVersion> getAllVersions(PodCastCatalogLanguage castCatalogLanguage);


    class PodCastCatalogVersion {
        private int version;
        private final File langJSON;
        private final File langJSONZipped;
        private final File langDat;

        private transient PodCastCatalog podCastCatalog; //Only load latest version in-memory

        private PodCastCatalogVersion(File versionRoot, PodCastCatalogLanguage podCastCatalogLanguage) {
            langDat = new File(versionRoot, podCastCatalogLanguage.name() + ".dat");
            langJSON = new File(versionRoot, podCastCatalogLanguage.name() + ".json");
            langJSONZipped = new File(versionRoot, podCastCatalogLanguage.name() + "_json.zip");

            make(versionRoot);
        }

        private void make(File versionRoot) {
            try {
                version = Integer.parseInt(versionRoot.getName());
                langDat.createNewFile();
                langJSON.createNewFile();
                langJSONZipped.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static PodCastCatalogVersion create(File versionRoot, PodCastCatalogLanguage podCastCatalogLanguage) {
            PodCastCatalogVersion podCastCatalogVersion = new PodCastCatalogVersion(versionRoot, podCastCatalogLanguage);
            podCastCatalogVersion.make(versionRoot);
            return podCastCatalogVersion;
        }

        static PodCastCatalogVersion load(File versionRoot, PodCastCatalogLanguage podCastCatalogLanguage) {
            return new PodCastCatalogVersion(versionRoot, podCastCatalogLanguage);
        }

        void loadPodCastCatalogFromDisc() {

            ObjectInputStream in = null;
            FileInputStream fileIn = null;
            try {
                fileIn = new FileInputStream(langDat);
                in = new ObjectInputStream(fileIn);
                podCastCatalog = ((PodCastCatalog) in.readObject());
            } catch (Exception e) {

                throw new RuntimeException("Unable to read PodCastCatalog " + langDat.getAbsolutePath(), e);
            } finally {
                if (in != null) {
                    IOUtils.closeQuietly(in);
                }
                if (fileIn != null) {
                    IOUtils.closeQuietly(fileIn);
                }
            }
        }

        public File getLangJSON() {
            return langJSON;
        }

        public File getLangJSONZipped() {
            return langJSONZipped;
        }

        public File getLangDat() {
            return langDat;
        }

        public int getVersion() {
            return version;
        }

        public PodCastCatalog getPodCastCatalog() {
            return podCastCatalog;
        }

        @Override
        public String toString() {
            return "PodCastCatalogVersion{" +
                    "version=" + version +
                    ", langJSON=" + langJSON +
                    ", langJSONZipped=" + langJSONZipped +
                    ", langDat=" + langDat +
                    ", podCastCatalog=" + podCastCatalog +
                    '}';
        }
    }

}
