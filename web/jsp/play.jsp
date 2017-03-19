<%--
  Created by IntelliJ IDEA.
  User: miguelkrantz
  Date: 2017-03-19
  Time: 22:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    String sImage = request.getParameter("s");
    String cName = request.getParameter("c");


    String shareImageURL = "http://static-cdn.sr.se/sida/images/2071/c7f07d6b-937a-4b83-9658-db76c00a19b8.jpg";
    String channelDisplayName = "Sommar i P!";

    if(sImage!= null){
        shareImageURL = sImage;
    }

    if(cName != null){
        channelDisplayName = cName;
    }

%>

<head>
<title>Pods @2017</title>

<!-- Apple smart banner -->
<meta name="apple-itunes-app" content="app-id=1209200428">
<!-- <meta property="al:ios:url" content="example://applinks"/>
 <meta property="al:ios:app_store_id" content="454290334"/>
 <meta property="al:ios:app_name" content="ImageTV"/> -->

<!-- Facebook -->
<!-- https://developers.facebook.com/docs/sharing/opengraph/object-properties-->
<meta property="og:title" content="Pods"/>
<meta property="og:description"
      content="Lyssna på PodCasts"/>
<meta property="og:title" content="<%=channelDisplayName%>"/>
<meta property="og:type" content="website"/>
<!--    <meta property="og:image" content="http://jibjabbloggedyblog.files.wordpress.com/2014/01/extra-logo-1200x630-v2013_09_25_123759.jpg" /> -->
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

</style>
</head>

<body style="background-color : rgb(92,161,192);">

Pods:

</body>
</html>
