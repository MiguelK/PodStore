package com.podcastcatalog;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.modelbuilder.PodCastCatalogBuilder;
import com.podcastcatalog.service.datastore.PodCastCatalogVersion;
import com.podcastcatalog.service.datastore.ServiceDataStorage;
import com.podcastcatalog.service.job.CreateLinkPages;
import com.podcastcatalog.service.job.JobManagerService;
import com.podcastcatalog.service.job.MemoryDumperJob;
import com.podcastcatalog.service.job.PodCastCatalogUpdater;
import com.podcastcatalog.service.job.UpdateSearchSuggestionsJob;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
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


        System.out.println("StartupServlet....isLocalDevMode = " + ServerInfo.isLocalDevMode());
        LOG.info("About to start PodStore...");

        ServiceDataStorage serviceDataStorageDisk = ServiceDataStorage.useDefault();
        PodCastCatalogService.getInstance().setStorage(serviceDataStorageDisk);

        LOG.info("Starting PodCastCatalog..., working dir= " + serviceDataStorageDisk.getPodDataHomeDir().getAbsolutePath());

        //OPENSHIFT_APP_DNS //FIXME SE or US
        // JobManagerService.getInstance().registerJob(new SubscriptionNotifierJob(), 10, TimeUnit.SECONDS); //FIXME
        JobManagerService.getInstance().registerJob(new CreateLinkPages(),20,20, TimeUnit.SECONDS);
        JobManagerService.getInstance().registerJob(new PodCastCatalogUpdater(), 20, TimeUnit.HOURS); //FIXME
        JobManagerService.getInstance().registerJob(new MemoryDumperJob(), 120, TimeUnit.MINUTES); //FIXME change time, remove

        int period = 30 * 3600;

        int counter = 0;
        for (PodCastCatalogLanguage language : PodCastCatalogLanguage.values()) {

            if(ServerInfo.isLocalDevMode() || !language.isInMemorySearchSuggestions()) {
                continue;
            }

            JobManagerService.getInstance().registerJob(new UpdateSearchSuggestionsJob(language),
                    5 + counter, period, TimeUnit.SECONDS); //FIXME change time, remove
            counter += 15;
        }

        //FIXME prod
        loadPodCastCatalog(PodCastCatalogLanguage.US);
        loadPodCastCatalog(PodCastCatalogLanguage.SE);

        /*if (ServerInfo.isLocalDevMode()) {
            //Only executed locally, memory issue to have all catalogs in JVM

            for (PodCastCatalogLanguage language : PodCastCatalogLanguage.values()) {
        //        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(language.create());
            }

            PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(PodCastCatalogLanguage.SE.create());

            PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(PodCastCatalogLanguage.SE); //.get(10,TimeUnit.SECONDS);


            for (PodCastCatalogLanguage language : PodCastCatalogLanguage.values()) {

                if(serviceDataStorageDisk.exists(language)) {
                    LOG.info("Zip file already exist for " + language);
                    continue;
                }

                try {
                    PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(language); //.get(10,TimeUnit.SECONDS);
                } catch (Exception e) {
                    LOG.info(e.getMessage());
                }
            }
        } else {
          loadPodCastCatalog(PodCastCatalogLanguage.US.create());
          loadPodCastCatalog(PodCastCatalogLanguage.SE.create());
        }*/

        JobManagerService.getInstance().startAsync();

      //  PodCastSubscriptionService.getInstance().start();
    }

    private void loadPodCastCatalog(PodCastCatalogLanguage language) {

        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(language.create());

        Optional<PodCastCatalogVersion> currentVersion = ServiceDataStorage.useDefault().getCurrentVersion(language);

        if (!currentVersion.isPresent()) {
            LOG.info("No catalog exists. in homeDir=" + ServiceDataStorage.useDefault().getPodDataHomeDir().getAbsolutePath() + " for language=" + language);
            PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(language);
            return;
        }

        LOG.info("PodCastCatalog " + currentVersion.get()
                + " exists, loadInMemory=" + language.isInMemory()
                + ", buildInMemoryIndex=" + language.isInMemoryIndex());

        if(language.isInMemory()) {
            LOG.info("Loading existing PodCastCatalog " + currentVersion.get() + " inMemory");

            currentVersion.get().loadPodCastCatalogFromDisc(); //Load .dat file used as PodCastCatalog
            PodCastCatalogService.getInstance().loadPodCastCatalog(currentVersion.get().getPodCastCatalog());
        }

        if(language.isInMemoryIndex()){
            LOG.info("Building search index for existing PodCastCatalog " + currentVersion.get());
            PodCastCatalogService.getInstance().buildIndexAsync(language);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }
}
