package com.podcastcatalog;

import com.podcastcatalog.modelbuilder.PodCastCatalogBuilderSE;
import com.podcastcatalog.service.ServiceDataStorage;
import com.podcastcatalog.service.ServiceDataStorageDisk;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

public class StartupServlet extends HttpServlet {

    private final static Logger LOG = Logger.getLogger(StartupServlet.class.getName());

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        LOG.info("About to start PodStore..");

        ServiceDataStorage serviceDataStorageDisk = ServiceDataStorage.useDefault();
        PodCastCatalogService.getInstance().setStorage(serviceDataStorageDisk);

        LOG.info("Starting PodCastCatalog..., working dir= " + serviceDataStorageDisk);

//        JobManagerService.getInstance().registerJob(new SubscriptionNotifierJob(), 10, TimeUnit.SECONDS); //FIXME
//        JobManagerService.getInstance().registerJob(new PodCastCatalogUpdater(), 20, TimeUnit.HOURS); //FIXME
//        JobManagerService.getInstance().startAsync();
//
//        PodCastSubscriptionService.getInstance().start();

        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilderSE());//FIXME English

        Optional<ServiceDataStorageDisk.PodCastCatalogVersion> currentVersion = serviceDataStorageDisk.getCurrentVersion();

        if (currentVersion.isPresent()) {
            LOG.info("Trying to load existing catalog " + currentVersion.get());
            PodCastCatalogService.getInstance().loadPodCastCatalog(currentVersion.get().getPodCastCatalogSwedish());
            PodCastCatalogService.getInstance().buildIndexAsync();
        } else {
            LOG.info("No catalog exists. in homeDir=" + serviceDataStorageDisk);
            PodCastCatalogService.getInstance().buildPodCastCatalogsAsync();
        }
        //        registerOrStartLoading(dataStorage.load(PodCastCatalogLanguage.Sweden)); //FIXME English

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }
}
