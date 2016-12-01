package com.podcastcatalog.model.subscription;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Subscriber implements Serializable{

    private String deviceToken;

    private List<Subscription> subscriptions = new ArrayList<Subscription>();

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
}
