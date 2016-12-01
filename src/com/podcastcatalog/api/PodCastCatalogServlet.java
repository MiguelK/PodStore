package com.podcastcatalog.api;

import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.storage.DataStorage;
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

        if(language==null){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Missing lang parameter? or invalid lang=" + request.getParameter("lang")
                    + " valid values=" + Arrays.toString(PodCastCatalogLanguage.values()));
            return;
        }

        DataStorage dataStorage = new DataStorage();

        if(!dataStorage.getCurrentVersion().isPresent()){
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,"No catalog version loaded yet. Loading in progress?");
            return;
        }

        com.podcastcatalog.model.podcastcatalog.PodCastCatalog podCastCatalog =
                PodCastCatalogService.getInstance().getPodCastCatalog(language);

        LOG.info("Writing podCastCatalog as JSON " + podCastCatalog);

        File zipFile = dataStorage.getCurrentVersion().orElseGet(null).getSweJSONZipped();//FIXME Only SWE

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipFile.getName());

        ServletOutputStream outputStream = response.getOutputStream();

        IOUtils.copy(new FileInputStream(zipFile),outputStream);

        outputStream.flush();
    }
}
