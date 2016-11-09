package com.podcastcatalog;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.builder.PodCastCatalogBuilderSE;
import com.podcastcatalog.store.DiscStorage;
import com.podcastcatalog.store.HomeDirectoryLocator;
import com.podcastcatalog.store.Storage;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

public class StartupServlet extends HttpServlet {

    private final static Logger LOG = Logger.getLogger(StartupServlet.class.getName());

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        HomeDirectoryLocator locator = new HomeDirectoryLocator();

        File catalogDir = locator.locateDataDir();

        LOG.info("Starting PodCastCatalog..., working dir= " + catalogDir.getAbsolutePath());

        Storage discStorage = new DiscStorage(catalogDir);
        PodCastCatalogService.getInstance().setStorage(discStorage);
        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilderSE());//FIXME English

        Optional<PodCastCatalog> podCastCatalog = discStorage.load(PodCastCatalogLanguage.Sweden);
        if (podCastCatalog.isPresent()) {
            PodCastCatalogService.getInstance().loadPodCastCatalog(podCastCatalog.get());
        } else {
            PodCastCatalogService.getInstance().buildPodCastCatalogsAsync();
        }
        //        registerOrStartLoading(discStorage.load(PodCastCatalogLanguage.Sweden)); //FIXME English

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }
}
