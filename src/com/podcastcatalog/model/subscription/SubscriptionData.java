package com.podcastcatalog.model.subscription;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Data only serializabe to disk
public class SubscriptionData implements Serializable {

    private Map<String, Subscriber> subscriberById = new HashMap<>();
    private Map<String, Subscription> subscriptionByContentId = new HashMap<>();


    public SubscriptionData() {
    }

    public void subscribe(String deviceToken, String contentId, ContentIdValidator contentIdValidator) {
        throw new UnsupportedOperationException();
    }

    public void unsubscribe(String subscriptionId, String contentId) {

        String subscriptionIdTrimmed = StringUtils.trimToNull(subscriptionId);

        Subscriber subscriber = subscriberById.get(subscriptionIdTrimmed);

        if (subscriber == null) {
            return;
        }
//
        String contentIdTrimmed = StringUtils.trimToNull(contentId);
        Subscription subscription = subscriptionByContentId.get(contentIdTrimmed);

        if (subscription == null) {
            return;
        }

        subscription.removeSubscriber(subscriber);
        subscriber.removeSubscription(subscription);

        if (subscription.hasNoSubscribers()) {
            subscriptionByContentId.remove(contentIdTrimmed);

        }


    }

    public void deleteSubscriber(String deviceToken) {
        subscriberById.remove(deviceToken);
    }

    public Subscriber getSubscriber(String id) {
        return subscriberById.get(id);
    }

    public void registerSubscriber(Subscriber subscriber) {
        subscriberById.put(subscriber.getDeviceToken(), subscriber);
    }

    public Subscription getSubscription(String contentId) {
        return subscriptionByContentId.get(contentId);
    }

    public void addSubscription(Subscription subscription) {
        subscriptionByContentId.put(subscription.getContentId(), subscription);
    }

    public List<Subscriber> getSubscribers() {
        return new ArrayList<>(subscriberById.values());
    }

    public List<Subscription> getSubscriptions() {
        return new ArrayList<>(subscriptionByContentId.values());
    }
}
