package com.podcastcatalog.model;

import com.podcastcatalog.TestUtil;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.logging.Level;

import static org.testng.Assert.*;

public class PodCastCatalogMetaDataTest {

    @Test(groups = TestUtil.SLOW_TEST)
    public void testToString() throws Exception {


    File source = new File("/tmp/POD_DATA_HOME/ES_MetaData.dat");

        ObjectInputStream in = null;
        FileInputStream fileIn = null;
        try {
            try {
                fileIn = new FileInputStream(source);
                in = new ObjectInputStream(fileIn);
                Object o = in.readObject();

                System.out.println("DONE " + o);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } finally {
            if (in != null) {
                IOUtils.closeQuietly(in);
            }
            if (fileIn != null) {
                IOUtils.closeQuietly(fileIn);
            }
        }
    }

}