package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.bundle.Bundle;

import java.util.List;
import java.util.Set;

public interface PodCastCatalogBuilder {
    Set<BundleBuilder> getBundleBuilders();

    PodCastCatalogLanguage getPodCastCatalogLang();

    List<Bundle> createFromFetchedData(List<PodCast> podCas,
                                       List<PodCastCategory> PodCastCategories);
}
