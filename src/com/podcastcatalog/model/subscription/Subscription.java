package com.podcastcatalog.model.subscription;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Subscription {

    private String latestPodCastEpisodeId;
    private String podCastId;

    //All user Subscribers for this content
    private final List<Subscriber> subscribers = new ArrayList<>();

    public Subscription(String podCastId) {
        if(StringUtils.isEmpty(podCastId)){
            throw  new IllegalArgumentException("podCastId is mandatory");
        }
        this.podCastId = StringUtils.trimToNull(podCastId);
    }

    public String getLatestPodCastEpisodeId() {
        return latestPodCastEpisodeId;
    }

    public void setLatestPodCastEpisodeId(String latestPodCastEpisodeId) {
        this.latestPodCastEpisodeId = latestPodCastEpisodeId;
    }

    public String getPodCastId() {
        return podCastId;
    }

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription that = (Subscription) o;

        return podCastId.equals(that.podCastId);

    }

    @Override
    public int hashCode() {
        return podCastId.hashCode();
    }

    public boolean hasNoSubscribers() {
        return subscribers.isEmpty();
    }
}
