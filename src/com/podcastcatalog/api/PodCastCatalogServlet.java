package com.podcastcatalog.api;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.service.datastore.PodCastCatalogVersion;
import com.podcastcatalog.service.datastore.ServiceDataStorage;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 *
 */
public class PodCastCatalogServlet extends HttpServlet {

    private final static Logger LOG = Logger.getLogger(PodCastCatalogServlet.class.getName());



    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PodCastCatalogLanguage language = PodCastCatalogLanguage.fromString(request.getParameter("lang"));

        if (language == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing lang parameter? or invalid lang=" + request.getParameter("lang")
                    + " valid values=" + Arrays.toString(PodCastCatalogLanguage.values()));
            return;
        }

        ServiceDataStorage serviceDataStorageDisk = ServiceDataStorage.useDefault();

     /*   if (!serviceDataStorageDisk.getCurrentVersion().isPresent()) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "No catalog version loaded yet. Loading in progress?");
            return;
        }*/

     /*   com.podcastcatalog.model.podcastcatalog.PodCastCatalog podCastCatalog =
                PodCastCatalogService.getInstance().getPodCastCatalog(language);

        if (podCastCatalog == null) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "PodCastCatalog with lang " + language + " is not loaded yet?");
            return;
        }

        LOG.info("Writing podCastCatalog as JSON " + podCastCatalog);*/

        PodCastCatalogVersion podCastCatalogVersion = serviceDataStorageDisk.getCurrentVersion(language).orElseGet(null);
        if (podCastCatalogVersion == null) {
            String message = "No podCastCatalogVersion with language=" + language + " exists, Loading in progress?";
            LOG.info(message);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, message);
            return;
        }

        File zipFile = podCastCatalogVersion.getLangJSONZipped();//FIXME Only SE

        if (zipFile == null) {
            String message = "No zipFile with language=" + language + " exists, Loading in progress?";
            LOG.info(message);

            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, message);
            return;
        }

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipFile.getName());

        ServletOutputStream outputStream = response.getOutputStream();

        IOUtils.copy(new FileInputStream(zipFile), outputStream);

        outputStream.flush();
    }
}
