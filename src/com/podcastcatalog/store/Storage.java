package com.podcastcatalog.store;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;

import java.io.IOException;
import java.util.Optional;

public interface Storage {

    Storage storageStrategy = new DiscStorage();

    static Optional<PodCastCatalog> load(PodCastCatalogLanguage podCastCatalogLanguage){

       String assetName = getFileName(podCastCatalogLanguage);

        try {
             return storageStrategy.load(assetName);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    static String getFileName(PodCastCatalogLanguage podCastCatalogLanguage) {
        return podCastCatalogLanguage.name() + ".dat";
    }

    static void  save(PodCastCatalog podCastCatalog) {
        String assetName = getFileName(podCastCatalog.getPodCastCatalogLanguage());

        try {
            storageStrategy.save(assetName, podCastCatalog);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void delete(PodCastCatalogLanguage podCastCatalogLanguage) {
        String assetName = getFileName(podCastCatalogLanguage);

        try {
            storageStrategy.delete(assetName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Optional<PodCastCatalog> load(String fileName) throws Exception;
    void save(String assetName, PodCastCatalog podCastCatalog) throws IOException;
    void delete(String fileName);

}
