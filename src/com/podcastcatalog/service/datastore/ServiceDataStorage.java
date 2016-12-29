package com.podcastcatalog.service.datastore;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalog;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.subscription.SubscriptionData;

import java.io.File;
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

    File getCatalogVersionHomeDirectory(PodCastCatalogLanguage language);

    File getSubscriptionDataFile();

    void deleteAll();

    Optional<PodCastCatalogVersion> getCurrentVersion(PodCastCatalogLanguage language);

    List<PodCastCatalogVersion> getAllVersions(PodCastCatalogLanguage castCatalogLanguage);

}
