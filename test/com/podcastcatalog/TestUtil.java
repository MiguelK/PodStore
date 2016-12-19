package com.podcastcatalog;

import com.google.gson.Gson;
import org.apache.commons.lang3.ClassUtils;
import org.testng.Assert;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class TestUtil {

    private static final String TEMP_FILE_PATH = System.getProperty("java.io.tmpdir");
   public static final File IO_TEMP_DATA_DIRECTORY;

    static {
       /* File a = new File("/home/krantmig/tools/temp");
        File b = new File("/Users/miguelkrantz/Documents/temp/podda/testng-data");

        if (a.exists() && a.isDirectory() && a.canRead() && a.canWrite()) {
            IO_TEMP_DATA_DIRECTORY = a;
        } else if (b.exists() && b.isDirectory() && b.canRead() && b.canWrite()) {
            IO_TEMP_DATA_DIRECTORY = b;
        } else {*/
            IO_TEMP_DATA_DIRECTORY = new File(TEMP_FILE_PATH);
//        }
    }

    public static final String SLOW_TEST = "slow";

    public static final Gson GSON = new Gson();


    public static void assertSerializable(Object obj) {
        List classes = ClassUtils.getAllInterfaces(obj.getClass());
        Assert.assertTrue(classes.contains(Serializable.class));
    }

    public static void assertToJSONNotNull(Object obj) {
        Assert.assertNotNull(GSON.toJson(obj));
    }

    public static void assertToJSONContains(Object obj, String json) {
        Assert.assertTrue(GSON.toJson(obj).contains(json));
    }
}
