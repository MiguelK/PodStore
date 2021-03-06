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

import java.net.URL;
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
                    " SubscriptionNotifierJob_start, subscriptions=" + subscriptions.size());
        }


        int pushSent = 0;
        int subscriptionCount = 0;
        for (Subscription subscription : subscriptions)

            try {
                subscriptionCount++;
                if ((subscriptionCount % 40) == 0) {
                 //   Thread.sleep(2000); //Decrease pressure on ItunesSearchAPI

                    LOG.info("subscriptionCount=" + subscriptionCount);
                }
                String podCastId = subscription.getPodCastId();

                URL feedURL = subscription.getFeedURL();
                //if (feedURL == null) {
                //    feedURL = ItunesSearchAPI.getFeedURLFromPodCast(podCastId);
                //  subscription.setFeedURL(feedURL); //Save and later upload to one.com as cache
                //}

                if (feedURL == null) {
                    continue;
                }

                Thread.sleep(3000);

                ItunesSearchAPI.PodCastSmall podCastSmall = ItunesSearchAPI.getLatestEpisodeIdFromPodCast(podCastId, feedURL);
                if (podCastSmall == null) {
                    continue;
                }

                //Do not push first time added
                if (subscription.getLatestPodCastEpisodeId() == null) {
                    LOG.info("SubscriptionNotifierJob_update Subscription for first time " + podCastId + ", episode=" + podCastSmall.getLatestPodCastEpisodeId());

                    URL podCastFeedURL = ItunesSearchAPI.getFeedURLFromPodCast(podCastId); //Try fetch PodCast feed
                    LOG.info("SubscriptionNotifierJob_update Subscription for first time " + podCastId + ", podCastFeedURL=" + podCastFeedURL);

                    subscription.setFeedURL(podCastFeedURL); //Save and later upload to one.com as cache

                    PodCastSubscriptionService.getInstance().update(podCastId, podCastSmall.getLatestPodCastEpisodeId());
                    continue;
                }

                boolean isEpisodeUpdated = !podCastSmall.getLatestPodCastEpisodeId().equals(subscription.getLatestPodCastEpisodeId());
                if (isEpisodeUpdated) {
                    PodCastSubscriptionService.getInstance().update(podCastId, podCastSmall.getLatestPodCastEpisodeId());

                    sendPushMessage(subscription.getSubscribers(), podCastSmall);

                    pushSent++;

                    if (pushSent < 5 || (pushSent % 40) == 0) {
                        LOG.info("SubscriptionNotifierJob_PUSH sent: (" + pushSent + ") podCast=" + podCastSmall.getPodCastTitle()
                                + ",subscribers=" + subscription.getSubscribers().size() + ", latest Episode=" + podCastSmall.getLatestPodCastEpisodeId());

                    }

                }
            } catch (Exception e) {
                LOG.info(subscription.getPodCastId() + ": " + subscription.getSubscribers().size() + ": " + subscription.getLatestPodCastEpisodeId()
                        + " Failed push message" + e.getMessage());
            }

        LOG.info("SubscriptionNotifierJob_Done, Subscription start uploadToOneCom()");

        PodCastSubscriptionService.getInstance().uploadToOneCom();

        LOG.info("SubscriptionNotifierJob_Done, Subscription " + subscriptions.size() + " " + pushSent + " push messages sent");

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
