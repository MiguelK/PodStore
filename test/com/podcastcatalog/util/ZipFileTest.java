package com.podcastcatalog.util;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.service.datastore.LocatorProduction;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZipFileTest {

    @Test(groups = TestUtil.SLOW_TEST)
    public void zip() throws IOException {

        File someFile = new File(LocatorProduction.getInstance().getPodDataHomeDirectory(),"some.json");

        System.out.println(someFile.getAbsolutePath());

        List<String> lines = Arrays.asList("The first line", "The second line");
        Path sourceFile =  someFile.toPath();   //Paths.get("the-sourceFile-name.txt");
        Files.write(sourceFile, lines, Charset.forName("UTF-8"));

        File targetFile = new File(LocatorProduction.getInstance().getPodDataHomeDirectory(), "ZipFileTest.zip");
        ZipFile.zip(someFile, targetFile);

        Assert.assertTrue(targetFile.canRead());
        Assert.assertTrue(targetFile.exists());
        Assert.assertTrue(targetFile.isFile());
        Assert.assertTrue(Files.size(targetFile.toPath())>30);

    }

    @Test
    public void saveObjects() throws IOException {

        File someFile = new File(TestUtil.IO_TEMP_DATA_DIRECTORY,"podcast_index_A.json");

        System.out.println(someFile.getAbsolutePath());

        List<String> lines = new ArrayList<>();
        for( int i=0; i< 1000; i++) {
            lines.add("PodCastname_" + i + ";pid=2362352653;imageURL=http://192.168.8.171:8080/service/customer/c/frontend/customer/engagement/123?test");
        }
      //  List<String> lines = Arrays.asList("The first line", "The second line");
        Path sourceFile =  someFile.toPath();   //Paths.get("the-sourceFile-name.txt");
        Files.write(sourceFile, lines, Charset.forName("UTF-8"));

    }

    }