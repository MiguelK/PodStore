package com.podcastcatalog;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.builder.PodCastCatalogBuilderSE;
import com.podcastcatalog.store.DataStorage;

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

        DataStorage dataStorage = new DataStorage();

        LOG.info("Starting PodCastCatalog..., working dir= " + dataStorage);

        PodCastCatalogService.getInstance().setStorage(dataStorage);
        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilderSE());//FIXME English

        Optional<PodCastCatalog> podCastCatalog = dataStorage.load(PodCastCatalogLanguage.Sweden);
        if (podCastCatalog.isPresent()) {
            PodCastCatalogService.getInstance().loadPodCastCatalog(podCastCatalog.get());
        } else {
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
