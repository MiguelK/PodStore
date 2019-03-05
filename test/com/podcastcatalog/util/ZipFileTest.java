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
import java.util.Arrays;
import java.util.List;

public class ZipFileTest {

    @Test(groups = TestUtil.SLOW_TEST)
    public void zip() throws IOException {

        File someFile = new File("some.json");

        System.out.println(someFile.getAbsolutePath());

        List<String> lines = Arrays.asList("The first line", "The second line");
        Path sourceFile =  someFile.toPath();   //Paths.get("the-sourceFile-name.txt");
        Files.write(sourceFile, lines, Charset.forName("UTF-8"));

        someFile = new File("/Users/miguelkrantz/Documents/temp/words.json");

        File targetFile = new File(LocatorProduction.getInstance().getPodDataHomeDirectory(), "ZipFileTest.zip");
        ZipFile.zip(someFile, targetFile);

        Assert.assertTrue(targetFile.canRead());
        Assert.assertTrue(targetFile.exists());
        Assert.assertTrue(targetFile.isFile());
        Assert.assertTrue(Files.size(targetFile.toPath())>30);

    }
}