package com.podcastcatalog.util;

import com.podcastcatalog.StartupServlet;

import java.util.logging.Logger;

public class ServerInfo {

    private final static Logger LOG = Logger.getLogger(ServerInfo.class.getName());


    public static boolean isUSMode() {
        //e.g appDns=podstore-itemstore.rhcloud.com
        //e.g appDns=podstore-us-itemstore.rhcloud.com

        String appDns = System.getenv("OPENSHIFT_APP_DNS");

        LOG.info("OPENSHIFT_APP_DNS=" + appDns);

        return appDns != null && appDns.contains("-us-");
    }
}
