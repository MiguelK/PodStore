package com.podcastcatalog.model.subscription;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Data only serializabe to disk
public class SubscriptionData implements Serializable {

    private final Map<String, Subscriber> subscriberByDeviceToken = new HashMap<>();
    private final Map<String, Subscription> subscriptionByPodCastId = new HashMap<>();

    public SubscriptionData() {
    }

    public void unSubscribe(String deviceToken, String podCastId) {
        String podCastIdTrimmed = StringUtils.trimToNull(podCastId);

        Subscriber subscriber = subscriberByDeviceToken.get(podCastIdTrimmed);

        if (subscriber == null) {
            return;
        }

        Subscription subscription = subscriptionByPodCastId.get(podCastIdTrimmed);

        if (subscription == null) {
            return;
        }

        subscription.removeSubscriber(subscriber);
        subscriber.removeSubscription(subscription);

        if (subscription.hasNoSubscribers()) {
            subscriptionByPodCastId.remove(podCastIdTrimmed);
        }
    }

    public void deleteSubscriber(String deviceToken) {

        subscriberByDeviceToken.remove(StringUtils.trimToEmpty(deviceToken));
    }

    public Subscriber getSubscriber(String deviceToken) {
        return subscriberByDeviceToken.get(StringUtils.trimToEmpty(deviceToken));
    }

    public void registerSubscriber(Subscriber subscriber) {
        subscriberByDeviceToken.put(subscriber.getDeviceToken(), subscriber);
    }

    public Subscription getSubscription(String podCastId) {
        return subscriptionByPodCastId.get(StringUtils.trimToEmpty(podCastId));
    }

    public void addSubscription(Subscription subscription) {
        subscriptionByPodCastId.put(subscription.getPodCastId(), subscription);
    }

    public List<Subscriber> getSubscribers() {
        return new ArrayList<>(subscriberByDeviceToken.values());
    }

    public List<Subscription> getSubscriptions() {
        return new ArrayList<>(subscriptionByPodCastId.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubscriptionData that = (SubscriptionData) o;

        if (!subscriberByDeviceToken.equals(that.subscriberByDeviceToken)) return false;
        return subscriptionByPodCastId.equals(that.subscriptionByPodCastId);
    }

    @Override
    public int hashCode() {
        int result = subscriberByDeviceToken.hashCode();
        result = 31 * result + subscriptionByPodCastId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SubscriptionData{" +
                "subscriberByDeviceToken=" + subscriberByDeviceToken +
                ", subscriptionByPodCastId=" + subscriptionByPodCastId +
                '}';
    }
}
