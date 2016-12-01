package com.podcastcatalog.api;

import com.podcastcatalog.service.subscription.PodCastSubscriptionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/subscriber")
public class Subscriber {

    @POST
    @Path("{deviceToken}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSubscriber(@PathParam("deviceToken") String deviceToken) {
        try {
            PodCastSubscriptionService.getInstance().registerSubscriber(deviceToken);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to register new Subscriber " + e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{deviceToken}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSubscriber(@PathParam("deviceToken") String deviceToken) {
        try {
            PodCastSubscriptionService.getInstance().deleteSubscriber(deviceToken);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to delete Subscriber " + e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("{deviceToken}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscriber(@PathParam("deviceToken") String deviceToken) {

        com.podcastcatalog.model.subscription.Subscriber subscriber;
        try {
            subscriber = PodCastSubscriptionService.getInstance().getSubscriber(deviceToken);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to get Subscriber " + e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).entity(subscriber).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getStatus() {
        return Response.status(Response.Status.OK).entity(PodCastSubscriptionService.getInstance().getStatusAsHTLM()).build();
    }
}
