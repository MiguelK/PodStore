package com.podcastcatalog.service.job;

import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeDuration;
import com.podcastcatalog.model.subscription.Subscriber;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.modelbuilder.collector.PodCastFeedParser;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.service.subscription.PodCastSubscriptionService;
import com.podcastcatalog.model.subscription.Subscription;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class SubscriptionNotifierJob implements Job {

    private final static Logger LOG = Logger.getLogger(SubscriptionNotifierJob.class.getName());

    public void doWork() {

        if(!PodCastSubscriptionService.getInstance().isSubscribersLoaded()) {
            return;
        }

       // PodCastSubscriptionService.getInstance().recreateIfSubscriptionFileIsDeleted();

        try {
            List<Subscription> subscriptions = PodCastSubscriptionService.getInstance().getSubscriptions();

            if (!subscriptions.isEmpty()) {
                LOG.info(getClass().getSimpleName() +
                        " SubscriptionNotifierJob doWork(), subscriptions=" + subscriptions.size());
            }

            int pushSent = 0;
            for (Subscription subscription : subscriptions) {
                PodCast podCast = getPodCast(subscription.getPodCastId());

                if (podCast == null) {
                    LOG.warning("podCast is null for contentId=" + subscription.getPodCastId());
                    continue;
                }

                PodCastEpisode latestRemoteEpisode = podCast.getLatestPodCastEpisode();

                //Do not push first time added
                if(subscription.getLatestPodCastEpisodeId() == null) {
                    LOG.info("Updating Subscription for first time " + podCast.getTitle() + ", episode=" + latestRemoteEpisode.getId());
                    PodCastSubscriptionService.getInstance().update(podCast.getCollectionId(), latestRemoteEpisode.getId());
                    continue;
                }

                boolean isEpisodeUpdated = !latestRemoteEpisode.getId().equals(subscription.getLatestPodCastEpisodeId());
                if(isEpisodeUpdated) {
                    LOG.info("PUSH: latest=" + subscription.getLatestPodCastEpisodeId()
                            + ",remote=" + latestRemoteEpisode.getId() + ", podCast=" + podCast.getTitle()
                            + ", title=" + latestRemoteEpisode.getTitle() +
                    ",pid=" + latestRemoteEpisode.getPodCastCollectionId() + ", subscribers=" + subscription.getSubscribers().size());

                    sendPushMessage(subscription.getSubscribers(), podCast, latestRemoteEpisode);

                    PodCastSubscriptionService.getInstance().update(podCast.getCollectionId(), latestRemoteEpisode.getId());

                    pushSent++;
                }
            }

            PodCastSubscriptionService.getInstance().uploadToOneCom();

            LOG.info("SubscriptionNotifierJob done, Subscription " + subscriptions.size() + " " + pushSent + " push messages sent");
        } catch (Exception e) {
            LOG.info("Failed push message" + e.getMessage());
        }
    }

    private void debug( List<Subscription> subscriptions) {


        Set<String> pods = new HashSet<>();
        for (Subscription subscription : subscriptions) {
            if(pods.contains(subscription.getPodCastId())){
                LOG.info("Duplicate " + subscription.getPodCastId() + " subs=" + subscription.getSubscribers().size());
            }
            pods.add(subscription.getPodCastId());
            if(subscription.getSubscribers().size() > 1) {
                LOG.info("Subscribers " + subscription.getSubscribers().size());
            }
        }
    }

    private void sendPushMessage(List<String> subscribers, PodCast podCast, PodCastEpisode latestPodCastEpisode) {

        for (String deviceToken : subscribers) {
              String podCastEpisodeTitle = latestPodCastEpisode.getTitle();
              String description = latestPodCastEpisode.getDescription();
              String podCastEpisodeInfo = "";

            PodCastEpisodeDuration duration = latestPodCastEpisode.getDuration();
            if(duration != null) {
                podCastEpisodeInfo = duration.getDisplayValue();
            }

            try {
                  PodCastSubscriptionService.getInstance().
                          pushMessage(podCast.getTitle(), podCastEpisodeTitle,
                                  description, podCastEpisodeInfo, podCast.getCollectionId(),
                                  latestPodCastEpisode.getId(),  deviceToken);
              }catch (Exception e) {
                  LOG.info("Failed sendPushMessage" + e.getMessage());
              }
        }
    }


    private PodCast getPodCast(String podCastId) {
        Optional<PodCast> podCastOptional = ItunesSearchAPI.lookupPodCast(podCastId);
        return podCastOptional.orElse(null);
    }

   /* private Optional<PodCastEpisode> getLatestPodCastEpisodeFromSourceServer(PodCast podCast) {
        return PodCastFeedParser.parseLatestPodCastEpisode(podCast);
    }*/
}
