package com.podcastcatalog.api.subscription;

import com.podcastcatalog.subscribe.PodCastSubscriptions;

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
            PodCastSubscriptions.registerSubscriber(deviceToken);
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
            PodCastSubscriptions.deleteSubscriber(deviceToken);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to delete Subscriber " + e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getStatus() {
        return Response.status(Response.Status.OK).entity("<html><body>FIXME: Subscriber status</body></html>").build();
    }
}
