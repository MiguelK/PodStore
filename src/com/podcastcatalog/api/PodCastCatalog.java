package com.podcastcatalog.api;


import com.podcastcatalog.model.podcastsearch.PodCastTitle;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastsearch.ResultItem;
import com.podcastcatalog.model.podcastsearch.SearchResult;
import com.podcastcatalog.service.search.SearchSuggestionService;
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

    //FIXME Not used?
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPodCastCatalog(@QueryParam("lang") String lang) {

        PodCastCatalogLanguage podCastCatalogLanguage = PodCastCatalogLanguage.fromString(lang);
        if (podCastCatalogLanguage == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid lang parameter " + lang).build();
        }

        com.podcastcatalog.model.podcastcatalog.PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(podCastCatalogLanguage);

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

        List<ResultItem> podCastEpisodes = PodCastCatalogService.getInstance().search(podCastCatalogLanguage, queryParam);


        if(!podCastEpisodes.isEmpty() && query.length() > 5){
            LOG.info("Search=" + query + ", lang=" + lang + ", result=" + podCastEpisodes.size());

            SearchSuggestionService.getInstance().addPopularSearchTerm(podCastCatalogLanguage, query);
        }


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

        List<PodCastTitle> podCastTitles = SearchSuggestionService.getInstance().getPodCastTitles(podCastCatalogLanguage);

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

        List<PodCastTitle> podCastTitlesTrending = SearchSuggestionService.getInstance().getPodCastTitlesTrending(podCastCatalogLanguage);

        return Response.status(Response.Status.OK).entity(podCastTitlesTrending).build();
    }

    @GET
    @Path("/popularSearchTerms")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPopularSearchTerms(@QueryParam("lang") String lang) {

        PodCastCatalogLanguage podCastCatalogLanguage = PodCastCatalogLanguage.fromString(lang);
        if (podCastCatalogLanguage == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid lang parameter " + lang).build();
        }

        List<SearchTerm> popularSearchTerms = SearchSuggestionService.getInstance().getPopularSearchTerm(podCastCatalogLanguage);

        return Response.status(Response.Status.OK).entity(popularSearchTerms).build();
    }
}
