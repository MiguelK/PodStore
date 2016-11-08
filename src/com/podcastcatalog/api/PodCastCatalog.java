package com.podcastcatalog.api;


import com.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.api.util.StringFormatter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/podCastCatalog")
public class PodCastCatalog {

    private final static Logger LOG = Logger.getLogger(PodCastCatalog.class.getName());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStation(@QueryParam("lang") String lang) {

        com.podcastcatalog.api.response.PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);

        if(podCastCatalog==null){
            LOG.info("podCastCatalog for lang " + lang + " is not loaded yet?");
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Not ready yet").build();
        }

        return Response.status(Response.Status.OK).entity(podCastCatalog).build();
    }

    @GET
    @Path("/stat")
    @Produces(MediaType.TEXT_HTML)
    public Response getStatistics(@QueryParam("lang") String lang) {

//        PodCastCatalogService.getInstance().rebuildCatalog(...)//Action
        com.podcastcatalog.api.response.PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);

        if(podCastCatalog==null){
            return Response.status(Response.Status.OK).entity("No statistics exist for " + lang + " yet.?").build();
        }

        StringFormatter stringFormatter = StringFormatter.create(podCastCatalog);

        return Response.status(Response.Status.OK).entity(stringFormatter.format()).build();
    }
}
