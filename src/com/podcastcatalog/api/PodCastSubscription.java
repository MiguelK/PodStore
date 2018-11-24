package com.podcastcatalog.api;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.service.job.SubscriptionNotifierJob;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.service.subscription.PodCastSubscriptionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.logging.Logger;

//FIXME Remove, not used. version 1?
@Path("/podCastSubscription")
public class PodCastSubscription {

    private final static Logger LOG = Logger.getLogger(PodCastSubscription.class.getName());

    @POST
    @Path("{deviceToken}/{podCastId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribe(@PathParam("deviceToken") String deviceToken,
                              @PathParam("podCastId") String podCastId) {

        LOG.info("subscribe= " + deviceToken + ", podCastId=" + podCastId);

        PodCastSubscriptionService.getInstance().subscribe(deviceToken, podCastId);

        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("{deviceToken}/{podCastId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unSubscribe(@PathParam("deviceToken") String deviceToken,
                                @PathParam("podCastId") String podCastId) {


        LOG.info("unSubscribe= " + deviceToken + ", podCastId=" + podCastId);

        PodCastSubscriptionService.getInstance().unSubscribe(deviceToken, podCastId);

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getStatus() {
        return Response.status(Response.Status.OK).entity(PodCastSubscriptionService.getInstance().getStatusAsHTLM()).build();
    }
}
