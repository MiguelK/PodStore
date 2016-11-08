package com.podcastcatalog.store;

import java.io.File;

public class HomeDirectoryLocator {

    //public static final File CONTENT_ROOT_DIR;

    private static final String OPENSHIFT_DATA_DIR = "OPENSHIFT_DATA_DIR";

    private static final String PODDA_HOME_DIR = "POD_DATA";

   /* static {
        String openShiftDataDir = System.getenv(OPENSHIFT_DATA_DIR);
        CONTENT_ROOT_DIR = new File(openShiftDataDir, PODDA_HOME_DIR);
        if (!CONTENT_ROOT_DIR.exists()) {
            CONTENT_ROOT_DIR.mkdir();
        }

        if (!CONTENT_ROOT_DIR.exists()) {
            throw new IllegalArgumentException("FileServlet init param 'basePath' value '"
                    + CONTENT_ROOT_DIR.getAbsolutePath() + "' does actually not exist in file system.");
        } else if (!CONTENT_ROOT_DIR.isDirectory()) {
            throw new IllegalArgumentException("FileServlet init param 'basePath' value '"
                    + CONTENT_ROOT_DIR.getAbsolutePath() + "' is actually not a directory in file system.");
        } else if (!CONTENT_ROOT_DIR.canRead()) {
            throw new IllegalArgumentException("FileServlet init param 'basePath' value '"
                    + CONTENT_ROOT_DIR.getAbsolutePath() + "' is actually not readable in file system.");
        }
    }
*/

    public File locateDataDir() {
        File a = new File("/home/krantmig/tools/temp");//FIXME

        if (isReadAndWriteDirectory(a)) {
            return a;
        }

        File b = new File("/Users/miguelkrantz/Documents/temp/podda");//FIXME

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
