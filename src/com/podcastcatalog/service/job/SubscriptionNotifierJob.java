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
import com.podcastcatalog.util.ServerInfo;
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

        if (!PodCastSubscriptionService.getInstance().isSubscribersLoaded()) {
            LOG.info("SubscriptionNotifierJob isSubscribersLoaded=false.");
            return;
        }

        if (ServerInfo.isLocalDevMode()) {
            LOG.info("No PUSH Local dev mode...");
            return;
        }


        List<Subscription> subscriptions = PodCastSubscriptionService.getInstance().getSubscriptions();

        if (!subscriptions.isEmpty()) {
            LOG.info(getClass().getSimpleName() +
                    " SubscriptionNotifierJob doWork(), subscriptions=" + subscriptions.size());
        }


        int pushSent = 0;
        for (Subscription subscription : subscriptions) {
            try {

                Thread.sleep(800); //Decrease pressure on ItunesSearchAPI
                String podCastId = subscription.getPodCastId();
                ItunesSearchAPI.PodCastSmall podCastSmall = ItunesSearchAPI.getLatestEpisodeIdForPodCast(podCastId);

                if (podCastSmall == null) {
                    LOG.warning("podCast is null for contentId=" + subscription.getPodCastId());
                    continue;
                }

                //Do not push first time added
                if (subscription.getLatestPodCastEpisodeId() == null) {
                    LOG.info("Updating Subscription for first time " + podCastId + ", episode=" + podCastSmall.getLatestPodCastEpisodeId());
                    PodCastSubscriptionService.getInstance().update(podCastId, podCastSmall.getLatestPodCastEpisodeId());
                    continue;
                }

                boolean isEpisodeUpdated = !podCastSmall.getLatestPodCastEpisodeId().equals(subscription.getLatestPodCastEpisodeId());
                if (isEpisodeUpdated) {

                    PodCastSubscriptionService.getInstance().update(podCastId, podCastSmall.getLatestPodCastEpisodeId());
                    PodCastSubscriptionService.getInstance().uploadToOneCom();

                    sendPushMessage(subscription.getSubscribers(), podCastSmall);

                    pushSent++;

                    LOG.info("PUSH: (" + pushSent + ") podCast=" + podCastSmall.getPodCastTitle()
                            + ",subscribers=" + subscription.getSubscribers().size() + ", latest Episode=" + podCastSmall.getLatestPodCastEpisodeId());

                }
            } catch (Exception e) {
                LOG.info(subscription.getPodCastId() + ": " + subscription.getSubscribers().size() + ": " + subscription.getLatestPodCastEpisodeId()
                        + " Failed push message" + e.getMessage());
            }
        }

        LOG.info("SubscriptionNotifierJob done, Subscription " + subscriptions.size() + " " + pushSent + " push messages sent");

    }

    private void debug(List<Subscription> subscriptions) {


        Set<String> pods = new HashSet<>();
        for (Subscription subscription : subscriptions) {
            if (pods.contains(subscription.getPodCastId())) {
                LOG.info("Duplicate " + subscription.getPodCastId() + " subs=" + subscription.getSubscribers().size());
            }
            pods.add(subscription.getPodCastId());
            if (subscription.getSubscribers().size() > 1) {
                LOG.info("Subscribers " + subscription.getSubscribers().size());
            }
        }
    }

    private void sendPushMessage(List<String> subscribers, ItunesSearchAPI.PodCastSmall podCastSmall) {

        for (String deviceToken : subscribers) {
            String podCastEpisodeTitle = podCastSmall.getPodCastEpisodeTitle();
            String description = podCastSmall.getPodCastEpisodeDescription();
            String podCastEpisodeInfo = "";

            PodCastEpisodeDuration duration = podCastSmall.getPodCastEpisodeDuration();
            if (duration != null) {
                podCastEpisodeInfo = duration.getDisplayValue();
            }

            try {
                PodCastSubscriptionService.getInstance().
                        pushMessage(podCastSmall.getPodCastTitle(), podCastEpisodeTitle,
                                description, podCastEpisodeInfo, podCastSmall.getPodCastPid(),
                                podCastSmall.getLatestPodCastEpisodeId(), deviceToken);
            } catch (Exception e) {
                LOG.info("Failed sendPushMessage" + e.getMessage());
            }
        }
    }
}
