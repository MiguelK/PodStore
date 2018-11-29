package com.podcastcatalog.model.subscription;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Data only serializabe to disk
public class SubscriptionData implements Serializable {

   // private final Map<String, Subscriber> subscriberByDeviceToken = new HashMap<>();
    private final Map<String, Subscription> subscriptionByPodCastId = new HashMap<>();


    //Subscriber ->* Subscription

    public SubscriptionData() {
    }

    public void unSubscribe(String deviceToken, String podCastId) {

        //Subscriber subscriber = new Subscriber(deviceToken); //subscriberByDeviceToken.get(deviceToken);

        /*if (subscriber == null) {
            return;
        }*/

        Subscription subscription = subscriptionByPodCastId.get( podCastId);

        if (subscription == null) {
            return;
        }

        subscription.removeSubscriber(deviceToken);
        //subscriber.removeSubscription(subscription);

        if (subscription.hasNoSubscribers()) {
            subscriptionByPodCastId.remove(podCastId);
        }

        /*if (subscriber.getSubscriptions().isEmpty()) {
            subscriberByDeviceToken.remove(deviceToken);
        }*/
    }

   /* public void deleteSubscriber(String deviceToken) {
        subscriberByDeviceToken.remove(StringUtils.trimToEmpty(deviceToken));
    }*/

    /*public Subscriber getSubscriber(String deviceToken) {
        return subscriberByDeviceToken.get(StringUtils.trimToEmpty(deviceToken));
    }*/

  /*  public void registerSubscriber(Subscriber subscriber) {
        subscriberByDeviceToken.put(subscriber.getDeviceToken(), subscriber);
    }*/

    public Subscription getSubscription(String podCastId) {
        return subscriptionByPodCastId.get(StringUtils.trimToEmpty(podCastId));
    }

    public void addSubscription(Subscription subscription) {
        subscriptionByPodCastId.put(subscription.getPodCastId(), subscription);
    }

    public List<Subscription> getSubscriptions() {
        return new ArrayList<>(subscriptionByPodCastId.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubscriptionData that = (SubscriptionData) o;

        return subscriptionByPodCastId.equals(that.subscriptionByPodCastId);
    }

    @Override
    public int hashCode() {
        return subscriptionByPodCastId.hashCode();
    }

    @Override
    public String toString() {
        return "SubscriptionData{ (" + subscriptionByPodCastId.size() + ") " +
                ", subscriptionByPodCastId=" + subscriptionByPodCastId +
                '}';
    }
}
