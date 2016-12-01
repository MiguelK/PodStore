package com.podcastcatalog.model.subscription;

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
    }

    public void unsubscribe(String deviceToken, String contentId) {
    }

    public void deleteSubscriber(String deviceToken) {
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
