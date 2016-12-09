package com.podcastcatalog.service.subscription;

import com.podcastcatalog.model.subscription.ContentIdValidator;
import com.podcastcatalog.model.subscription.Subscriber;
import com.podcastcatalog.model.subscription.Subscription;
import com.podcastcatalog.model.subscription.SubscriptionData;
import com.podcastcatalog.service.ServiceDataStorage;
import org.apache.commons.lang3.StringUtils;

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
        subscriptionData = ServiceDataStorage.useDefault().loadSubscriptionData();
    }

    public void subscribe(String subscriberId, String contentId, ContentIdValidator contentIdValidator) {

        if (!contentIdValidator.isValidContentId(contentId)) {
            throw new IllegalArgumentException("Invalid contentId " + contentId + " not accepted by " + contentIdValidator);
        }

        writeLock.lock();

        Subscriber subscriber = subscriptionData.getSubscriber(subscriberId);
        Subscription subscription = subscriptionData.getSubscription(contentId);

        try {
            //Ok forts subscriber for this content
            if (subscription == null) {
                subscription = new Subscription(contentId);
                subscriptionData.addSubscription(subscription);
            }

            subscriber.addSubscription(subscription);
            subscription.addSubscriber(subscriber);
        } finally {
            writeLock.unlock();
        }
    }

    public void unSubscribe(String deviceToken, String contentId) {

        writeLock.lock();
        try {
            subscriptionData.unsubscribe(deviceToken, contentId);
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

    private Optional<Subscription> getSubscription(String contentId) {
        readLock.lock();
        try {
            return Optional.ofNullable(subscriptionData.getSubscription(StringUtils.trimToEmpty(contentId)));
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

    public void pushMessage(String message, String contentId) {
        //FIXME add to queu
        //FIXME Update pushDateTime
        Optional<Subscription> subscription = getSubscription(contentId);
        if (subscription.isPresent()) {
            LOG.info("Send push message " + message + " to contentId=" + subscription.get());
        }

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
}
