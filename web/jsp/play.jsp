<%@ page import="com.podcastcatalog.service.podcastcatalog.PodCastCatalogService" %>
<%@ page import="com.podcastcatalog.model.podcastcatalog.PodCast" %>
<%@ page import="java.util.Optional" %>
<%@ page import="com.podcastcatalog.model.podcastcatalog.PodCastEpisode" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="com.podcastcatalog.util.UAgentInfo" %>
<%@ page import="com.podcastcatalog.util.PodCastURLParser" %>
<%@ page import="com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI" %>
<%@ page import="javax.ws.rs.core.Response" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<%
    PodCastURLParser.Parameters podCastRequestParameters = PodCastURLParser.parsePodCastEpisodeId(request.getQueryString());

    String podCastId = null;
    String podCastEpisodeId = null;

    if(podCastRequestParameters != null) {
        podCastId = podCastRequestParameters.getPodCastId();
        podCastEpisodeId = podCastRequestParameters.getPodCastEpisodeId();
    }

    String podCastImageURL = "http://www.pods.one/site/images/default-share-image.png";
    String podCastTitle = "PodCast on Pods";
    String podCastEpisodeTitle = "";

    if(podCastId!= null){

        Optional<PodCast> podCastInMemory = PodCastCatalogService.getInstance().getPodCastById(podCastId);

        PodCast podCast = null;
        if(podCastInMemory.isPresent()){
            podCast = podCastInMemory.get();
        } else {

            Optional<com.podcastcatalog.model.podcastcatalog.PodCast>  podCastNotInMemory = ItunesSearchAPI.lookupPodCast(podCastId);
            if (podCastNotInMemory.isPresent()) {
                podCast = podCastNotInMemory.get();
            }
        }

        if(podCast!= null){
            podCastImageURL = podCast.getArtworkUrl600();
            podCastTitle = podCast.getTitle() + " on Pods";
        }

        if(podCast!= null && podCastEpisodeId!= null){
            for (PodCastEpisode podCastEpisode : podCast.getPodCastEpisodesInternal()) {
                if(podCastEpisode.getId().equals(podCastEpisodeId)){
                    podCastEpisodeTitle = podCastEpisode.getTitle();
                    break;
                }
            }
        }
    }

    String parameters = "";
    if(podCastId!=null && podCastId.length()>0) {
        parameters = "?pid=" + podCastTitle;
    }

    if(podCastEpisodeTitle!=null && podCastEpisodeTitle.length()>0){
        parameters+= "&eid=" + podCastEpisodeTitle;
    }
    if(podCastImageURL!=null && podCastImageURL.length()>0){
        parameters+= "&podCastImage=" + podCastImageURL;
    }

    String soundId = request.getParameter("sid");

    if(soundId!=null && soundId.length()>0){
        parameters+= "&sid=" + soundId;
    }


    //if(soundId != null) {
        //FIXME Property
    //    String redirectURL = "http://resourceservice-itemstore.rhcloud.com/ResourceService/stream/" + soundId;
    //  response.sendRedirect(redirectURL);
    //} else {
        String redirectURL = "http://www.pods.one" + parameters; //?pid=" + podCastId + "&eid=" + podCastEpisodeTitle + "&podCastImage=" + podCastImageURL;
        response.sendRedirect(redirectURL);
    //}
%>

<body style="background-color : rgb(37,40,45);">

</body>
</html>
