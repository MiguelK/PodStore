package com.podcastcatalog.service.job;

import com.podcastcatalog.model.subscription.Subscriber;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.modelbuilder.collector.PodCastFeedParser;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.service.subscription.PodCastSubscriptionService;
import com.podcastcatalog.model.subscription.Subscription;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class SubscriptionNotifierJob implements Job {

    private final static Logger LOG = Logger.getLogger(SubscriptionNotifierJob.class.getName());
    static final String PUSH_PAYLOAD_NEW_LINE = "\\r\\n";

    private final boolean testMode = true;

    public void doWork() {

        if(!PodCastSubscriptionService.getInstance().isSubscribersLoaded()) {
            return;
        }

        try {
            List<Subscription> subscriptions = PodCastSubscriptionService.getInstance().getSubscriptions();

            if (!subscriptions.isEmpty()) {
                LOG.info(getClass().getSimpleName() +
                        " looking for PodCastEpisode updates. subscriptions=" + subscriptions.size());
            }

            //Loop all PodCasts that anyone subscribes to
            boolean isDirty = false;
            for (Subscription subscription : subscriptions) {
                PodCast podCast = getPodCast(subscription.getPodCastId());

                if (podCast == null) {
                    LOG.warning("podCast is null for contentId=" + subscription.getPodCastId());
                    continue;
                }

                Optional<PodCastEpisode> latestPodCastEpisode = getLatestPodCastEpisodeFromSourceServer(podCast);

                if (!latestPodCastEpisode.isPresent()) {
                    LOG.warning("latestRemotePodCast is null for contentId=" + subscription.getPodCastId());
                    continue;
                }

                //Can subscribe to pods not in-memory Chines, German etc
                PodCastEpisode latestRemote = latestPodCastEpisode.get();
                String latestPodCastEpisodeId = subscription.getLatestPodCastEpisodeId();

                if (latestPodCastEpisodeId == null) {
                    subscription.setLatestPodCastEpisodeId(latestRemote.getId());
                    isDirty = true;
                }


                if (testMode) {
                    LOG.info("PUSH message to ..." + subscription.getSubscribers().size() + " subscribers");
                    isDirty = true;
                    sendPushMessage(subscription.getSubscribers(), podCast, latestRemote);
                } else {
                    if (!subscription.getLatestPodCastEpisodeId().equals(latestRemote.getId())) {
                        isDirty = true;
                        LOG.info("Prod PUSH message to ..." + subscription.getSubscribers().size() + " subscribers");
                        sendPushMessage(subscription.getSubscribers(), podCast, latestRemote);
                    }
                }
            }

            if (isDirty) {
                LOG.info("Saving and uploading to one.com");
                PodCastSubscriptionService.getInstance().uploadToOneCom();
            }

        } catch (Exception e) {
            LOG.info("Failed push message" + e.getMessage());
        }


    }

    private void sendPushMessage(List<Subscriber> subscribers, PodCast podCast, PodCastEpisode latestRemote) {
        for (Subscriber subscriber : subscribers) {
              LOG.info("PUSH to this subscriber " + podCast.getTitle() + ", episode=" + latestRemote.getTitle());
              String title = latestRemote.getTitle();
              String description = latestRemote.getDescription();
              PodCastSubscriptionService.getInstance().
                      pushMessage(title, podCast.getTitle(),latestRemote.getPodCastCollectionId(), description,
                              latestRemote.getId(),  subscriber.getDeviceToken());
        }
    }


    private PodCast getPodCast(String podCastId) {

        Optional<PodCast> castById = PodCastCatalogService.getInstance().getPodCastById(podCastId);
        if (castById.isPresent()) {
            return castById.get();
        }

        Optional<PodCast> podCastOptional = ItunesSearchAPI.lookupPodCast(podCastId);
        return podCastOptional.orElse(null);

    }

    Optional<PodCastEpisode> getLatestPodCastEpisodeFromSourceServer(PodCast podCast) {
        return PodCastFeedParser.parseLatestPodCastEpisode(podCast);
    }
}
