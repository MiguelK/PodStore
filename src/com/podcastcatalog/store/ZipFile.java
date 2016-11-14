package com.podcastcatalog.store;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFile {


    public static void zip(File file, File zipFileName) {

        FileOutputStream fileOutputStream = null;

        ZipOutputStream zipOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            //create ZipOutputStream to write to the zip file
            fileOutputStream = new FileOutputStream(zipFileName);
            zipOutputStream = new ZipOutputStream(fileOutputStream);
            //add a new Zip Entry to the ZipOutputStream
            ZipEntry ze = new ZipEntry(file.getName());
            zipOutputStream.putNextEntry(ze);
            //read the file and write to ZipOutputStream
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fileInputStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, len);
            }

            //Close the zip entry to write to zip file
            zipOutputStream.closeEntry();
            System.out.println(file.getCanonicalPath() + " is zipped to " + zipFileName);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(zipOutputStream);
        }
    }

}
