package com.podcastcatalog.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/podCast")
public class PodCast {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPodCastByCollectionId(@QueryParam("podCastCollectionId") String podCastCollectionId) {


        //Look in-memory
        //no hit search Itunes
      /*  PodCastCatalogLanguage podCastCatalogLanguage = PodCastCatalogLanguage.fromString(lang);
        if (podCastCatalogLanguage == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid lang parameter " + lang).build();
        }*/

       /* com.podcastcatalog.api.response.PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(podCastCatalogLanguage);

        if (podCastCatalog == null) {
//            LOG.info("podCastCatalog for lang " + lang + " is not loaded yet?");
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Not ready yet").build();
        }*/

        return Response.status(Response.Status.OK).entity("ge by id ").build();
    }
}
