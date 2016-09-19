package com.podcastcatalog.api;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//FIXME rest API
@Path("/api")
public class PodCastCatalog {


    @GET
    @Path("{stationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStation(@PathParam("stationId") String stationId) {

        //LOG.fine("getStation() stationId =" + stationId);

       // StationDTO stationDTO = InMemoryCache.getInstance().getStationById(stationId);

        return Response.status(Response.Status.OK).entity("").build();
    }

}
