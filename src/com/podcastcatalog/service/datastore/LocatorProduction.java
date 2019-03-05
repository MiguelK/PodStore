package com.podcastcatalog.service.datastore;

import java.io.File;

public class LocatorProduction  {

    private static final String OPENSHIFT_DATA_DIR = "OPENSHIFT_DATA_DIR";

    private static final String PODDA_HOME_DIR = "POD_DATA_HOME";//Must exist before starting app! //FIXME

    private static final LocatorProduction INSTANCE = new LocatorProduction();

    public static LocatorProduction getInstance() {
        return INSTANCE;
    }

    public File getPodDataHomeDirectory() {
         File  podDataHomeDir = new File("/tmp/", PODDA_HOME_DIR);

        if (!podDataHomeDir.exists()) {
            if (!podDataHomeDir.mkdirs()) {
                throw new IllegalStateException("Unable to create dirs " + podDataHomeDir.getAbsolutePath());
            }
        }

        if (isReadAndWriteDirectory(podDataHomeDir)) {
            return podDataHomeDir;

        }

        throw new IllegalStateException("Unable to locate home directory");

    }

    private boolean isReadAndWriteDirectory(File file) {
        return file.isDirectory() && file.canRead() && file.canWrite();
    }
}
