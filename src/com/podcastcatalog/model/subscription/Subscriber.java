package com.podcastcatalog.model.subscription;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Subscriber implements Serializable{

    private String deviceToken;

    //This user Subscriber's subscriptions
    private final List<Subscription> subscriptions = new ArrayList<>();

    public Subscriber(String deviceToken) {
        String deviceTokenTrimmed = StringUtils.trimToNull(deviceToken);
        if (deviceTokenTrimmed == null) {
            throw new IllegalArgumentException("deviceToken is empty");
        }
        this.deviceToken = deviceTokenTrimmed;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void addSubscription(Subscription subscription){
        subscriptions.add(subscription);
    }

    public void removeSubscription(Subscription subscription){
        subscriptions.remove(subscription);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscriber that = (Subscriber) o;

        return deviceToken.equals(that.deviceToken);

    }

    @Override
    public int hashCode() {
        return deviceToken.hashCode();
    }
}
