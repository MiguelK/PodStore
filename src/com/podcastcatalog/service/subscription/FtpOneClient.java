package com.podcastcatalog.service.subscription;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.podcastcatalog.model.PodCastCatalogMetaData;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalog;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.subscription.SubscriptionData;
import com.podcastcatalog.service.appstatistic.AppStatisticDataContainer;
import com.podcastcatalog.service.datastore.LocatorProduction;
import com.podcastcatalog.util.IOUtil;
import com.podcastcatalog.util.ServerInfo;
import com.podcastcatalog.util.ZipFile;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FtpOneClient {

    private static final Gson GSON;

    static {
        List<String> fieldExclusions = new ArrayList<String>();
        fieldExclusions.add("podCastEpisodesInternal"); //FIXME Maybe ok to send all episodes to client to big?

        List<Class<?>> classExclusions = new ArrayList<Class<?>>();
        // classExclusions.add(PodCast.class);
        GSON = GsonFactory.build(fieldExclusions, classExclusions);
    }

    public static final String PATH_SUBSCRIPTION = "/Subscriptions/";
    public static final String PATH_LANGUAGE = "/language/";

    private static final String SUBSCRIPTIONS_JSON_FILE = "Subscriptions.dat";
    private static final String POD_CAST_CATALOG_META_DATA_FILE = "_MetaData.dat";
    private static final String PODS_ONE_HOST_NAME = "http://pods.one";

    public static final String SUBSCRIPTIONS_PATH = PODS_ONE_HOST_NAME + PATH_SUBSCRIPTION;
    public static final String SUBSCRIPTIONS_FILE_URL = SUBSCRIPTIONS_PATH +
            SUBSCRIPTIONS_JSON_FILE;

    public static final String APP_STATISTICS_FILE_NAME = "AppStatistics.dat";
    public static final String APP_STATISTICS_PATH = "/AppStatistics/";
    public static final String APP_STATISTIC_FILE_URL = PODS_ONE_HOST_NAME + APP_STATISTICS_PATH + APP_STATISTICS_FILE_NAME;
    public static final String RADIO_STATION_FILE = PODS_ONE_HOST_NAME + "/Radio/radioStations.txt";

    private static FtpOneClient INSTANCE = new FtpOneClient();

    private static final Logger LOG = Logger.getLogger(FtpOneClient.class.getName());

    private final ExecutorService executorService;

    public static FtpOneClient getInstance() {
        return INSTANCE;
    }

    private FtpOneClient() {
        executorService = Executors.newSingleThreadExecutor();
    }

    //Upload JSON zip file used by App Client
     public void upload(PodCastCatalog podCastCatalog)  {

        PodCastCatalogLanguage lang = podCastCatalog.getPodCastCatalogLanguage();
        File langJSON = new File(LocatorProduction.getInstance().getPodDataHomeDirectory(), lang.name() + ".json");
        File jsonZipped = new File(LocatorProduction.getInstance().getPodDataHomeDirectory(), lang.name() + "_json.zip");

        try {
            saveAsJSON(podCastCatalog, langJSON);
        } catch (IOException e) {
            return;
        }
        ZipFile.zip(langJSON, jsonZipped);

        FtpOneClient.getInstance().uploadToOneCom(jsonZipped, PATH_LANGUAGE);
    }

    public void backup(PodCastCatalogLanguage lang) {

        File backupDir = new File(LocatorProduction.getInstance().getPodDataHomeDirectory() +
                File.separator + "backup");
        if(!backupDir.exists()) {
            backupDir.mkdirs();
        }

        File zippedJSON = new File(backupDir, lang.name() + "_json.zip");

        String requestURL = PODS_ONE_HOST_NAME + PATH_LANGUAGE +  lang.name() + "_json.zip";

        zippedJSON.delete();

        CloseableHttpClient client = HttpClients.createDefault();
        try {
            try (CloseableHttpResponse response = client.execute(new HttpGet(requestURL))) {

                if (response.getStatusLine().getStatusCode() == 404) {
                    LOG.info("No file exist on server requestURL=" + requestURL);
                    throw new IOException();
                }

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (FileOutputStream outstream = new FileOutputStream(zippedJSON)) {
                        entity.writeTo(outstream);
                    }

                    EntityUtils.consume(entity);
                }
            }
        } catch (Exception e) {
           LOG.warning("Unable to backup lang=" + lang + "from one.com" + e.getMessage());
        }

    }
    //Upload metaData dat zip file used by this server when start/restart
     public void upload(PodCastCatalogMetaData podCastCatalogMetaData, PodCastCatalogLanguage lang)  {

        String fileName = lang.name() + POD_CAST_CATALOG_META_DATA_FILE;

        File file = new File(LocatorProduction.getInstance().getPodDataHomeDirectory(), fileName);
        //LOG.info("Start uploading PodCastCatalogMetaData to server " + fileName
        //        + " " + podCastCatalogMetaData);

        try {
            IOUtil.saveAsObject(podCastCatalogMetaData, file);

            LOG.info("Validate PodCastCatalogMetaData " + lang);
            Thread.sleep(3000);
            Object object = IOUtil.getObject(file);

            PodCastCatalogMetaData validatedOK = (PodCastCatalogMetaData)object;
            if(validatedOK == null) {
                throw new IllegalArgumentException("Failed to load/validate PodCastCatalogMetaData " + lang);
            }

            LOG.info("Validate PodCastCatalogMetaData OK " + validatedOK);
            Thread.sleep(3000);

            FtpOneClient.getInstance().uploadToOneCom(file, PATH_LANGUAGE); //FIXME
        } catch (Exception e) {
            LOG.info("Failed to upload PodCastCatalogMetaData " + e.getMessage());
        }
    }


    public void  upload(AppStatisticDataContainer appStatisticData)  {
        if(ServerInfo.isLocalDevMode()) {
            LOG.info("DevMode no appStatisticData upload to one.com");
            return;
        }
        LOG.info("Uploading AppStatisticDataContainer to one.com " + appStatisticData.appStatisticDataLang.size());

        File file = new File(LocatorProduction.getInstance().getPodDataHomeDirectory(), APP_STATISTICS_FILE_NAME);
        try {
            IOUtil.saveAsObject(appStatisticData, file);
            FtpOneClient.getInstance().uploadToOneCom(file, APP_STATISTICS_PATH);
        } catch (IOException e) {
            LOG.info("Failed uploading appStatisticData to " + PODS_ONE_HOST_NAME + APP_STATISTICS_PATH + " message" + e.getMessage());
        }
    }
    public  AppStatisticDataContainer loadAppStatistics()  {
        File downloadedFile = new File(LocatorProduction.getInstance().getPodDataHomeDirectory(), APP_STATISTICS_FILE_NAME);

        try {
            return (AppStatisticDataContainer)loadFromServer(downloadedFile, APP_STATISTIC_FILE_URL);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to load object=" + downloadedFile.getAbsolutePath(), e.getMessage());
        }

        return null;
    }

    //Upload SubscriptionData dat file used by this server
     void  upload(SubscriptionData subscriptionData)  {
        if(ServerInfo.isLocalDevMode()) {
            LOG.info("DevMode no subscriptionData upload to one.com");
            return;
        }

        File file = new File(LocatorProduction.getInstance().getPodDataHomeDirectory(), SUBSCRIPTIONS_JSON_FILE);
        try {
            // LOG.info("upload subscriptions=" + subscriptionData.getSubscriptions().size());
            IOUtil.saveAsObject(subscriptionData, file);
            uploadToOneCom(file, PATH_SUBSCRIPTION);
        } catch (Exception e) {
            LOG.info("Failed uploading subscriptionData to " + PODS_ONE_HOST_NAME + PATH_SUBSCRIPTION + " message" + e.getMessage());
        }
    }

    public  SubscriptionData loadSubscribers() {
        File downloadedFile = new File(LocatorProduction.getInstance().getPodDataHomeDirectory(), SUBSCRIPTIONS_JSON_FILE);

        try {
            return (SubscriptionData)loadFromServer(downloadedFile, SUBSCRIPTIONS_FILE_URL);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to load object=" + downloadedFile.getAbsolutePath(), e.getMessage());
        }

        return null;
    }


     public PodCastCatalogMetaData load(PodCastCatalogLanguage lang) throws IOException {

        String fileName = lang.name() + POD_CAST_CATALOG_META_DATA_FILE;
        File downloadedFile = new File(LocatorProduction.getInstance().getPodDataHomeDirectory(), fileName);
        try {
            return (PodCastCatalogMetaData)loadFromServer(downloadedFile, PODS_ONE_HOST_NAME + PATH_LANGUAGE + fileName);
        } catch (Exception e) {
            if(ServerInfo.isLocalDevMode()) {
                e.printStackTrace();
                LOG.log(Level.WARNING, "Unable to load object=" + downloadedFile.getAbsolutePath(), e.getMessage());
            }

            return null;
        }
    }


     public void uploadToOneCom(File sourceFile, String serverPath) {
        try {
            boolean b = sourceFile.setLastModified(System.currentTimeMillis());
            FileTask fileTask = new FileTask(sourceFile, serverPath);

            executorService.submit(fileTask);
        } catch (Exception ex) {
            LOG.info("Failed submit new task " + ex.getMessage());
        }
    }

    public Object loadFromServer(File downloadedFile, String requestURL) throws Exception {


        downloadedFile.delete();

        CloseableHttpClient client = HttpClients.createDefault();
        try (CloseableHttpResponse response = client.execute(new HttpGet(requestURL))) {

            if (response.getStatusLine().getStatusCode() == 404) {
                LOG.info("No file exist on server requestURL=" + requestURL);
                throw new IOException();
            }

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (FileOutputStream outstream = new FileOutputStream(downloadedFile)) {
                    entity.writeTo(outstream);
                }

                EntityUtils.consume(entity);
            }

            Object in = IOUtil.getObject(downloadedFile);
            if (in != null) return in;
        }

        throw new IOException();
    }



    public static class GsonFactory {

        public static Gson build(final List<String> fieldExclusions, final List<Class<?>> classExclusions) {
            GsonBuilder b = new GsonBuilder();
            b.addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return fieldExclusions != null && fieldExclusions.contains(f.getName());
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return classExclusions != null && classExclusions.contains(clazz);
                }
            });
            return b.create();

        }
    }
    private void saveAsJSON(PodCastCatalog podCastCatalog, File file) throws IOException {

        try {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8")) {
                GSON.toJson(podCastCatalog, writer);
            }
        } catch (IOException e) {
            LOG.info("Failed saveAsJSON " + e.getMessage());
            throw new IOException(e);
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
                } catch (Exception ex) {
                    LOG.info("Failed close stream " + ex.getMessage());
                }
            }
        }
    }
}
