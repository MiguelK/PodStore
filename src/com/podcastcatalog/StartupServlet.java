package com.podcastcatalog;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.PodCastCatalogBuilder;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderCN;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderDE;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderES;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderFR;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderNO;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderSE;
import com.podcastcatalog.modelbuilder.language.PodCastCatalogBuilderUS;
import com.podcastcatalog.service.datastore.PodCastCatalogVersion;
import com.podcastcatalog.service.datastore.ServiceDataStorage;
import com.podcastcatalog.service.job.JobManagerService;
import com.podcastcatalog.service.job.MemoryDumperJob;
import com.podcastcatalog.service.job.PodCastCatalogUpdater;
import com.podcastcatalog.service.job.SubscriptionNotifierJob;
import com.podcastcatalog.service.job.UpdateSearchSuggestionsJob;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.service.subscription.PodCastSubscriptionService;
import com.podcastcatalog.util.ServerInfo;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class StartupServlet extends HttpServlet {

    private final static Logger LOG = Logger.getLogger(StartupServlet.class.getName());

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        //Important for FeedParser, could cause 403 otherwise.
        System.setProperty("http.agent", "Chrome");


        System.out.println("StartupServlet....serverMode SE and US " + ServerInfo.isUSMode());
        LOG.info("About to start PodStore...");

        ServiceDataStorage serviceDataStorageDisk = ServiceDataStorage.useDefault();
        PodCastCatalogService.getInstance().setStorage(serviceDataStorageDisk);

        LOG.info("Starting PodCastCatalog..., working dir= " + serviceDataStorageDisk.getPodDataHomeDir().getAbsolutePath());

        //OPENSHIFT_APP_DNS //FIXME SE or US
        // JobManagerService.getInstance().registerJob(new SubscriptionNotifierJob(), 10, TimeUnit.SECONDS); //FIXME
        JobManagerService.getInstance().registerJob(new PodCastCatalogUpdater(), 20, TimeUnit.HOURS); //FIXME
        JobManagerService.getInstance().registerJob(new MemoryDumperJob(), 60, TimeUnit.MINUTES); //FIXME change time, remove
        int period = 30 * 3600;


        int counter = 0;
        for (PodCastCatalogLanguage language : PodCastCatalogLanguage.values()) {
            JobManagerService.getInstance().registerJob(new UpdateSearchSuggestionsJob(language),5 + counter, period, TimeUnit.SECONDS); //FIXME change time, remove

            counter += 7;
        }

        JobManagerService.getInstance().startAsync();

        PodCastSubscriptionService.getInstance().start();

        if (ServerInfo.isLocalDevMode()) {
            //Only executed locally, memory issue to have all catalogs in JVM
            loadPodCastCatalog(serviceDataStorageDisk, new PodCastCatalogBuilderSE());
            loadPodCastCatalog(serviceDataStorageDisk, new PodCastCatalogBuilderES());
            loadPodCastCatalog(serviceDataStorageDisk, new PodCastCatalogBuilderNO());
            loadPodCastCatalog(serviceDataStorageDisk, new PodCastCatalogBuilderFR());
            loadPodCastCatalog(serviceDataStorageDisk, new PodCastCatalogBuilderUS());
            loadPodCastCatalog(serviceDataStorageDisk, new PodCastCatalogBuilderCN());
            loadPodCastCatalog(serviceDataStorageDisk, new PodCastCatalogBuilderDE());
        } else {
            loadPodCastCatalog(serviceDataStorageDisk, new PodCastCatalogBuilderUS());
            loadPodCastCatalog(serviceDataStorageDisk, new PodCastCatalogBuilderSE());
        }
    }

    private void loadPodCastCatalog(ServiceDataStorage serviceDataStorageDisk, PodCastCatalogBuilder builder) {

        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(builder);

        PodCastCatalogLanguage language = builder.getPodCastCatalogLang();
        Optional<PodCastCatalogVersion> currentVersion = serviceDataStorageDisk.getCurrentVersion(language);
        if (currentVersion.isPresent()) {
            LOG.info("Loading existing PodCastCatalog " + currentVersion.get());
            PodCastCatalogService.getInstance().loadPodCastCatalog(currentVersion.get().getPodCastCatalog());

            if (!ServerInfo.isLocalDevMode()) {
                PodCastCatalogService.getInstance().buildIndexAsync(language);
            }
        } else {
            LOG.info("No catalog exists. in homeDir=" + serviceDataStorageDisk);
            PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(language);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }
}
