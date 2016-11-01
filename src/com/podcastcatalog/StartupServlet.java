package com.podcastcatalog;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class StartupServlet extends HttpServlet {

    private final static Logger LOG = Logger.getLogger(StartupServlet.class.getName());

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        LOG.info("Starting PodCastCatalog...");

        PodCastCatalogService.getInstance().startAsync();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }
}
