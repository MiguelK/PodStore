package com.podcastcatalog.service.job;

import com.podcastcatalog.model.subscription.Subscriber;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.modelbuilder.collector.PodCastFeedParser;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.service.subscription.PodCastSubscriptionService;
import com.podcastcatalog.model.subscription.Subscription;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class SubscriptionNotifierJob implements Job {

    private final static Logger LOG = Logger.getLogger(SubscriptionNotifierJob.class.getName());
    static final String PUSH_PAYLOAD_NEW_LINE = "\\r\\n";

    public void doWork() {
        List<Subscription> subscriptions = PodCastSubscriptionService.getInstance().getSubscriptions();

        if (!subscriptions.isEmpty()) {
            LOG.info(getClass().getSimpleName() +
                    " looking for PodCastEpisode updates. subscriptions=" + subscriptions.size());
        }

        //Loop all PodCasts that anyone subscribes to
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

       //FIXME test     if ( !latestRemote.equals(latestPodCastEpisodeId) ) {
            LOG.info("PUSH message to ..." + subscription.getSubscribers().size() + " subscribers" );

                for (Subscriber subscriber : subscription.getSubscribers()) {
                    LOG.info("PUSH to this subscriber?");
                        String title = podCast.getTitle();
                      PodCastSubscriptionService.getInstance().pushMessage(title, "Server body", subscriber.getDeviceToken());
                }
            //}

            //Get all subscribers for this PodCast



            //latestRemote.getId()

            /*PodCastEpisode latestInMemory = podCast.getLatestPodCastEpisode();

            if (latestRemote.getCreatedDate().isAfter(latestInMemory.getCreatedDate())) {
                //Ok, later episode than in-memoru catalog version exist...
                String remoteLatestId = latestRemote.getId();

                if (!remoteLatestId.equals(latestInMemory.getId()) && subscription.isNotAlreadyPushed(remoteLatestId)) {
                    //push message and update subscriptionPushDate //FIXME format message. lang?
                    pushMessage(podCast.getTitle() + PUSH_PAYLOAD_NEW_LINE + " Nytt avsnitt: " + latestRemote.getTitle(), subscription.getPodCastId());
                }
            }*/
        }
    }


    PodCast getPodCast(String podCastId) {

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
