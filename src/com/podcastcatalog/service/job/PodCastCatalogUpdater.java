package com.podcastcatalog.service.job;

import com.podcastcatalog.model.PodCastCatalogMetaData;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.service.subscription.FtpOneClient;
import com.podcastcatalog.util.ServerInfo;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PodCastCatalogUpdater implements Job {

    private final static Logger LOG = Logger.getLogger(PodCastCatalogUpdater.class.getName());

    @Override
    public void doWork() {


        //FIXME Build all PodCastCatalogMetaData....

        try {

            LOG.info("PodCastCatalogUpdater start building Catalog SE");

        PodCastCatalogMetaData podCastCatalogMetaData = null;
        for (PodCastCatalogLanguage language : PodCastCatalogLanguage.values()) {

            //TEST only 1
            if(language != PodCastCatalogLanguage.NO) {
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
        LOG.info("Building SE Catalog took more than 10 minutes");
    }
        /*Future future = PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(PodCastCatalogLanguage.SE);

        try {
            //Minimize memory only one building at a time
            long sleepMillis =  TimeUnit.HOURS.toMillis(2);
            LOG.info("PodCastCatalogUpdater start building Catalog US, waited 2 hours");

            Thread.sleep(sleepMillis);
     //       future.get(20, TimeUnit.MINUTES);
        } catch (Exception e) {
            LOG.info("Building SE Catalog took more than 10 minutes");
        }

        LOG.info("PodCastCatalogUpdater start building Catalog US");
        PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(PodCastCatalogLanguage.US);
*/
        /*
        for (PodCastCatalogLanguage language : PodCastCatalogLanguage.values()) {
            PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(language);
        }*/
    }
}
