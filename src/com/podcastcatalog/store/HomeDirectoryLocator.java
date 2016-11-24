package com.podcastcatalog.store;

import java.io.File;

class HomeDirectoryLocator {

    private static final String OPENSHIFT_DATA_DIR = "OPENSHIFT_DATA_DIR";

    private static final String PODDA_HOME_DIR = "POD_DATA";

     File locateDataDirectory() {
        File a = new File("/home/krantmig/tools/temp");

        if (isReadAndWriteDirectory(a)) {
            return a;
        }

        File b = new File("/Users/miguelkrantz/Documents/temp/podda");

        if (isReadAndWriteDirectory(b)) {
            return b;
        }


        String openShiftDataDir = System.getenv(OPENSHIFT_DATA_DIR);
        File contentRoot = new File(openShiftDataDir, PODDA_HOME_DIR);

        if (!contentRoot.exists()) {
            contentRoot.mkdir();
        }

        if (isReadAndWriteDirectory(contentRoot)) {
            return contentRoot;

        }

        throw new IllegalStateException("Unable to locate home directory");

    }

    private boolean isReadAndWriteDirectory(File file) {
        return file.isDirectory() && file.canRead() && file.canWrite();
    }
}
