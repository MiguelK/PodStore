package com.podcastcatalog.util;

import java.io.File;
import java.util.logging.Logger;

public class ServerInfo {

    private final static Logger LOG = Logger.getLogger(ServerInfo.class.getName());

    public static boolean isLocalDevMode() {
        File localPath = new File("/Users/miguelkrantz/Documents/temp/");

        return localPath.isDirectory() && localPath.exists();
    }
}
