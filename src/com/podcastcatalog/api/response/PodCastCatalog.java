package com.podcastcatalog.api.response;

import com.podcastcatalog.api.response.bundle.Bundle;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class PodCastCatalog implements Serializable {

    private final PodCastCatalogLanguage podCastCatalogLanguage;
    private final LocalDateTime created;
    private final List<Bundle> bundles;
    private  PodCastCatalog(PodCastCatalogLanguage podCastCatalogLanguage, List<Bundle> bundles) {

        if(podCastCatalogLanguage==null){
            throw new IllegalArgumentException("podCastCatalogLanguage is mandatory");
        }

        if(bundles ==null || bundles.isEmpty()){
            throw new IllegalArgumentException("bundles is mandatory");
        }

        this.created = LocalDateTime.now();
        this.bundles = Collections.unmodifiableList(bundles);
        this.podCastCatalogLanguage = podCastCatalogLanguage;
    }

    public List<Bundle> getBundles() {
        return bundles;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public static PodCastCatalog create(PodCastCatalogLanguage podCastCatalogLanguage, List<Bundle> bundles) {
        return new PodCastCatalog(podCastCatalogLanguage, bundles);
    }

    public PodCastCatalogLanguage getPodCastCatalogLanguage() {
        return podCastCatalogLanguage;
    }

    @Override
    public String toString() {
        return "PodCastCatalog{" +
                "podCastCatalogLanguage=" + podCastCatalogLanguage +
                ", created=" + created +
                ", bundles=" + bundles.size() +
                '}';
    }
}
