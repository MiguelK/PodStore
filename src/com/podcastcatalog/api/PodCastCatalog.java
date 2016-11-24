package com.podcastcatalog.api;


import com.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.api.response.search.ResultItem;
import com.podcastcatalog.api.response.search.SearchResult;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Path("/podCastCatalog")
public class PodCastCatalog {

    private final static Logger LOG = Logger.getLogger(PodCastCatalog.class.getName());

    //FIXME Not used?
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPodCastCatalog(@QueryParam("lang") String lang) {

        PodCastCatalogLanguage podCastCatalogLanguage = PodCastCatalogLanguage.fromString(lang);
        if (podCastCatalogLanguage == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid lang parameter " + lang).build();
        }

        com.podcastcatalog.api.response.PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(podCastCatalogLanguage);

        if (podCastCatalog == null) {
            LOG.info("podCastCatalog for lang " + lang + " is not loaded yet?");
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Not ready yet").build();
        }

        return Response.status(Response.Status.OK).entity(podCastCatalog).build();
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@QueryParam("query") String query,
                           @QueryParam("lang") String lang) {

        PodCastCatalogLanguage podCastCatalogLanguage = PodCastCatalogLanguage.fromString(lang);
        if (podCastCatalogLanguage == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid lang parameter " + lang).build();
        }

        String queryParam = StringUtils.trimToNull(query);
        if (queryParam == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid query parameter " + query).build();
        }

//        com.podcastcatalog.api.response.PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(podCastCatalogLanguage);
//
//        if (podCastCatalog == null) {
//            LOG.info("podCastCatalog for lang " + lang + " is not loaded yet?");
//            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Not ready yet").build();
//        }


        //FIXME should uncahed PodCats be fetched??
        List<ResultItem> podCastEpisodes = PodCastCatalogService.getInstance().searchEpisodes(queryParam);

        SearchResult searchResult = new SearchResult(podCastEpisodes);

        return Response.status(Response.Status.OK).entity(searchResult).build();
    }
}
