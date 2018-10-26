package com.podcastcatalog.service.job;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.util.ServerInfo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class PodCastCatalogUpdater implements Job {

    private final static Logger LOG = Logger.getLogger(PodCastCatalogUpdater.class.getName());

    @Override
    public void doWork() {


        LOG.info("PodCastCatalogUpdater start building Catalog SE");

        Future future = PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(PodCastCatalogLanguage.SE);

        try {
            //Minimize memory only one building at a time
            long sleepMillis =  TimeUnit.MINUTES.toMillis(20); // 20 minuts
            Thread.sleep(sleepMillis);
     //       future.get(20, TimeUnit.MINUTES);
        } catch (Exception e) {
            LOG.info("Building SE Catalog took more than 10 minutes");
        }

        LOG.info("PodCastCatalogUpdater start building Catalog US");
        PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(PodCastCatalogLanguage.US);

        /*
        for (PodCastCatalogLanguage language : PodCastCatalogLanguage.values()) {
            PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(language);
        }*/
    }
}
