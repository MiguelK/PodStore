package com.podcastcatalog.model.subscription;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Subscription {

    private String latestPushedPodCastEpisodeId;
    private LocalDateTime latestPushedDateTime;
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
}
