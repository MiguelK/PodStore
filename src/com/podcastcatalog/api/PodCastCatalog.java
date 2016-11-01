package com.podcastcatalog.api;


import com.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;

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

        //LOG.fine("getStation() stationId =" + stationId);

       // StationDTO stationDTO = InMemoryCache.getInstance().getStationById(stationId);

        com.podcastcatalog.api.response.PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);

        if(podCastCatalog==null){
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Not ready yes").build();
        }
        //if podCastCatalog null?
        LOG.info("podCastCatalog=" + podCastCatalog);


        return Response.status(Response.Status.OK).entity(podCastCatalog).build();
    }

    @GET
    @Path("/stat")
    @Produces(MediaType.TEXT_HTML)
    public Response getStatistics() {

//        PodCastCatalogService.getInstance().rebuildCatalog(...)
        com.podcastcatalog.api.response.PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);


        if(podCastCatalog==null){
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Not ready yes").build();
        }

        return Response.status(Response.Status.OK).entity("podCastCatalog stattsitcs " + podCastCatalog.getBundles().size()).build();
    }

}
