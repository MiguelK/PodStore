<%@ page import="com.podcastcatalog.service.podcastcatalog.PodCastCatalogService" %>
<%@ page import="com.podcastcatalog.model.podcastcatalog.PodCast" %>
<%@ page import="java.util.Optional" %>
<%@ page import="com.podcastcatalog.model.podcastcatalog.PodCastEpisode" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Arrays" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<%
    String podCastId = request.getParameter("pid");
    String podCastEpisodeId = request.getParameter("eid");

    String queryString = URLDecoder.decode(request.getQueryString(), "UTF-8");

    if(queryString!=null){

        String[] split = queryString.split("&");

        if(split.length>2){ //Always > 2 pis and optional eid plus isi
            String[] pidKeyValue= split[0].split("=");

            if(podCastId== null && pidKeyValue!=null && pidKeyValue.length==2 &&
                    pidKeyValue[0].equals("pid")){
                //pid = 302534426%26
                podCastId = pidKeyValue[1];
            }

            //-1f2bfc50-64485d5b
            String[] eidKeyValue = split[1].split("=");

            if(podCastEpisodeId!=null && eidKeyValue!= null && eidKeyValue.length==2 &&
                    eidKeyValue[0].equals("eid")){
                podCastEpisodeId = eidKeyValue[1];
            }
        }

/*
        int pidStart = queryString.indexOf("pid");
        int endIndex = queryString.lastIndexOf("isi")-1; //Exclude &
        if(pidStart!=-1 && endIndex<= queryString.length()){
            String linkParams = queryString.substring(pidStart, endIndex);
            System.out.println("sdsds" + linkParams);
        } */
    }

    if(podCastId!=null){
        podCastId = URLDecoder.decode(podCastId, "UTF-8");
    }

    if(podCastEpisodeId!=null){
        podCastEpisodeId = URLDecoder.decode(podCastEpisodeId, "UTF-8");
    }


    String shareImageURL = "http://static-cdn.sr.se/sida/images/2071/c7f07d6b-937a-4b83-9658-db76c00a19b8.jpg";
    String podCastTitle = "PodCast...";
    String podCastEpisodeTitle = "Avsnitt...";

    if(podCastId!= null){
        Optional<PodCast> podCastById = PodCastCatalogService.getInstance().getPodCastById(podCastId);

        PodCast podCast = null;
        if(podCastById.isPresent()){
            podCast = podCastById.get();
            shareImageURL = podCast.getArtworkUrl600();
            podCastTitle = podCast.getTitle();
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
%>

<head>
<title>Pods @2017</title>

<!-- Apple smart banner -->
<meta name="apple-itunes-app" content="app-id=1209200428", app-argument=http://www.pods.one">
<!-- <meta property="al:ios:url" content="example://applinks"/>
 <meta property="al:ios:app_store_id" content="1209200428"/>
 <meta property="al:ios:app_name" content="PodsApp"/> -->

<!-- Facebook -->
<!-- https://developers.facebook.com/docs/sharing/opengraph/object-properties-->
<meta property="og:title" content="<%=podCastTitle%>"/>
<meta property="og:description"
      content="<%=podCastEpisodeTitle%>"/>
<meta property="og:type" content="website"/>
<meta property="og:image" content="<%=shareImageURL%>"/>

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

    video#backgroundvid {
        position: fixed;
        right: 0;
        bottom: 0;
        min-width: 100%;
        min-height: 100%;
        width: auto;
        height: auto;
        z-index: -100;
    }

    video#videoStyle {
        margin-left: auto;
        margin-right: auto;
        display: block;
        width: 600px;
        z-index: -100;
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

Pods: The PodCast Player<br>


<br>
<p>ShareImageURL:<%=shareImageURL%>
<br>
PodCastTitle:<%=podCastTitle%>
<br>
PodCastEpisodeTitle:<%=podCastEpisodeTitle%>
<br>

<%=request.getRequestURL()%>
    <br>
    <%
        StringBuilder keyValues = new StringBuilder();
        for (String s : request.getParameterMap().keySet()) {
            keyValues.append("Key=").append(s).append(" : Value=").append(Arrays.toString(request.getParameterMap().get(s))).append("<br>");
        }
    %>
    <%=keyValues%>
</p>
</body>
</html>
