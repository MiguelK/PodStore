package com.podcastcatalog;

import com.google.gson.Gson;
import org.apache.commons.lang3.ClassUtils;
import org.testng.Assert;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class TestUtil {

    private static final String TEMP_FILE_PATH = System.getProperty("java.io.tmpdir");
    public static final File IO_TEMP_DATA_DIRECTORY = new File(TEMP_FILE_PATH);

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

    /*public static ServiceDataStorage createForTest() {
        LocatorProduction production = new LocatorProduction();
        File f = new File(production.getPodDataHomeDirectory().getParentFile(), "POD_DATA_HOME_TEST");
        if (!f.exists()) {
            f.mkdirs();
        }


        return new ServiceDataStorageDisk(f);
    }*/
}
