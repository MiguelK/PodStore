package com.podcastcatalog.api;

import com.podcastcatalog.service.subscription.PodCastSubscriptionService;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/podCastSubscription")
public class PodCastSubscription {

    private final static Logger LOG = Logger.getLogger(PodCastSubscription.class.getName());

    @POST
    @Path("{deviceToken}/{podCastId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribe(@PathParam("deviceToken") String deviceToken,
                              @PathParam("podCastId") String podCastId) {

        PodCastSubscriptionService.getInstance().subscribe(deviceToken, podCastId);

        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{deviceToken}/{podCastId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unSubscribe(@PathParam("deviceToken") String deviceToken,
                                @PathParam("podCastId") String podCastId) {

        PodCastSubscriptionService.getInstance().unSubscribe(deviceToken, podCastId);

        return Response.status(Response.Status.OK).build();
    }
}
