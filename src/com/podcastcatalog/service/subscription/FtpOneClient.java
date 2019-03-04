package com.podcastcatalog.service.subscription;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.podcastcatalog.model.PodCastCatalogMetaData;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalog;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.subscription.SubscriptionData;
import com.podcastcatalog.service.datastore.ServiceDataStorage;
import com.podcastcatalog.service.datastore.ServiceDataStorageDisk;
import com.podcastcatalog.util.ZipFile;
import org.apache.commons.io.IOUtils;
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FtpOneClient {

    private static final String PATH_SUBSCRIPTION = "/Subscriptions/";
    public static final String PATH_LANGUAGE = "/language/";

    private static final String SUBSCRIPTIONS_JSON_FILE = "Subscriptions.dat";
    private static final String POD_CAST_CATALOG_META_DATA_FILE = "_MetaData.dat";
    private static final String PODS_ONE_HOST_NAME = "http://pods.one";

    private static FtpOneClient INSTANCE = new FtpOneClient();

    private static final Logger LOG = Logger.getLogger(FtpOneClient.class.getName());

    private final ExecutorService executorService;

    public static FtpOneClient getInstance() {
        return INSTANCE;
    }

    private FtpOneClient() {
        executorService = Executors.newSingleThreadExecutor();
    }


    //Upload SubscriptionData dat file used by this server
    void upload(SubscriptionData subscriptionData)  {
        LOG.info("upload SubscriptionData to server ");

        File file = new File(ServiceDataStorage.useDefault().getPodDataHomeDir(), SUBSCRIPTIONS_JSON_FILE);
        try {
            saveAsObject(subscriptionData, file);
            FtpOneClient.getInstance().uploadToOneCom(file, PATH_SUBSCRIPTION);
        } catch (IOException e) {
            LOG.info("Failed push message" + e.getMessage());
        }
    }

    //Upload JSON zip file used by App Client
    public void upload(PodCastCatalog podCastCatalog, PodCastCatalogLanguage lang)  {
        LOG.info("upload PodCastCatalog to server " + lang);

        File langJSON = new File(ServiceDataStorage.useDefault().getPodDataHomeDir(), lang.name() + ".json");
        File jsonZipped = new File(ServiceDataStorage.useDefault().getPodDataHomeDir(), lang.name() + "_json.zip");

        try {
            saveAsJSON(podCastCatalog, langJSON);
        } catch (IOException e) {
            return;
        }
        ZipFile.zip(langJSON, jsonZipped);

        FtpOneClient.getInstance().uploadToOneCom(jsonZipped, PATH_LANGUAGE);
    }

    //Upload metaData dat zip file used by this server when start/restart
    public void upload(PodCastCatalogMetaData podCastCatalogMetaData, PodCastCatalogLanguage lang)  {

        String fileName = lang.name() + POD_CAST_CATALOG_META_DATA_FILE;

        File file = new File(ServiceDataStorage.useDefault().getPodDataHomeDir(), fileName);
        LOG.info("Start uploading PodCastCatalogMetaData to server " + fileName
                + " " + podCastCatalogMetaData);

        try {
            saveAsObject(podCastCatalogMetaData, file);
            FtpOneClient.getInstance().uploadToOneCom(file, PATH_LANGUAGE);
        } catch (IOException e) {
            LOG.info("Failed to upload PodCastCatalogMetaData " + e.getMessage());
        }
    }

    SubscriptionData loadSubscribers()  {
        File downloadedFile = new File(ServiceDataStorage.useDefault().getPodDataHomeDir(), SUBSCRIPTIONS_JSON_FILE);

        try {
            return (SubscriptionData)loadFromServer(downloadedFile, PODS_ONE_HOST_NAME + PATH_SUBSCRIPTION +
                     SUBSCRIPTIONS_JSON_FILE);
        } catch (Exception e) {
            LOG.log(Level.INFO, "Unable to load object=" + downloadedFile.getAbsolutePath(), e.getMessage());
        }

        return new SubscriptionData();
    }


    public PodCastCatalogMetaData load(PodCastCatalogLanguage lang) throws IOException {

        String fileName = lang.name() + POD_CAST_CATALOG_META_DATA_FILE;
        File downloadedFile = new File(ServiceDataStorage.useDefault().getPodDataHomeDir(), lang.name());
        try {
            return (PodCastCatalogMetaData)loadFromServer(downloadedFile, PODS_ONE_HOST_NAME + PATH_LANGUAGE + fileName);
        } catch (Exception e) {
          //  LOG.log(Level.INFO, "Unable to load object=" + downloadedFile.getAbsolutePath(), e.getMessage());
            return null;
        }
    }


    public void uploadToOneCom(File sourceFile, String serverPath) {
        try {
            LOG.info("Uploading " + sourceFile.getName() + "to " + serverPath);
            FileTask fileTask = new FileTask(sourceFile, serverPath);

            executorService.submit(fileTask);
        } catch (Exception ex) {
            LOG.info("Failed submit new task " + ex.getMessage());
        }
    }

    private Object loadFromServer(File downloadedFile, String requestURL) throws IOException {
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

            ObjectInputStream in = null;
            FileInputStream fileIn = null;
            try {
                try {
                    fileIn = new FileInputStream(downloadedFile);
                    in = new ObjectInputStream(fileIn);
                    return  in.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    LOG.log(Level.INFO, "Unable to load object=" + downloadedFile.getAbsolutePath(), e.getMessage());
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

        throw new IOException();
    }

    private void saveAsObject(Object object, File targetFile) throws IOException {
        FileOutputStream fileOut = null;
        ObjectOutputStream out = null;
        try {
            fileOut =
                    new FileOutputStream(targetFile);
            out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(fileOut);
        }
    }

    private static final Gson GSON;// = new Gson();

    static {
        List<String> fieldExclusions = new ArrayList<String>();
        fieldExclusions.add("podCastEpisodesInternal");

        List<Class<?>> classExclusions = new ArrayList<Class<?>>();
        // classExclusions.add(PodCast.class);
        GSON = ServiceDataStorageDisk.GsonFactory.build(fieldExclusions, classExclusions);
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
                } catch (IOException ex) {
                    LOG.info("Failed close stream " + ex.getMessage());
                }
            }
        }
    }
}
