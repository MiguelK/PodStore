package com.podcastcatalog.api;

import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.service.podcaststar.PodCastStarService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/podCast")
public class PodCast {

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPodCastByCollectionId(@PathParam("id") String id) {

        Optional<com.podcastcatalog.model.podcastcatalog.PodCast> podCast = PodCastCatalogService.getInstance().getPodCastById(id);

        if (!podCast.isPresent()) {
            podCast = ItunesSearchAPI.lookup(id);
            //FIXME if hit fetct and and to in-memory index?
        }

        if (!podCast.isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No podcast with id= " + id + " exist").build();
        }

        return Response.status(Response.Status.OK).entity(podCast.get()).build();
    }

    @POST
    @Path("/star/{id}/{episodeId}/{stars}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response starPodCast(@PathParam("id") String id,
                                @PathParam("episodeId") String episodeId, @PathParam("stars") int stars) {

        try {
            PodCastStarService.getInstance().starPodCast(id, episodeId, stars);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to star podCastEpisode " + e.getMessage()).build();
        }


        return Response.status(Response.Status.OK).build();
    }
}
