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
    public Response getPodCastById(@PathParam("id") String id) {

        Optional<com.podcastcatalog.model.podcastcatalog.PodCast> podCast = PodCastCatalogService.getInstance().getPodCastById(id);

        if (!podCast.isPresent()) {
            podCast = ItunesSearchAPI.lookup(id);
            //FIXME if hit fetch and to in-memory index?
        }

        if (!podCast.isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No podcast with id= " + id + " exist").build();
        }

        return Response.status(Response.Status.OK).entity(podCast.get()).build();
    }

   /* @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPodCastByIds(@QueryParam(value = "id") String id) {

        if (StringUtils.isEmpty(id)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing id= " + id).build();
        }

        Map<String, com.podcastcatalog.model.podcastcatalog.PodCast> podCastById = new HashMap<>();

        List<String> podCastIds = Arrays.asList(StringUtils.split(StringUtils.trimToEmpty(id), ","));

        for (String podCastId : podCastIds) {
            Optional<com.podcastcatalog.model.podcastcatalog.PodCast> podCast = PodCastCatalogService.getInstance().getPodCastById(podCastId);

            if (!podCast.isPresent()) {
                podCast = ItunesSearchAPI.lookup(id);
                //FIXME if hit fetch and and to in-memory index? performance
            }

            if (!podCast.isPresent()) {
                //FIXME lOG? invalid id received?
                continue;
            }

            podCastById.put(podCast.get().getCollectionId(), podCast.get());
        }

        PodCastStatus podCastStatus = new PodCastStatus(podCastById);

        return Response.status(Response.Status.OK).entity(podCastStatus).build();
    }*/


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
