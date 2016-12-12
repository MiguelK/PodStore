package com.podcastcatalog.api;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcaststatus.PodCastStatus;
import com.podcastcatalog.model.subscription.Subscription;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.service.subscription.PodCastSubscriptionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Path("/subscriber")
public class Subscriber {

    @GET
    @Path("{deviceToken}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscriberPodCasts(@PathParam(value = "deviceToken") String deviceToken) {

        com.podcastcatalog.model.subscription.Subscriber subscriber;
        try {
            subscriber = PodCastSubscriptionService.getInstance().getSubscriber(deviceToken);
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to get subscriber deviceToken=" + deviceToken
                    + " " + e.getMessage()).build();
        }

        Map<String, PodCast> podCastById = new HashMap<>();

        for (Subscription subscription : subscriber.getSubscriptions()) {
            String podCastId = subscription.getContentId();
            Optional<PodCast> podCast = PodCastCatalogService.getInstance().
                    getPodCastById(podCastId);

            if (!podCast.isPresent()) {
                podCast = ItunesSearchAPI.lookup(podCastId);
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
    }

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

    /*@GET //FIXME Not used? remove
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
    }*/

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getStatus() {
        return Response.status(Response.Status.OK).entity(PodCastSubscriptionService.getInstance().getStatusAsHTLM()).build();
    }
}
