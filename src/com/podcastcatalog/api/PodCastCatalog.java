package com.podcastcatalog.api;


import com.podcastcatalog.model.podcastsearch.PodCastInfo;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastsearch.ResultItem;
import com.podcastcatalog.model.podcastsearch.SearchResult;
import com.podcastcatalog.service.search.SearchTerm;
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

    @GET
    @Path("/heartBeat")
    public Response heartBeat() {
        return Response.status(Response.Status.OK).build();
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

        List<ResultItem> podCastEpisodes = PodCastCatalogService.getInstance().search(podCastCatalogLanguage, queryParam);

        SearchResult searchResult = new SearchResult(podCastEpisodes);

        return Response.status(Response.Status.OK).entity(searchResult).build();
    }

    @GET
    @Path("/podCastTitles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPodCastTitles(@QueryParam("lang") String lang) {

        PodCastCatalogLanguage podCastCatalogLanguage = PodCastCatalogLanguage.fromString(lang);
        if (podCastCatalogLanguage == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid lang parameter " + lang).build();
        }

        List<PodCastInfo> podCastTitles = PodCastCatalogService.getInstance().getPodCastTitles(podCastCatalogLanguage);

        return Response.status(Response.Status.OK).entity(podCastTitles).build();
    }

    @GET
    @Path("/podCastTitlesTrending")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPodCastTitlesTrending(@QueryParam("lang") String lang) {

        PodCastCatalogLanguage podCastCatalogLanguage = PodCastCatalogLanguage.fromString(lang);
        if (podCastCatalogLanguage == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid lang parameter " + lang).build();
        }

        List<PodCastInfo> podCastTitlesTrending =  PodCastCatalogService.getInstance().getPodCastTitlesTrending(podCastCatalogLanguage);

        return Response.status(Response.Status.OK).entity(podCastTitlesTrending).build();
    }

    //Deprecated NOT USED REMOVE FIXME
    @GET
    @Path("/popularSearchTerms")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPopularSearchTerms(@QueryParam("lang") String lang) {

        PodCastCatalogLanguage podCastCatalogLanguage = PodCastCatalogLanguage.fromString(lang);
        if (podCastCatalogLanguage == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid lang parameter " + lang).build();
        }


        List<SearchTerm> popularSearchTerms = PodCastCatalogService.getInstance().getPopularSearchTerms(podCastCatalogLanguage);

        return Response.status(Response.Status.OK).entity(popularSearchTerms).build();
    }
}
