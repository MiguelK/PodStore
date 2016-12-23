<%@ page import="com.podcastcatalog.service.podcastcatalog.PodCastCatalogService" %>
<%@ page import="com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage" %>
<%@ page import="javax.ws.rs.core.Response" %>
<%@ page import="com.podcastcatalog.util.StringFormatter" %>
<%@ page import="com.podcastcatalog.model.subscription.Subscription" %>
<%@ page import="com.podcastcatalog.service.subscription.PodCastSubscriptionService" %>
<%@ page import="com.podcastcatalog.model.podcastcatalog.PodCastCatalog" %><%--
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
    String contextPath = request.getContextPath();

    if (request.getParameter("action") != null) {

        PodCastCatalogService.getInstance().buildPodCastCatalogsAsync();
        out.println("Start building PodCastCatalog(s)...<br><br>");
    }

    PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.Sweden);
    StringFormatter podCastCatalogStatus = StringFormatter.create(podCastCatalog);
%>

<%=podCastCatalogStatus.format()%>

<br>

<tr>
    <td>SubscriptionService</td>
    <td><%=PodCastSubscriptionService.getInstance().getStatusAsHTLM() %>
    </td>
</tr>

<table>
    <tr>
        <td>PodCastCatalogIndex</td>
        <td><%= PodCastCatalogService.getInstance().getPodCastCatalogIndexStatus() %>
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
                    Rebuild PodCastCatalog(s)
                </button>
            </form>
        </td>
        <td>
            <form action="">
                <button type="submit" value="refresh" title="refresh">
                    Refresh View (Call when Rebuilding PodCastCatalog(s) )
                </button>
            </form>
        </td>
    </tr>

    <tr>
        <td>##################### API ################################ </td>
    </tr>

    <tr>
        <td>
            <a href="<%=contextPath%>/api/podCastCatalog?lang=SE">getPodCastCatalog (SE)</a>
        </td>
    </tr>

    <tr>

        <td>
            <a href="<%=contextPath%>/api/jsonfile?lang=SE">get ZIP file (SE)</a>
     </td>
    </tr>

    <tr>
        <td>
            <form method="post" action="http://localhost:10080/PodStore/api/subscriber/123">
                <button type="submit" value="subscribe" title="subscribe">
                    Create Subscribe (123)
                </button>
            </form>
        </td>
    </tr>
    <tr>
        <td>
            <form method="post" action="http://localhost:10080/PodStore/api/podCastSubscription/123/983093754">
                <button type="submit" value="subscribe" title="subscribe">
                    subscribe (SubscribeId=123)
                </button>
            </form>
        </td>
    </tr>
</table>
<br>
</body>
</html>
