package com.podcastcatalog.api;

import com.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.store.DataStorage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

//Zipped JSON?
public class PodCastCatalogServlet extends HttpServlet {

    private final static Logger LOG = Logger.getLogger(PodCastCatalogServlet.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String lang = StringUtils.trimToNull(request.getParameter("lang"));

        if(lang==null){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Missing lang parameter");
            return;
        }


        DataStorage dataStorage = new DataStorage();

        com.podcastcatalog.api.response.PodCastCatalog podCastCatalog =
                PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);

        LOG.info("Writing podCastCatalog as JSON " + podCastCatalog);

        File zipFile = dataStorage.get();

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipFile.getName());

        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.copy(new FileInputStream(zipFile),outputStream);

        outputStream.flush();//Needed?
    }
}
