package com.podcastcatalog.api;

import com.podcastcatalog.model.podcaststatus.PodCastStatus;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.service.podcaststar.PodCastStarService;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/podCast")
public class PodCast {

    /**
     * Used when user clicks on a search result row.
     * Downloaded and cached locally.
     *
     *
     * @param id PodCast id (e.g podcastCollectionId)
     * @return PodCast with episodes etc.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPodCastById(@PathParam("id") String id) {

        Optional<com.podcastcatalog.model.podcastcatalog.PodCast> podCastInMemory = PodCastCatalogService.getInstance().getPodCastById(id);

        if(podCastInMemory.isPresent()){
            com.podcastcatalog.model.podcastcatalog.PodCast withAllEpisodes =
                    com.podcastcatalog.model.podcastcatalog.PodCast.createWithAllEpisodes(podCastInMemory.get());

            return Response.status(Response.Status.OK).entity(withAllEpisodes).build();
        }

        Optional<com.podcastcatalog.model.podcastcatalog.PodCast>  podCastNotInMemory = ItunesSearchAPI.lookupPodCast(id);
        if (!podCastNotInMemory.isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No podcast with id= " + id + " exist in Itunes ").build();
        }

        PodCastCatalogService.getInstance().updatePodCastIndex(podCastNotInMemory.get());

        return Response.status(Response.Status.OK).entity(podCastNotInMemory.get()).build();
    }

    /**
     * Called from playList to see if any locally stored podCast has newer episodes.
     *
     * @param id One or many podCastIds e.g (123,455,33 comma seperated)
     * @return Map of latest episodes, and all located podCasts.
     */
    @GET
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
                podCast = ItunesSearchAPI.lookupPodCast(id);
            }

            if (!podCast.isPresent()) {
                //FIXME lOG? invalid id received?
                continue;
            }

            PodCastCatalogService.getInstance().updatePodCastIndex(podCast.get());

            podCastById.put(podCast.get().getCollectionId(), podCast.get());
        }

        PodCastStatus podCastStatus = new PodCastStatus(podCastById);

        return Response.status(Response.Status.OK).entity(podCastStatus).build();
    }


    //FIXME Not used
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
