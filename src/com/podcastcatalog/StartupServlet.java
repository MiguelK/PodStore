package com.podcastcatalog;

import com.podcastcatalog.builder.PodCastCatalogBuilderSE;
import com.podcastcatalog.job.JobManager;
import com.podcastcatalog.job.SubscriptionNotifierJob;
import com.podcastcatalog.store.DataStorage;
import com.podcastcatalog.subscribe.PodCastSubscriptionService;

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

        DataStorage dataStorage = new DataStorage();

        LOG.info("Starting PodCastCatalog..., working dir= " + dataStorage);

        JobManager.getInstance().registerJob(new SubscriptionNotifierJob(), 10, TimeUnit.SECONDS); //FIXME
        JobManager.getInstance().startAsync();

        PodCastSubscriptionService.getInstance().loadFromDiskAsync(dataStorage);

        PodCastCatalogService.getInstance().setStorage(dataStorage);
        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilderSE());//FIXME English

        Optional<DataStorage.PodCastCatalogVersion> currentVersion = dataStorage.getCurrentVersion();

        if (currentVersion.isPresent()) {
            LOG.info("Trying to load existing catalog " + currentVersion.get());
            PodCastCatalogService.getInstance().loadPodCastCatalog(currentVersion.get().getPodCastCatalogSwedish());
            PodCastCatalogService.getInstance().buildIndexAsync();
        } else {
            LOG.info("No catalog exists. in homeDir=" + dataStorage.getRootDir().getAbsolutePath());
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
