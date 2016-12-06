package com.podcastcatalog.model.subscription;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Subscription {

    private String latestPushedPodCastEpisodeId;
    private LocalDateTime latestPushedDateTime;
    private String contentId;

    //All user Subscribers for this content
    private List<Subscriber> subscribers = new ArrayList<Subscriber>();

    public Subscription(String contentId) {
        if(StringUtils.isEmpty(contentId)){
            throw  new IllegalArgumentException("contentId is mandatory");
        }
        this.contentId = StringUtils.trimToNull(contentId);
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
    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public LocalDateTime getLatestPushedDateTime() {
        //FIXME
        return latestPushedDateTime;
    }

    public boolean isNotAlreadyPushed(String id){
        return !StringUtils.trimToEmpty(latestPushedPodCastEpisodeId).equals(StringUtils.trimToEmpty(id));
    }

    public void setLatestPushedDateTime(LocalDateTime latestPushedDateTime) {
        this.latestPushedDateTime = latestPushedDateTime;
    }

    public boolean hasSentPushMessage(){
        return  latestPushedDateTime!=null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscription that = (Subscription) o;

        return contentId.equals(that.contentId);

    }

    @Override
    public int hashCode() {
        return contentId.hashCode();
    }

    public boolean hasNoSubscribers() {
        return subscribers.isEmpty();
    }
}
