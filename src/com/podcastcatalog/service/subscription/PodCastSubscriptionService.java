package com.podcastcatalog.service.subscription;

import com.podcastcatalog.model.subscription.Subscription;
import com.podcastcatalog.model.subscription.SubscriptionData;
import com.podcastcatalog.util.ServerInfo;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PodCastSubscriptionService {

    private static final PodCastSubscriptionService INSTANCE = new PodCastSubscriptionService();
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(PodCastSubscriptionService.class.getName());

    private SubscriptionData subscriptionData = new SubscriptionData();

    private final ExecutorService pushMessagesThreadPool = Executors.newFixedThreadPool(1);

    public static PodCastSubscriptionService getInstance() {
        return INSTANCE;
    }

    public void start(File accountConfFile) {

        LOG.info("Starting PodCastSubscriptionService using account file= " + accountConfFile.getAbsolutePath());
        PushMessageClient.getInstance().configure(accountConfFile);

        subscriptionData = FtpOneClient.getInstance().loadSubscribers();
        if(subscriptionData != null) {
            LOG.info("Loaded " + subscriptionData.getSubscriptions().size());
        } else {
            LOG.log(Level.SEVERE, "Unable to load  subscriptionData (null)");
        }
    }

    //If subscriptionData is null == file exists but not able to load.
    public boolean isSubscribersLoaded() {
        return subscriptionData != null;
    }

    private boolean isNotLoaded() {
        return !isSubscribersLoaded();
    }

    //FIXME do in background?
    public void uploadToOneCom() {
        FtpOneClient.getInstance().upload(subscriptionData);
    }

    public void subscribe(String deviceToken, String podCastId) {
        if(isNotLoaded()) {
            return;
        }

        writeLock.lock();


        Subscription subscription = subscriptionData.getSubscription(podCastId);
        //Ok forts subscriber for this content
        if (subscription == null) {
            subscription = new Subscription(podCastId);
            subscriptionData.addSubscription(subscription);
        }

        try {
           // subscriber.addSubscription(subscription);
            subscription.addSubscriber(deviceToken);

           // LOG.info("subscribe() subscribers= " + subscription.getSubscribers().size() + " https://pods.one/open?pid=" + podCastId);

           // FtpOneClient.getInstance().upload(subscriptionData);
        } finally {
            writeLock.unlock();
        }
    }

    public void unSubscribe(String deviceToken, String podCastId) {
        if(isNotLoaded()) {
            return;
        }
        writeLock.lock();
        try {
            subscriptionData.unSubscribe(deviceToken, podCastId);

            Subscription subscription = subscriptionData.getSubscription(podCastId);


            String info = "no subscribers for podCastId=" + podCastId;
            if(subscription!=null) {
                info = subscription.getSubscribers().size() + " subscribers for podCastId=" + podCastId;
            }

            if (ServerInfo.isLocalDevMode()) {
                LOG.info("unSubscribe(), " + info);
            }

            //FtpOneClient.getInstance().upload(subscriptionData);
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

    public void update(String podCastId, String latestPodCastEpisodeId) {
        if(isNotLoaded()) {
            return;
        }
        writeLock.lock();
        try {
            Subscription subscription = subscriptionData.getSubscription(podCastId);
            if(subscription != null) {
                subscription.setLatestPodCastEpisodeId(latestPodCastEpisodeId);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void updateFeedURL(String podCastId, URL feedURL) {
        if(isNotLoaded()) {
            return;
        }
        writeLock.lock();
        try {
            Subscription subscription = subscriptionData.getSubscription(podCastId);
            if(subscription != null) {
                subscription.setFeedURL(feedURL);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public List<Subscription> getSubscriptions() {
        if(isNotLoaded()) {
            return Collections.emptyList();
        }
        readLock.lock();
        try {
            return subscriptionData.getSubscriptions();
        } finally {
            readLock.unlock();
        }
    }

    public void pushMessage(String title, String body, String description, String podCastEpisodeInfo, String pid, String eid, String deviceToken) throws IOException {
      //  pushMessagesThreadPool.submit(() -> {
            PushMessageClient.getInstance().pushMessageWithToken(title,
                    body, description, podCastEpisodeInfo, pid, eid, deviceToken);
     //   });
    }
}
