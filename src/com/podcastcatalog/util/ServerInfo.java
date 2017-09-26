package com.podcastcatalog.util;

import com.podcastcatalog.StartupServlet;

import java.util.logging.Logger;

public class ServerInfo {

    private final static Logger LOG = Logger.getLogger(ServerInfo.class.getName());
    
    public static boolean isSWEMode() {
        return true;
    }

    public static boolean isUSMode() {
        return true;
    }
}
