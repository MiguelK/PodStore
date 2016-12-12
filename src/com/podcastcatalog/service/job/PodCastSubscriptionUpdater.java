package com.podcastcatalog.service.job;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.subscription.Subscription;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.service.subscription.PodCastSubscriptionService;

import java.util.Optional;

public class PodCastSubscriptionUpdater implements Job {

    @Override
    public void doWork() {

        for (Subscription subscription : PodCastSubscriptionService.getInstance().getSubscriptions()) {
            Optional<PodCast> podCast = ItunesSearchAPI.lookup(subscription.getContentId());

            podCast.ifPresent(podCast1 ->
                    PodCastCatalogService.getInstance().updatePodCastIndex(podCast1)
            );
        }
    }
}
