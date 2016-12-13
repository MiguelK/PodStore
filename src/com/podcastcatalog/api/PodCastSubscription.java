package com.podcastcatalog.api;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.service.subscription.PodCastSubscriptionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

//FIXME Remove, not used. version 1?
@Path("/podCastSubscription")
public class PodCastSubscription {

    @POST
    @Path("{deviceToken}/{contentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribe(@PathParam("deviceToken") String deviceToken,
                              @PathParam("contentId") String contentId) {

        try {
            PodCastSubscriptionService.getInstance().subscribe(deviceToken, contentId, contentId1 -> {
                Optional<PodCast> podCast = PodCastCatalogService.getInstance().getPodCastById(contentId);
                return podCast.isPresent();
            });
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to subscribe " + e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{deviceToken}/{contentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unSubscribe(@PathParam("deviceToken") String deviceToken,
                                @PathParam("contentId") String contentId) {

        try {
            PodCastSubscriptionService.getInstance().unSubscribe(deviceToken, contentId);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to unSubscribe " + e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getStatus() {
        return Response.status(Response.Status.OK).entity(PodCastSubscriptionService.getInstance().getStatusAsHTLM()).build();
    }
}
