package com.podcastcatalog.util;

import com.podcastcatalog.StartupServlet;

import java.io.File;
import java.util.logging.Logger;

public class ServerInfo {

    private final static Logger LOG = Logger.getLogger(ServerInfo.class.getName());

    public static boolean isLocalDevMode() {
        File localPath = new File("/Users/miguelkrantz/Documents/temp/");

        return localPath.isDirectory() && localPath.exists();
    }
    
    public static boolean isSWEMode() {
        return true;
    }

    public static boolean isUSMode() {
        return true;
    }
}
