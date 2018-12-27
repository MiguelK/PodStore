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

    public static final String PATH_SUBSCRIPTION = "/Subscriptions/";
    public static final String PATH_LANGUAGE = "/language/";

    private static FtpFileUploader INSTANCE = new FtpFileUploader();

    private static final Logger LOG = Logger.getLogger(FtpFileUploader.class.getName());

    private final ExecutorService executorService;

    public static FtpFileUploader getInstance() {
        return INSTANCE;
    }

    private FtpFileUploader() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public void uploadToOneCom(File sourceFile, String serverPath) {
        try {
            FileTask fileTask = new FileTask(sourceFile, serverPath);

            executorService.submit(fileTask);
        } catch (Exception ex) {
            LOG.info("Failed submit new task " + ex.getMessage());
        }
    }

    private class FileTask implements  Runnable {

        private final File sourceFile;
        private final String serverPath;

        FileTask(File sourceFile, String serverPath) {
            this.sourceFile = sourceFile;
            this.serverPath = serverPath;
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

                ftpClient.changeWorkingDirectory(serverPath);

                String fileName = getSourceFile().getName();
                inputStream =  new FileInputStream(getSourceFile());

               // LOG.info("Start uploading§ file " + fileName + " to server path=" + PATH_NAME_SERVER);
                boolean done = ftpClient.storeFile(fileName, inputStream);

                if (!done) {
                    LOG.info( fileName + " done=false failed upload to path=" + serverPath);
                }
            } catch (Exception ex) {
                LOG.warning("Failed to upload file to server " + ex.getMessage());
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
                    LOG.info("Failed close stream " + ex.getMessage());
                }
            }
        }
    }
}
