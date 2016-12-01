package com.podcastcatalog.modelbuilder;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastcatalog.PodCastCategory;
import com.podcastcatalog.model.podcastcatalog.Bundle;

import java.util.List;
import java.util.Set;

public interface PodCastCatalogBuilder {
    Set<BundleBuilder> getBundleBuilders();

    PodCastCatalogLanguage getPodCastCatalogLang();

    List<Bundle> createFromFetchedData(List<PodCast> podCas,
                                       List<PodCastCategory> PodCastCategories);
}
