<%@ page import="com.podcastcatalog.PodCastCatalogService" %>
<%@ page import="com.podcastcatalog.api.response.PodCastCatalogLanguage" %>
<%@ page import="javax.ws.rs.core.Response" %>
<%@ page import="com.podcastcatalog.api.util.StringFormatter" %><%--
  Created by IntelliJ IDEA.
  User: krantmig
  Date: 11/8/16
  Time: 5:09 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin page</title>
</head>
<body>

<%

    if (request.getParameter("action") != null) {

        PodCastCatalogService.getInstance().buildPodCastCatalogsAsync();
        out.println("Start reloding Catalog...<br><br>");
    }

    com.podcastcatalog.api.response.PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);
    StringFormatter stringFormatter = StringFormatter.create(podCastCatalog);
%>

<%=stringFormatter.format()%>

<table>
    <tr>
        <td>PodCastIndex</td>
        <td><%= PodCastCatalogService.getInstance().getPodCastIndexStatus() %>
        </td>
    </tr>

    <tr>
        <td>TextSearchEngine</td>
        <td><%= PodCastCatalogService.getInstance().getTextSearchEngineStatus() %>
        </td>
    </tr>

</table>

<br>

<table>
    <tr>
        <td>
            <form action="">
                <input type="hidden" id="thisField" name="action" value="action">
                <button type="submit" value="relod" title="reload">
                    Reload
                </button>
            </form>
        </td>

        <td>
            <form action="">
                <button type="submit" value="refresh" title="refresh">
                    Refresh
                </button>
            </form>
        </td>
    </tr>

    <tr>

        <td>
            <form action="http://localhost:10080/PodStore/api/podCastCatalog?lang='SV'">
                <button type="submit" value="refresh" title="refresh">
                    API getPodCastCatalog() SE
                </button>
            </form>
        </td>
    </tr>

    <tr>

        <td>
            <form action="http://localhost:10080/PodStore/jsonfile?lang='SV'">
                <button type="submit" value="refresh" title="refresh">
                    get ZIP file
                </button>
            </form>
        </td>
    </tr>

</table>

<br>


</body>
</html>
