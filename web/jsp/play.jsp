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

    String redirectURL = "http://www.pods.one" + parameters; //?pid=" + podCastId + "&eid=" + podCastEpisodeTitle + "&podCastImage=" + podCastImageURL;
    response.sendRedirect(redirectURL);

%>

<head>
<title>Pods @2017</title>

<!-- Apple smart banner -->
<meta name="apple-itunes-app" content="app-id=1209200428", app-argument=http://www.pods.one">

<!-- Schema.org markup for Google+ -->
<meta itemprop="name" content="<%=podCastTitle%>">
<meta itemprop="description" content="<%=podCastEpisodeTitle%>">
<meta itemprop="image" content="<%=podCastImageURL%>">

<!-- Facebook -->
<meta property="og:title" content="<%=podCastTitle%>"/>
<meta property="og:description"
      content="<%=podCastEpisodeTitle%>"/>
<meta property="og:type" content="article"/>
<meta property="og:image" content="<%=podCastImageURL%>"/>

<!-- Twitter -->
<meta name="twitter:site" content="@Pods">
<meta name="twitter:title" content="<%=podCastTitle%>">
<meta name="twitter:description" content="<%=podCastEpisodeTitle%>">
<meta name="twitter:image" content="<%=podCastImageURL%>">
<meta name="twitter:card" content="summary_large_image">

<%--
<meta name="twitter:image" content="<%=shareImageURL%>"/>
<meta name="twitter:image:src" content="<%=shareImageURL%>">
<meta name="twitter:title" content="<%=podCastTitle%>"/>
<meta name="twitter:creator" content="Pods Inc">
<meta name="twitter:description"
          content="<%=podCastEpisodeTitle%>"/>
<meta name="twitter:app:country" content="SE">
<meta name="twitter:app:name:iphone" content="Pods">
<meta name="twitter:app:id:iphone" content="1209200428">
<meta name="twitter:app:url:iphone" content="https://itunes.apple.com/se/app/pods-podcast-player/id1209200428?mt=8">
<meta name="twitter:app:name:ipad" content="Pods">
<meta name="twitter:app:id:ipad" content="1209200428">
<meta name="twitter:app:url:ipad" content="https://itunes.apple.com/se/app/pods-podcast-player/id1209200428?mt=8">
--%>

    <!-- Lato font -->
<link href='https://fonts.googleapis.com/css?family=Lato:300,400' rel='stylesheet' type='text/css'>

<style>
    table {
        width: 100%;
    }

    table, th, td {
        border: 1px solid rgb(92,161,192);
        border-collapse: collapse;
    }

    th, td {
        padding: 5px;
        text-align: left;
    }

    table#t01 tr:nth-child(even) {
        background-color: #eee;
    }

    table#t01 tr:nth-child(odd) {
        background-color: #fff;
    }

    table#t01 th {
        background-color: rgb(92,161,192);
        color: white;
    }

    h2 {
        font-family: 'Lato', sans-serif;
        color: white;
        font-size: 300%;
    }
    h3 {
        color: white;
        font-size: 200%;
    }

    p {
        color: #4c6b87;
    }

</style>
</head>

<body style="background-color : rgb(37,40,45);">


<%--
Pods: The PodCast Player<br>
<br>
<p>ShareImageURL:<%=shareImageURL%>
<br>
PodCastTitle:<%=podCastTitle%>
<br>
PodCastEpisodeTitle:<%=podCastEpisodeTitle%>
<br>
    isMobileDevice <%=isMobileDevice%>
 <br>
    userAgent = <%=userAgent%>
    <br>
    agentInfo = <%=agentInfo%>

<%=request.getRequestURL()%>
    <br>
    <%
        StringBuilder keyValues = new StringBuilder();
        if(request.getParameterMap()!=null) {
            for (String s : request.getParameterMap().keySet()) {
                keyValues.append("Key=").append(s).append(" : Value=").append(Arrays.toString(request.getParameterMap().get(s))).append("<br>");
            }
        }
    %>
    <%=keyValues%>
</p>
--%>
</body>
</html>
