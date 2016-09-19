package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCastCatalogLanguage;

import java.util.Set;

public interface PodCastCatalogBuilder {
    Set<BundleBuilder> getBundleBuilders();
    PodCastCatalogLanguage getPodCastCatalogLang();
}
