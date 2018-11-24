package com.podcastcatalog.service.subscription;

import com.podcastcatalog.model.subscription.ContentIdValidator;
import com.podcastcatalog.model.subscription.Subscriber;
import com.podcastcatalog.model.subscription.Subscription;
import com.podcastcatalog.model.subscription.SubscriptionData;
import com.podcastcatalog.service.datastore.ServiceDataStorage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Optional;
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

    public static PodCastSubscriptionService getInstance() {
        return INSTANCE;
    }

    public void start() {

        //FIXME
        File file = new File("/Users/miguelkrantz/Documents/intellij-projects/FirebaseClient/pods-service.account.json");

        PushMessageClient.getInstance().configure(file);
        subscriptionData = ServiceDataStorage.useDefault().loadSubscriptionData();
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


            LOG.info("subscriptionData= " + subscriptionData);
        } finally {
            writeLock.unlock();
        }
    }

    public void unSubscribe(String deviceToken, String contentId) {
        writeLock.lock();
        try {
            subscriptionData.unSubscribe(deviceToken, contentId);
            LOG.info("subscriptionData= " + subscriptionData);
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


    public Subscriber getSubscriber(String deviceToken) {
        readLock.lock();
        try {
            return subscriptionData.getSubscriber(StringUtils.trimToEmpty(deviceToken));
        } finally {
            readLock.unlock();
        }
    }

    public void registerSubscriber(String deviceToken) {
        Subscriber subscriber = new Subscriber(deviceToken);
        writeLock.lock();
        try {
            subscriptionData.registerSubscriber(subscriber);
        } finally {
            writeLock.unlock();
        }
    }

    public void pushMessage(String title, String body, String deviceToken) {
        //FIXME add to queu
        //FIXME Update pushDateTime

        PushMessageClient.getInstance().pushMessageWithToken(title,
                body, deviceToken);
    }

    public String getStatusAsHTLM() {

        StringBuilder html = new StringBuilder("<table>");
        html.append("<tr><td>Subscribers</td><td>").append(subscriptionData.getSubscribers().size()).append("</td></tr>");
        html.append("<tr><td>Subscriptions</td><td>").append(subscriptionData.getSubscriptions().size()).append("</td></tr>");
        for (Subscriber subscriber : subscriptionData.getSubscribers()) {
            html.append("<tr><td>").append(subscriber.getDeviceToken())
                    .append("</td>").append("<td>").append(subscriber.getSubscriptions().size())
                    .append("</td>").append("</tr>");
        }

        html.append("</table>");

        return html.toString();
    }

    private Optional<Subscription> getSubscription(String contentId) {
        readLock.lock();
        try {
            return Optional.ofNullable(subscriptionData.getSubscription(StringUtils.trimToEmpty(contentId)));
        } finally {
            readLock.unlock();
        }
    }
}
