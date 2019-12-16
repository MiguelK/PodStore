package com.podcastcatalog.service.job;

import com.podcastcatalog.model.PodCastCatalogMetaData;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.service.appstatistic.AppStatisticService;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.service.subscription.FtpOneClient;
import com.podcastcatalog.util.ServerInfo;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PodCastCatalogUpdater implements Job {

    private final static Logger LOG = Logger.getLogger(PodCastCatalogUpdater.class.getName());

    @Override
    public void doWork() {

        try {

            LOG.info("PodCastCatalogUpdater running..." + Thread.currentThread().getName());

            AppStatisticService.getInstance().uploadToOne();

            PodCastCatalogMetaData podCastCatalogMetaData = null;
            for (PodCastCatalogLanguage language : PodCastCatalogLanguage.values()) {

                //TEST only 1
               //if (language != PodCastCatalogLanguage.KR && language != PodCastCatalogLanguage.JP) {
                //     continue;
                //}

                /*if(PodCastCatalogService.getInstance().isMetaDataRegistered(language)) {
                    LOG.info("PodCastCatalogMetaData already exist, will update for lang=" + language);
                    PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(language.create());
                    continue;
                }*/ //No updates i OpenShift MemoryFix

                try {
                    podCastCatalogMetaData = FtpOneClient.getInstance().load(language);
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Failed loading " + language, e);
                }

                if(ServerInfo.isLocalDevMode()) {
                 //  podCastCatalogMetaData = null; //Always build at startup time
                }

                if (podCastCatalogMetaData == null && ServerInfo.isLocalDevMode()) { //Oly update from local memory fix
                    PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(language.create());
                } else if (podCastCatalogMetaData != null) {
                    PodCastCatalogService.getInstance().register(language, podCastCatalogMetaData);
                } else {
                    LOG.log(Level.SEVERE, "Failed loading language=" + language + " in Production.");
                }
            }

            LOG.info("PodCastCatalogUpdater: Done updating");

        } catch (Exception e) {
            LOG.info("PodCastCatalogUpdater: failed, " + e.getMessage());
        }
    }
}
