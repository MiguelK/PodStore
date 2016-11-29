package com.podcastcatalog.api.subscription;

import com.podcastcatalog.subscribe.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/podCastSubscription")
public class PodCastSubscription {

    @POST
    @Path("{deviceToken}/{contentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribe(@PathParam("deviceToken") String deviceToken,
                              @PathParam("contentId") String contentId) {

        try {
            PodCastSubscriptions.getInstance().subscribe(deviceToken, contentId, contentId1 -> {
                return true; //FIXME check in ProdCat collectionId exists
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
            PodCastSubscriptions.getInstance().unSubscribe(deviceToken, contentId);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to unSubscribe " + e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getStatus() {
        return Response.status(Response.Status.OK).entity(PodCastSubscriptions.getInstance().getStatusAsHTLM()).build();
    }
}
