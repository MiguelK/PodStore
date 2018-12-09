package com.podcastcatalog.service.subscription;

import com.google.gson.Gson;
import com.podcastcatalog.model.subscription.Subscriber;
import com.podcastcatalog.model.subscription.Subscription;
import com.podcastcatalog.model.subscription.SubscriptionData;
import com.podcastcatalog.service.datastore.ServiceDataStorage;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PodCastSubscriptionService {

    private static final PodCastSubscriptionService INSTANCE = new PodCastSubscriptionService();
    private static final String SUBSCRIPTIONS_JSON_FILE = "Subscriptions.dat";
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(PodCastSubscriptionService.class.getName());

    private SubscriptionData subscriptionData = new SubscriptionData();

    private final ExecutorService threadPool = Executors.newFixedThreadPool(1);

    public static PodCastSubscriptionService getInstance() {
        return INSTANCE;
    }

    public void start(File accountConfFile) {

        LOG.info("Starting PodCastSubscriptionService using account file= " + accountConfFile.getAbsolutePath());
        PushMessageClient.getInstance().configure(accountConfFile);

        try {
            subscriptionData = loadSubscribers();
            if(subscriptionData != null){
                LOG.info("Loaded " + subscriptionData.getSubscriptions().size());
            }
        } catch (IOException e) {
            LOG.warning("Failed to load Subscribers " + e.getMessage());
        }
    }

    //If subscriptionData is null == file exists but not able to load.
    public boolean isSubscribersLoaded() {
        return subscriptionData != null;
    }

    public void recreateIfSubscriptionFileIsDeleted() {

        CloseableHttpClient client = HttpClients.createDefault();
        try (CloseableHttpResponse response = client.execute(new HttpGet("http://pods.one/Subscriptions/Subscriptions.dat"))) {

            if (response.getStatusLine().getStatusCode() == 404) {
                LOG.info("No Subscriptions.dat file exist on server. Creating new subscriptionData");
                this.subscriptionData = new SubscriptionData();
                uploadToOneCom();
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOG.info("Check subscription file failed " + e.getMessage());

        }
    }

    SubscriptionData loadSubscribers() throws IOException {

        File downloadedFile = new File(ServiceDataStorage.useDefault().getPodDataHomeDir(), SUBSCRIPTIONS_JSON_FILE);

        CloseableHttpClient client = HttpClients.createDefault();
        try (CloseableHttpResponse response = client.execute(new HttpGet("http://pods.one/Subscriptions/Subscriptions.dat"))) {

            if (response.getStatusLine().getStatusCode() == 404) {
                LOG.info("No Subscriptions.dat file exist on server");
                return new SubscriptionData();
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
                    return ((SubscriptionData) in.readObject());
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

        return null;
    }

    public void uploadToOneCom() {

        //1# Write to temp file... subscriptions.json
        File file = new File(ServiceDataStorage.useDefault().getPodDataHomeDir(), SUBSCRIPTIONS_JSON_FILE);
        try {
            // saveAsJSON(file);
            saveAsObject(subscriptionData, file);
            FtpFileUploader.getInstance().uploadToOneCom(file);
        } catch (IOException e) {
            LOG.info("Failed push message" + e.getMessage());
        }
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

    public void subscribe(String deviceToken, String podCastId) {

        writeLock.lock();

      /*  Subscriber subscriber = subscriptionData.getSubscriber(deviceToken);
        if (subscriber == null) {
            subscriber = new Subscriber(deviceToken);
            subscriptionData.registerSubscriber(subscriber);
        }*/

        Subscription subscription = subscriptionData.getSubscription(podCastId);
        //Ok forts subscriber for this content
        if (subscription == null) {
            subscription = new Subscription(podCastId);
            subscriptionData.addSubscription(subscription);
        }

        try {
           // subscriber.addSubscription(subscription);
            subscription.addSubscriber(deviceToken);

            LOG.info("subscribe= " + subscriptionData);
        } finally {
            writeLock.unlock();
        }
    }

    public void unSubscribe(String deviceToken, String podCastId) {
        writeLock.lock();
        try {
            subscriptionData.unSubscribe(deviceToken, podCastId);
            LOG.info("unSubscribe= " + subscriptionData);
        } finally {
            writeLock.unlock();
        }
    }

    public void deleteSubscriber(String deviceToken) {
        writeLock.lock();
        try {
          //FIXME needed?  subscriptionData.deleteSubscriber(deviceToken);
        } finally {
            writeLock.unlock();
        }
    }

    public List<Subscription> getSubscriptions() {
        readLock.lock();
        try {
            return subscriptionData.getSubscriptions();
        } finally {
            readLock.unlock();
        }
    }

    public void pushMessage(String title, String body, String description, String podCastEpisodeInfo, String pid, String eid, String deviceToken) {
        threadPool.submit(() -> {
            PushMessageClient.getInstance().pushMessageWithToken(title,
                    body, description, podCastEpisodeInfo, pid, eid, deviceToken);
        });
    }
}
