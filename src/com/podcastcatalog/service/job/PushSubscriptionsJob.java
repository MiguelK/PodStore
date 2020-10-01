package com.podcastcatalog.service.job;

import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeDuration;
import com.podcastcatalog.model.subscription.Subscription;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.service.subscription.PodCastSubscriptionService;
import com.podcastcatalog.util.ServerInfo;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PushSubscriptionsJob implements Job {

    private final static Logger LOG = Logger.getLogger(PushSubscriptionsJob.class.getName());

    //Started once...
    public void doWork() {
        if (ServerInfo.isLocalDevMode()) {
            LOG.info("PushSubscriptionsJob: No PUSH Local dev mode...");
            return;
        }

        try {
            if (!isReady()) {
                LOG.info("PushSubscriptionsJob is not ready yet...");
                Thread.sleep(60000);
                doWork();
            }

            //Copy FIXME
            List<Subscription> subscriptions = new ArrayList<>(PodCastSubscriptionService.getInstance().getSubscriptions());

            LOG.info("PushSubscriptionsJob 1: subscriptions=" + subscriptions.size());
            List<FetchLatestPodCastEpisodeResult> fetchResults = createFetchLatestPodCastEpisodeTasks(subscriptions);

            List<FetchLatestPodCastEpisodeResult> newSubscriptions = new ArrayList<>();
            List<FetchLatestPodCastEpisodeResult> pushToSend = new ArrayList<>();
            for (FetchLatestPodCastEpisodeResult feedPayLoad : fetchResults) {
                if (feedPayLoad.shouldSendPush) {
                    pushToSend.add(feedPayLoad);
                } else if (feedPayLoad.isNewSubscription) {
                    newSubscriptions.add(feedPayLoad);
                }
            }

            LOG.info("PushSubscriptionsJob 2: newSubscriptions=" + newSubscriptions.size() +
                    ", pushToSend=" + pushToSend.size());

            //FIXME Update podCastFeedURL + latestEpisode
            for (FetchLatestPodCastEpisodeResult newSubscription : newSubscriptions) {
             /*   URL podCastFeedURL = ItunesSearchAPI.getFeedURLFromPodCast(newSubscription.getPodCastId());
                if (podCastFeedURL != null) {
                    PodCastSubscriptionService.getInstance().updateFeedURL(newSubscription.getPodCastId(), podCastFeedURL);
                }

                PodCastSubscriptionService.getInstance().update(newSubscription.getPodCastId(),
                        newSubscription.podCastSmall.getLatestPodCastEpisodeId());*/
             //FIXME
            }

            //FIXME Sends PUSH Synch
            for (FetchLatestPodCastEpisodeResult result : pushToSend) {
            /*    PodCastSubscriptionService.getInstance().update(result.getPodCastId(),
                        result.podCastSmall.getLatestPodCastEpisodeId());
                sendPushMessage(result.getSubscribers(), result.podCastSmall);
                */
            //FIXME

            }

            //PodCastSubscriptionService.getInstance().uploadToOneCom();

            LOG.info("PushSubscriptionsJob 3 done");

            Thread.sleep(60000 * 60); //Rerun after 1h
            doWork();
        } catch (Exception e) {
            LOG.warning("Failed: " + e.getMessage());
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e1) {
                //Ignore
            }
          //  doWork(); //FIXME Retry if error
        }
    }

    //fetchLatestPodCastEpisodeTask
    public List<FetchLatestPodCastEpisodeResult> createFetchLatestPodCastEpisodeTasks(List<Subscription> subscriptions) throws InterruptedException, java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException {
        List<FetchLatestPodCastEpisodeTask> tasks = new ArrayList<>();
        for (Subscription subscription : subscriptions) {

            URL feedURL = subscription.getFeedURL();
            if (feedURL == null) {
                continue;
            }

            FetchLatestPodCastEpisodeTask task = new FetchLatestPodCastEpisodeTask(subscription.getPodCastId(), feedURL,
                    subscription.getLatestPodCastEpisodeId(), subscription.getSubscribers());
            tasks.add(task);
        }

        Collection<FetchLatestPodCastEpisodeTask> parsePodCastFeedTasks = ForkJoinTask.invokeAll(tasks);
        List<FetchLatestPodCastEpisodeResult> payLoads = new ArrayList<>();
        for (FetchLatestPodCastEpisodeTask task : parsePodCastFeedTasks) {
            FetchLatestPodCastEpisodeResult payLoad = task.get(8, TimeUnit.SECONDS);
            if (payLoad != null) {
                payLoads.add(payLoad);
            }
        }
        return payLoads;
    }

    private boolean isReady() {
        if (!PodCastSubscriptionService.getInstance().isSubscribersLoaded()) {
            return false;
        }

        if (PodCastSubscriptionService.getInstance().getSubscriptions().isEmpty()) {
            return false;
        }
        return true;
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

    private class FetchLatestPodCastEpisodeTask extends RecursiveTask<FetchLatestPodCastEpisodeResult> {

        private String podCastId;
        private URL feedURL;
        private String getLatestPodCastEpisodeId;
        private List<String> subscribers;

        FetchLatestPodCastEpisodeTask(String podCastId, URL feedURL, String getLatestPodCastEpisodeId, List<String> subscribers) {
            this.podCastId = podCastId;
            this.feedURL = feedURL;
            this.getLatestPodCastEpisodeId = getLatestPodCastEpisodeId;
            this.subscribers = subscribers;
        }

        @Override
        protected FetchLatestPodCastEpisodeResult compute() {

            ItunesSearchAPI.PodCastSmall podCastSmall = ItunesSearchAPI.getLatestEpisodeIdFromPodCast(podCastId, feedURL);
            if (podCastSmall == null) {
                return null;
            }

            boolean isNewSubscription = getLatestPodCastEpisodeId == null;
            boolean isEpisodeUpdated = !podCastSmall.getLatestPodCastEpisodeId().equals(getLatestPodCastEpisodeId);

            return new FetchLatestPodCastEpisodeResult(podCastSmall,
                    podCastId,subscribers,isEpisodeUpdated,isNewSubscription);
        }
    }

    private class FetchLatestPodCastEpisodeResult {
        ItunesSearchAPI.PodCastSmall podCastSmall;
        String podCastId;
        List<String> subscribers;
        boolean shouldSendPush;
        boolean isNewSubscription;

        public FetchLatestPodCastEpisodeResult(ItunesSearchAPI.PodCastSmall podCastSmall,
                                               String podCastId, List<String> subscribers,
                                               boolean shouldSendPush, boolean isNewSubscription) {
            this.podCastSmall = podCastSmall;
            this.podCastId = podCastId;
            this.subscribers = subscribers;
            this.shouldSendPush = shouldSendPush;
            this.isNewSubscription = isNewSubscription;
        }

        public ItunesSearchAPI.PodCastSmall getPodCastSmall() {
            return podCastSmall;
        }

        public String getPodCastId() {
            return podCastId;
        }

        public List<String> getSubscribers() {
            return subscribers;
        }

        public boolean isShouldSendPush() {
            return shouldSendPush;
        }

        public boolean isNewSubscription() {
            return isNewSubscription;
        }
    }
}

