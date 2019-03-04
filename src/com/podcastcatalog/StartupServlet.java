package com.podcastcatalog;

import com.podcastcatalog.model.PodCastCatalogMetaData;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.service.datastore.ServiceDataStorage;
import com.podcastcatalog.service.job.JobManagerService;
import com.podcastcatalog.service.job.MemoryDumperJob;
import com.podcastcatalog.service.job.PodCastCatalogUpdater;
import com.podcastcatalog.service.job.SubscriptionNotifierJob;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.service.subscription.FtpOneClient;
import com.podcastcatalog.service.subscription.PodCastSubscriptionService;
import com.podcastcatalog.util.ServerInfo;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Features of PodStore-Server: (Same behavar in dev and prod except LinkPage creator)
 * 1. Create new catalog for every lang each xxx hour
 * 2. Upload lang zip files to one.com (fetched by App client)
 * 3. Upload lang podcast/podcastEpisode text search index to one.com (fetched by PodStore-Server at startup)
 * 4. Only search indexes is existing in-memory + Subscription/Subscribers
 *
 * Server at startup:
 * Try load all lang search indexes into memory from one.com
 * if index does not exists start (building process#1)
 *
 * Lang data stored on one.com:
 * TextSearchIndex + podCastTitles + podCastTitlesTrending + popularSearchQueries
 *
 *
 * #1 (building process)
 * 1. Create PodCastcatalog, export to one.com
 * 2 Index catalog, export PodCastServer to one.com
 */
public class StartupServlet extends HttpServlet {

    private final static Logger LOG = Logger.getLogger(StartupServlet.class.getName());

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        ServiceDataStorage serviceDataStorageDisk = ServiceDataStorage.useDefault();

        LOG.info("Starting PodStore POD_HOME_DIR=" + serviceDataStorageDisk.getPodDataHomeDir().getAbsolutePath());

        setupPodCastSubscriptionService(servletConfig, serviceDataStorageDisk);

        //Important for FeedParser, could cause 403 otherwise.
        System.setProperty("http.agent", "Chrome");

        LOG.info("Starting PodCastCatalog..., working dir= " + serviceDataStorageDisk.getPodDataHomeDir().getAbsolutePath());

        JobManagerService.getInstance().registerJob(new SubscriptionNotifierJob(), 3, TimeUnit.HOURS); //FIXME
      //  JobManagerService.getInstance().registerJob(new CreateLinkPages(),20,20, TimeUnit.SECONDS);
           //FIXME Memory problem max maxFeedCount == 400? ALL
      //  JobManagerService.getInstance().registerJob(new PodCastCatalogUpdater(), 48, TimeUnit.HOURS); //FIXME
        JobManagerService.getInstance().registerJob(new MemoryDumperJob(), 1, TimeUnit.MINUTES); //FIXME change time, remove

        JobManagerService.getInstance().registerJob(new PodCastCatalogUpdater(),0,48,TimeUnit.MINUTES);

       /* PodCastCatalogMetaData podCastCatalogMetaData = null;
        for (PodCastCatalogLanguage language : PodCastCatalogLanguage.values()) {

            //TEST only 1
            if(language != PodCastCatalogLanguage.ES) {
                continue;
            }

            try {
                podCastCatalogMetaData = FtpOneClient.getInstance().load(language);
                LOG.info("Loaded metadata from one.com for lang=" + language);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Failed loading " + language, e);
            }

            if (podCastCatalogMetaData == null) {
                LOG.info("No podCastCatalogMetaData exist on one.com. Start building PodCastCatalog language=" + language);
                PodCastCatalogService.getInstance().buildPodCastCatalogsAsync(language.create());

            } else {
                LOG.info("Loaded podCastCatalogMetaData from one.com.");
                PodCastCatalogService.getInstance().register(language, podCastCatalogMetaData);
            }
        }*/

        JobManagerService.getInstance().startAsync();
    }

    private void setupPodCastSubscriptionService(ServletConfig servletConfig, ServiceDataStorage serviceDataStorageDisk) {
        try {

            File temp = new File(serviceDataStorageDisk.getPodDataHomeDir(), "temp");
            temp.mkdirs();
            String accountFile = servletConfig.getServletContext().getResource("/WEB-INF/pods-service.account.json").getFile();
            File file = new File(accountFile);
            PodCastSubscriptionService.getInstance().start(file);
        } catch (MalformedURLException e) {
            LOG.log(Level.SEVERE, "Failed loading pods-service.account.json", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }
}
