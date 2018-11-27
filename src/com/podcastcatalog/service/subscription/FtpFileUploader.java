package com.podcastcatalog.service.subscription;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class FtpFileUploader {

    private static FtpFileUploader INSTANCE = new FtpFileUploader();

    private static final Logger LOG = Logger.getLogger(FtpFileUploader.class.getName());

    private final ExecutorService executorService;

    public static FtpFileUploader getInstance() {
        return INSTANCE;
    }

    private FtpFileUploader() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public void uploadToOneCom(File sourceFile) {
        try {
            FileTask fileTask = new FileTask(sourceFile);

            executorService.submit(fileTask);
        } catch (Exception ex) {
            LOG.info("Failed submit new task " + ex.getMessage());
        }
    }

    private class FileTask implements  Runnable {

        private final File sourceFile;

        FileTask(File sourceFile) {
            this.sourceFile = sourceFile;
        }


        File getSourceFile() {
            return sourceFile;
        }

        @Override
        public void run() {

            String server = "ftp.pods.one";
            int port = 21;
            String user = "pods.one";
            String pass = "Kodar%123";

            FTPClient ftpClient = new FTPClient();
            InputStream inputStream = null;
            try {

                ftpClient.connect(server, port);
                ftpClient.login(user, pass);
                ftpClient.enterLocalPassiveMode();

                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                String pathName = "/Subscriptions/";
                ftpClient.changeWorkingDirectory(pathName);

                String fileName = getSourceFile().getName();
                inputStream =  new FileInputStream(getSourceFile());

                LOG.info("Start uploading§ file " + fileName + " to server path=" + pathName);
                boolean done = ftpClient.storeFile(fileName, inputStream);

                if (done) {
                    LOG.info( fileName + " file uploaded successfully.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                try {
                    if(inputStream!=null){
                        inputStream.close();
                    }
                    if (ftpClient.isConnected()) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
