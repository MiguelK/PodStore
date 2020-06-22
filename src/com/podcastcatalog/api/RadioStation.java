package com.podcastcatalog.api;

import com.podcastcatalog.service.radio.RadioStationService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/radioStation")
public class RadioStation {


    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@QueryParam("query") String query,
                           @QueryParam("lang") String lang) {

        List<com.podcastcatalog.model.radio.RadioStation> radioStations = RadioStationService.INSTANCE.search(query);
        return Response.status(Response.Status.OK).entity(radioStations).build();
    }
}
