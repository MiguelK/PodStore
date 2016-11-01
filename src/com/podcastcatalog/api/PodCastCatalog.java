package com.podcastcatalog.api;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/podCastCatalog")
public class PodCastCatalog {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStation(@QueryParam("lang") String lang) {

        //LOG.fine("getStation() stationId =" + stationId);

       // StationDTO stationDTO = InMemoryCache.getInstance().getStationById(stationId);

        return Response.status(Response.Status.OK).entity("Hallo world").build();
    }

    @GET
    @Path("/stat")
    @Produces(MediaType.TEXT_HTML)
    public Response getStatistics() {


        return Response.status(Response.Status.OK).entity("podCastCatalog stattsitcs").build();
    }

}
