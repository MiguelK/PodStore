package com.podcastcatalog.api;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.service.appstatistic.AppStatisticData;
import com.podcastcatalog.service.appstatistic.AppStatisticService;
import com.podcastcatalog.util.ServerInfo;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/appStatistic")
public class AppStatistic {

    @GET
    @Path("/{lang}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAppStatistic(@PathParam("lang") String lang) {

        PodCastCatalogLanguage podCastCatalogLanguage = PodCastCatalogLanguage.fromString(lang);
        if (podCastCatalogLanguage == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid lang parameter " + lang).build();
        }

        AppStatisticData appStatisticData = AppStatisticService.getInstance().getAppStatisticData(podCastCatalogLanguage);

        return Response.status(Response.Status.OK).entity(appStatisticData).build();
    }

    @POST
    @Path("/add/{pid}/{eid}/{lang}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUserEngage(@PathParam("pid") String pid,
                                  @PathParam("eid") String eid,
                                  @PathParam("lang") String lang) {

        PodCastCatalogLanguage podCastCatalogLanguage = PodCastCatalogLanguage.fromString(lang);
        if (podCastCatalogLanguage == null || StringUtils.isEmpty(pid) || StringUtils.isEmpty(eid) ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid lang parameter " + lang).build();
        }

        try {
            AppStatisticService.getInstance().addUserEngaged(pid, eid,
                    podCastCatalogLanguage);

               if(ServerInfo.isLocalDevMode()) {
                    AppStatisticService.getInstance().uploadToOne();
                }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Failed to star podCastEpisode " + e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).build();
    }
}
