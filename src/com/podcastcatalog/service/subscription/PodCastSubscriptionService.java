package com.podcastcatalog.service.subscription;

import com.google.gson.Gson;
import com.podcastcatalog.model.subscription.Subscriber;
import com.podcastcatalog.model.subscription.Subscription;
import com.podcastcatalog.model.subscription.SubscriptionData;
import com.podcastcatalog.service.datastore.ServiceDataStorage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class PodCastSubscriptionService {

    private static final PodCastSubscriptionService INSTANCE = new PodCastSubscriptionService();
    private static final String SUBSCRIPTIONS_JSON_FILE = "Subscriptions.json";
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(PodCastSubscriptionService.class.getName());

    private SubscriptionData subscriptionData = new SubscriptionData();

    private final ExecutorService threadPool =  Executors.newFixedThreadPool(1);

    public static PodCastSubscriptionService getInstance() {
        return INSTANCE;
    }

    private final Gson GSON = new Gson();

    public void start(File accountConfFile) {

        //FIXME
            LOG.info("Starting PodCastSubscriptionService using account file= " + accountConfFile.getAbsolutePath());
            PushMessageClient.getInstance().configure(accountConfFile);

        subscriptionData = ServiceDataStorage.useDefault().loadSubscriptionData();
    }

    public void uploadToOneCom() {

        //1# Write to temp file... subscriptions.json
        File file = new File(ServiceDataStorage.useDefault().getPodDataHomeDir(), SUBSCRIPTIONS_JSON_FILE);
        saveAsJSON(file);

        FtpFileUploader.getInstance().uploadToOneCom(file);
    }

    private void saveAsJSON(File target) {

        try {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(target))) {
                GSON.toJson(subscriptionData, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void subscribe(String deviceToken, String podCastId) {

        writeLock.lock();

        Subscriber subscriber = subscriptionData.getSubscriber(deviceToken);
        if (subscriber == null) {
            subscriber = new Subscriber(deviceToken);
            subscriptionData.registerSubscriber(subscriber);
        }

        Subscription subscription = subscriptionData.getSubscription(podCastId);
        //Ok forts subscriber for this content
        if (subscription == null) {
            subscription = new Subscription(podCastId);
            subscriptionData.addSubscription(subscription);
        }

        try {
            subscriber.addSubscription(subscription);
            subscription.addSubscriber(subscriber);

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
            subscriptionData.deleteSubscriber(deviceToken);
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

    public void pushMessage(String title, String body, String description, String pid, String eid, String deviceToken) {
        threadPool.submit(() -> {
            PushMessageClient.getInstance().pushMessageWithToken(title,
                    body, description, pid, eid, deviceToken);
        });
    }
}
