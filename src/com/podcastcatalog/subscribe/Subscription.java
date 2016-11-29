package com.podcastcatalog.subscribe;

import com.podcastcatalog.subscribe.internal.Subscriber;

import java.util.ArrayList;
import java.util.List;

public class Subscription {

    private String contentId;

    private List<Subscriber> subscribers = new ArrayList<Subscriber>();

    public Subscription(String contentId) {
        this.contentId = contentId;
    }

    public String getContentId() {
        return contentId;
    }

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }
}
