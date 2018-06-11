package com.podcastcatalog.util;

import java.io.File;
import java.util.logging.Logger;

public class ServerInfo {

    private final static Logger LOG = Logger.getLogger(ServerInfo.class.getName());

    public static final  File localPath = new File("//Users/miguelkrantz/Documents/intellij-projects/PodStore/");

    public static boolean isLocalDevMode() {
        return localPath.isDirectory() && localPath.exists();
    }

    public static boolean shouldBuildSuggestionIndex() {
        if(isLocalDevMode()) {
            return false;
        }
        return true;
    }
}
