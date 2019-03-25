package com.podcastcatalog;

import com.podcastcatalog.service.datastore.LocatorProduction;
import com.podcastcatalog.service.job.JobManagerService;
import com.podcastcatalog.service.job.MemoryDumperJob;
import com.podcastcatalog.service.job.PodCastCatalogUpdater;
import com.podcastcatalog.service.job.SubscriptionNotifierJob;
import com.podcastcatalog.service.subscription.PodCastSubscriptionService;

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
 * Features of PodStore-Server: (Same behavior in dev and prod except LinkPage creator)
 * 1. Create new catalog for every lang each xxx hour
 * 2. Upload lang zip files to one.com. e.g DE_json.zip (fetched by App client)
 * 3. Upload lang podCast/podCastEpisode text search index to one.com.  e.g DE_MetaData.dat (fetched by PodStore-Server at startup)
 * 4. Only search indexes, trending, suggestions is existing in-memory (xx_MetaData.dat) + Subscription/Subscribers
 * <p>
 * Server at startup:
 * Try load all lang search indexes into memory from one.com. e.g (DE_MetaData.dat)
 * if index does not exists start (building ProductCatalog+metadata process#1)
 * <p>
 * Lang data stored on one.com:
 * TextSearchIndex + podCastTitles + podCastTitlesTrending + popularSearchQueries
 * <p>
 * <p>
 * #1 (building process)
 * 1. Create PodCastcatalog, export to one.com
 * 2 Index catalog, export PodCastServer to one.com
 */
public class StartupServlet extends HttpServlet {

    private final static Logger LOG = Logger.getLogger(StartupServlet.class.getName());

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);


        LOG.info("Starting PodStore POD_HOME_DIR=" + LocatorProduction.getInstance().getPodDataHomeDirectory().getAbsolutePath());

        setupPodCastSubscriptionService(servletConfig);

        //Important for FeedParser, could cause 403 otherwise.
        System.setProperty("http.agent", "Chrome");

        LOG.info("Starting PodCastCatalog..., working dir= " + LocatorProduction.getInstance().getPodDataHomeDirectory().getAbsolutePath());

        JobManagerService.getInstance().registerJob(new SubscriptionNotifierJob(), 0,12, TimeUnit.HOURS);
        //  JobManagerService.getInstance().registerJob(new CreateLinkPages(),20,20, TimeUnit.SECONDS);
        JobManagerService.getInstance().registerJob(new MemoryDumperJob(), 0,8, TimeUnit.HOURS); //FIXME change time, remove
        JobManagerService.getInstance().registerJob(new PodCastCatalogUpdater(), 0, 48, TimeUnit.HOURS); //FIXME

        JobManagerService.getInstance().startAsync();
    }

    private void setupPodCastSubscriptionService(ServletConfig servletConfig) {
        try {

            File temp = new File(LocatorProduction.getInstance().getPodDataHomeDirectory(), "temp");
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
