package com.podcastcatalog.subscribe;

import com.podcastcatalog.store.DataStorage;
import com.podcastcatalog.subscribe.internal.Subscriber;
import com.podcastcatalog.subscribe.internal.SubscriptionData;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class PodCastSubscriptions {

    private static final PodCastSubscriptions INSTANCE = new PodCastSubscriptions();
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(PodCastSubscriptions.class.getName());

    private SubscriptionData subscriptionData = new SubscriptionData();

    public static PodCastSubscriptions getInstance() {
        return INSTANCE;
    }

    public void loadFromDiskAsync(DataStorage dataStorage) {
        //FIXME load subscriptionData from disk async
        LOG.info("Loading SubscriptionData from disk...");
    }

    public void subscribe(String deviceToken, String contentId, ContentIdValidator contentIdValidator) {

        String deviceTokenTrimmed = StringUtils.trimToNull(deviceToken);
        String contentIdTrimmed = StringUtils.trimToNull(contentId);

        readLock.lock();
        Subscription subscription;
        Subscriber subscriber;
        try {
            subscriber = subscriptionData.getSubscriber(deviceTokenTrimmed);
            if (subscriber == null) {
                throw new IllegalArgumentException("No Subscriber with id " + deviceTokenTrimmed + " exists");
            }

            if (!contentIdValidator.isValidContentId(contentIdTrimmed)) {
                throw new IllegalArgumentException("Invalid contentId " + contentIdTrimmed + " not accepted by " + contentIdValidator);
            }

            subscription = subscriptionData.getSubscription(contentIdTrimmed);

        } finally {
            readLock.unlock();
        }

        writeLock.lock();
        try {
            if (subscription == null) {
                subscription = new Subscription(contentIdTrimmed);
                subscriptionData.addSubscription(subscription);
            }

            subscriber.addSubscription(subscription);
            subscription.addSubscriber(subscriber);
        } finally {
            writeLock.unlock();
        }
    }

    public void unSubscribe(String deviceToken, String contentId) {
    }

    public void deleteSubscriber(String deviceToken) {
    }

    //Throw exception if not existsing
    public Subscriber getSubscriber(String deviceToken) {
        readLock.lock();
        try {
            return subscriptionData.getSubscriber(StringUtils.trimToEmpty(deviceToken));
        } finally {
            readLock.unlock();
        }
    }

    public void registerSubscriber(String deviceToken) {
        String deviceTokenTrimmed = StringUtils.trimToNull(deviceToken);
        if (deviceTokenTrimmed == null) {
            throw new IllegalArgumentException("deviceToken is empty");
        }

        Subscriber subscriber = new Subscriber(deviceTokenTrimmed);
        writeLock.lock();
        try {
            subscriptionData.registerSubscriber(subscriber);
        } finally {
            writeLock.unlock();
        }
    }

    public void pushMessage(String message, String contentId) {
        //FIXME add to queu
    }

    public String getStatusAsHTLM() {

        StringBuilder html = new StringBuilder("<table>");
        html.append("<tr><td>Subscribers</td><td>").append(subscriptionData.getSubscribers().size()).append("</td></tr>");
        html.append("<tr><td>Subscriptions</td><td>").append(subscriptionData.getSubscriptions().size()).append("</td></tr>");
        for (Subscriber subscriber : subscriptionData.getSubscribers()) {
            html.append("<tr><td>").append(subscriber.getDeviceToken()).append("</td>").append("<td>"
                    + subscriber.getSubscriptions().size() + "</td>").append("</tr>");
        }

        html.append("</table>");

        return html.toString();
    }
}
