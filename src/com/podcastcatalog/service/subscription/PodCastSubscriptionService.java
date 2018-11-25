package com.podcastcatalog.service.subscription;

import com.podcastcatalog.model.subscription.Subscriber;
import com.podcastcatalog.model.subscription.Subscription;
import com.podcastcatalog.model.subscription.SubscriptionData;
import com.podcastcatalog.service.datastore.ServiceDataStorage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
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
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(PodCastSubscriptionService.class.getName());

    private SubscriptionData subscriptionData = new SubscriptionData();

    private final ExecutorService threadPool =  Executors.newFixedThreadPool(1);

    public static PodCastSubscriptionService getInstance() {
        return INSTANCE;
    }

    public void start(File accountConfFile) {

        //FIXME
            LOG.info("Account file= " + accountConfFile.getAbsolutePath());
            PushMessageClient.getInstance().configure(accountConfFile);

        subscriptionData = ServiceDataStorage.useDefault().loadSubscriptionData();
    }

    public void uploadToOneCom() {
        //FtpFileUploader
        //1 subscriptionData -> JSON file
        //2 ftp upload json file to one.com

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

    public void pushMessage(String title, String body, String pid, String eid, String deviceToken) {
        threadPool.submit(() -> {
            //Nytt avsnitt från
            PushMessageClient.getInstance().pushMessageWithToken(title,
                    body, pid, eid, deviceToken);
        });
    }
}
