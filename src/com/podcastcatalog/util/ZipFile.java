package com.podcastcatalog.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFile {

    static final int BUFFER = 2048;

    public static void zip(File sourceFile, File targetFile) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new
                    FileOutputStream(targetFile);
            CheckedOutputStream checksum = new
                    CheckedOutputStream(dest, new Adler32());
            ZipOutputStream out = new
                    ZipOutputStream(new
                    BufferedOutputStream(checksum));
            //out.setMethod(ZipOutputStream.DEFLATED);
            byte data[] = new byte[BUFFER];
            // get a list of files from current directory


           // File f = new File(".");
            // String files[] = f.list();

            //for (int i=0; i<files.length; i++) {
            //System.out.println("Adding: "+files[i]);
                FileInputStream fi = new
                        FileInputStream(sourceFile);
                origin = new
                        BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(sourceFile.getName());
                out.putNextEntry(entry);
                int count;
                while((count = origin.read(data, 0,
                        BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            //   }
            out.close();
            System.out.println("checksum: "+checksum.getChecksum().getValue());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    /*

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
    }*/

}
