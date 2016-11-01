package com.podcastcatalog.store;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;

import java.util.Optional;

public interface Storage {

//    Storage storageStrategy = new DiscStorage();

    /*static Optional<PodCastCatalog> load(PodCastCatalogLanguage podCastCatalogLanguage){

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
    }*/

    void delete(PodCastCatalogLanguage podCastCatalogLanguage);

    Optional<PodCastCatalog> load(PodCastCatalogLanguage podCastCatalogLanguage);

    void save(PodCastCatalog podCastCatalog);
//    void delete(String fileName);

}
