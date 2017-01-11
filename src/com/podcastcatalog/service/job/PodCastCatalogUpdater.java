package com.podcastcatalog.service.job;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;

import java.util.logging.Logger;

public class PodCastCatalogUpdater implements Job {

    private final static Logger LOG = Logger.getLogger(PodCastCatalogUpdater.class.getName());

    @Override
    public void doWork() {

        LOG.info(PodCastCatalogUpdater.class.getSimpleName() + " doWork()...");

        for (PodCastCatalogLanguage language : PodCastCatalogLanguage.values()) {
            PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(language);
        }
    }
}
