package com.podcastcatalog.model.subscription;

import com.podcastcatalog.model.subscription.Subscription;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Subscriber implements Serializable{

    private String deviceToken;

    private List<Subscription> subscriptions = new ArrayList<Subscription>();

    public Subscriber(String deviceToken) {
        this.deviceToken = deviceToken;
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
}
