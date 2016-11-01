package com.podcastcatalog;

import com.podcastcatalog.builder.PodCastCatalogBuilderSE;
import com.podcastcatalog.store.DiscStorage;
import com.podcastcatalog.store.Storage;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class StartupServlet extends HttpServlet {

    private final static Logger LOG = Logger.getLogger(StartupServlet.class.getName());

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        File catalogDir = new File("/home/krantmig/tools/temp");//FIXME
        LOG.info("Starting PodCastCatalog..., working dir= " + catalogDir.getAbsolutePath());

        Storage discStorage = new DiscStorage(catalogDir);
        PodCastCatalogService.getInstance().setStorage(discStorage);
        PodCastCatalogService.getInstance().registerPodCastCatalogBuilder(new PodCastCatalogBuilderSE());//FIXME English

        //FIXME validate
        PodCastCatalogService.getInstance().startAsync();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }
}
