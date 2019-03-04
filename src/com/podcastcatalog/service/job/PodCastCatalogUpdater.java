package com.podcastcatalog.service.job;

import com.podcastcatalog.model.PodCastCatalogMetaData;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.service.subscription.FtpOneClient;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PodCastCatalogUpdater implements Job {

    private final static Logger LOG = Logger.getLogger(PodCastCatalogUpdater.class.getName());

    @Override
    public void doWork() {

        try {

            LOG.info("PodCastCatalogUpdater running...");

            PodCastCatalogMetaData podCastCatalogMetaData = null;
            for (PodCastCatalogLanguage language : PodCastCatalogLanguage.values()) {

                //TEST only 1
                if (language != PodCastCatalogLanguage.NO) {
                    continue;
                }

                try {
                    podCastCatalogMetaData = FtpOneClient.getInstance().load(language);
                    LOG.info("Loaded metadata from one.com for lang=" + language);
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Failed loading " + language, e);
                }

                if (podCastCatalogMetaData == null) {
                    PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(language.create());

                } else {
                    LOG.info("Loaded podCastCatalogMetaData from one.com.");
                    PodCastCatalogService.getInstance().register(language, podCastCatalogMetaData);
                }
            }

        } catch (Exception e) {
            LOG.info("Building  Catalog took more than 10 minutes");
        }
    }
}
