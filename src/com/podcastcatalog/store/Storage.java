package com.podcastcatalog.store;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;

import java.util.Optional;

public interface Storage {
    void delete(PodCastCatalogLanguage podCastCatalogLanguage);

    Optional<PodCastCatalog> load(PodCastCatalogLanguage podCastCatalogLanguage);

    void save(PodCastCatalog podCastCatalog);
}
