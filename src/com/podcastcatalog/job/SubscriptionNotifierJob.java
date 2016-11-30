package com.podcastcatalog.job;

import com.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.api.response.PodCastEpisode;
import com.podcastcatalog.builder.collector.PodCastFeedParser;
import com.podcastcatalog.builder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.subscribe.PodCastSubscriptionService;
import com.podcastcatalog.subscribe.Subscription;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class SubscriptionNotifierJob implements Job {

    private final static Logger LOG = Logger.getLogger(SubscriptionNotifierJob.class.getName());

    public void doWork() {

        List<Subscription> subscriptions = PodCastSubscriptionService.getInstance().getSubscriptions();

        if (!subscriptions.isEmpty()) {
            LOG.info(getClass().getSimpleName() + " looking for PodCastEpisde updates. subscriptions=" + subscriptions.size());
        }

        for (Subscription subscription : subscriptions) {
            PodCast podCast = getPodCast(subscription.getContentId());

            if (podCast == null) {
                LOG.warning("podCast is null for contentId=" + subscription.getContentId());
                continue;
            }

            Optional<PodCastEpisode> remoteLatestOptional = PodCastFeedParser.parseLatestPodCastEpisode(podCast);

            if (!remoteLatestOptional.isPresent()) {
                LOG.warning("latestRemotePodCast is null for contentId=" + subscription.getContentId());
                continue;
            }

            PodCastEpisode remoteLatest = remoteLatestOptional.get();
            PodCastEpisode inMemoryLatest = podCast.getLatestPodCastEpisode();

            if (remoteLatest.getCreatedDate().isAfter(inMemoryLatest.getCreatedDate())) {
                //Ok, later episode than in-memoru catalog version exist...
                String remoteLatestId = remoteLatest.getId();

                if (!remoteLatestId.equals(inMemoryLatest.getId()) && subscription.isNotAlreadyPushed(remoteLatestId)) {
                    //push message and update subscriptionPushDate //FIXME format message
                    PodCastSubscriptionService.getInstance().pushMessage("New Episode " + remoteLatest.getTitle(),
                            subscription.getContentId());
                }
            }
        }
    }

    private PodCast getPodCast(String podCastId) {

        Optional<PodCast> castById = PodCastCatalogService.getInstance().getPodCastById(podCastId);
        if (castById.isPresent()) {
            return castById.get();
        }

        Optional<PodCast> podCastOptional = ItunesSearchAPI.lookup(podCastId);
        if (podCastOptional.isPresent()) {
            return podCastOptional.get();
        }

        return null;
    }
}
