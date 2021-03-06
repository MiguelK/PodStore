package com.podcastcatalog.service.job;

import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeDuration;
import com.podcastcatalog.model.subscription.Subscription;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.service.subscription.PodCastSubscriptionService;
import com.podcastcatalog.util.ServerInfo;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PushSubscriptionsJob implements Job {

    private final static Logger LOG = Logger.getLogger(PushSubscriptionsJob.class.getName());

    private ExecutorService threadPool = Executors.newFixedThreadPool(20);

    private LocalDateTime created = LocalDateTime.now();

    public void doWork() {
        if (ServerInfo.isLocalDevMode()) {
            LOG.info("PushSubscriptionsJob: No PUSH Local dev mode...");
            return;
        }

        try {
            if (!isReady()) {
                LOG.info("PushSubscriptionsJob is not ready yet...");
                Thread.sleep(60_000);
                doWork();
            }


            //Copy FIXME
            List<Subscription> subscriptions = new ArrayList<>(PodCastSubscriptionService.getInstance().getSubscriptions());

            LOG.info("PushSubscriptionsJob 1: subscriptions=" + subscriptions.size());
            List<FetchLatestPodCastEpisodeResult> fetchResults = createFetchLatestPodCastEpisodeTasks(subscriptions);

            if(fetchResults.isEmpty()) {
                LOG.info("PushSubscriptionsJob 1_4 : fetchResults was empty, wait 10min");
                Thread.sleep(1000 * 60 * 10); //Rerun after 10min
                doWork();
                return;
            }

            List<FetchLatestPodCastEpisodeResult> newSubscriptions = new ArrayList<>();
            List<FetchLatestPodCastEpisodeResult> pushToSubscribers = new ArrayList<>();
            for (FetchLatestPodCastEpisodeResult feedPayLoad : fetchResults) {
                if (feedPayLoad.shouldSendPush) {
                    pushToSubscribers.add(feedPayLoad);
                } else if (feedPayLoad.isNewSubscription) {
                    newSubscriptions.add(feedPayLoad);
                }
            }

            LOG.info("PushSubscriptionsJob 2: newSubscriptions=" + newSubscriptions.size() +
                    ", pushToSend=" + pushToSubscribers.size());

            for (FetchLatestPodCastEpisodeResult result : pushToSubscribers) {
                sendPushMessage(result.subscribers, result.podCastSmall);
                PodCastSubscriptionService.getInstance().update(result.podCastId,
                        result.podCastSmall.getLatestPodCastEpisodeId());
            }
            LOG.info("PushSubscriptionsJob 3: Sent =" + pushToSubscribers.size() + " Push messages");

            PodCastSubscriptionService.getInstance().uploadToOneCom();

            for (FetchLatestPodCastEpisodeResult newSubscription : newSubscriptions) {
                Thread.sleep(5000); //Lower pressure on Apple itunes.apple.com/lookup
                URL podCastFeedURL = ItunesSearchAPI.getFeedURLFromPodCast(newSubscription.podCastId);
                if (podCastFeedURL != null) {
                    PodCastSubscriptionService.getInstance().updateFeedURL(newSubscription.podCastId, podCastFeedURL);
                }

                PodCastSubscriptionService.getInstance().update(newSubscription.podCastId,
                        newSubscription.podCastSmall.getLatestPodCastEpisodeId());
            }

            if(!newSubscriptions.isEmpty()) {
                PodCastSubscriptionService.getInstance().uploadToOneCom();
            }

            LOG.info("PushSubscriptionsJob 3 done");

            Thread.sleep(1000 * 60 * 30); //Rerun after 30min
            doWork();
        } catch (Exception e) {
            LOG.warning("PushSubscriptionsJob Failed: " + e.getMessage() + ", retry in 1h");
            try {
                Thread.sleep(1000 * 60 * 10); //Rerun after 10min
            } catch (InterruptedException e1) {
                //Ignore
            }
            doWork(); //FIXME Retry if errorF
        }
    }

    private List<FetchLatestPodCastEpisodeResult> createFetchLatestPodCastEpisodeTasks(List<Subscription> subscriptions) {
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

        LOG.info("PushSubscriptionsJob 1_1 tasks=" + tasks.size());

        List<Future<FetchLatestPodCastEpisodeResult>> taskFeatures = new ArrayList<>();
        for (FetchLatestPodCastEpisodeTask task : tasks) {
            try {
                taskFeatures.add(threadPool.submit(task));
            } catch (Exception e) {
                LOG.info("Failed to submit task " + e.getMessage());
            }
        }

        LOG.info("PushSubscriptionsJob 1_2 tasks join all=" + tasks.size());
        List<FetchLatestPodCastEpisodeResult> payLoads = new ArrayList<>();

        int failCount = 0;
        int success = 0;
        long start = 0;
        for (Future<FetchLatestPodCastEpisodeResult> taskFeature : taskFeatures) {
            FetchLatestPodCastEpisodeResult payLoad;

            try {
                if(success == 0 && failCount == 0) {
                    start = System.currentTimeMillis();
                    LOG.info("Start task.get()="  + success);
                }
                payLoad = taskFeature.get(60, TimeUnit.SECONDS);
                if(success == 0 && failCount == 0) {
                    long elapsedTime =  System.currentTimeMillis() - start;
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime);
                    LOG.info("End task.get()="  + success + ", elapsedTime in seconds =" + seconds);
                }
            } catch (Exception e) {
                failCount++;
                if(failCount % 200 == 0) {
                    LOG.info("Failed to get task failCount=" + failCount + ", error=" + e.getMessage());
                }

                //Crash restart if needed
                if(failCount >= 200) {
                    long diffHours = Math.abs(Duration.between(created, LocalDateTime.now()).toHours());
                    if(diffHours >= 24) {
                        LocalTime timeNow = LocalTime.now();
                        if (timeNow.isAfter(LocalTime.of(0, 1)) && (timeNow.isBefore(LocalTime.of(6, 30))))
                        {
                            LOG.info("Crash Restarting Pods Server...");
                            System.exit(0);
                        }
                    }
                }

                continue;
            }
            if (payLoad != null) {
                success++;
                payLoads.add(payLoad);
            }
            if(success % 200 == 0) {
                long elapsedTime =  System.currentTimeMillis() - start;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime);
                LOG.info("success="  + success+ ", elapsedTime in seconds =" + seconds);
            }
        }
        LOG.info("PushSubscriptionsJob 1_3 tasks payLoads=" + payLoads.size());

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
                LOG.info("Failed sendPushMessage" + e.getMessage() +
                        ", PodCastPid=" + podCastSmall.getPodCastPid() +
                        ",subscribers=" + subscribers.size());
                try {
                    Thread.sleep(5000); //Wait 30sec try again...
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private class FetchLatestPodCastEpisodeTask implements Callable<FetchLatestPodCastEpisodeResult> {

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
        public FetchLatestPodCastEpisodeResult call() throws IOException {
            try {
                ItunesSearchAPI.PodCastSmall podCastSmall = ItunesSearchAPI.getLatestEpisodeIdFromPodCast(podCastId, feedURL);
                if (podCastSmall == null) {
                    return null;
                }

                boolean isNewSubscription = getLatestPodCastEpisodeId == null;
                boolean isEpisodeUpdated = !podCastSmall.getLatestPodCastEpisodeId().equals(getLatestPodCastEpisodeId);

                return new FetchLatestPodCastEpisodeResult(podCastSmall,
                        podCastId,subscribers,isEpisodeUpdated,isNewSubscription);
            } catch (Exception e) {
                LOG.info("Failed FetchLatestPodCastEpisodeResult" + e.getMessage()
                        + ", podCastId=" + podCastId + ", feedURL=" + feedURL);
                throw new IOException(e);
            }
        }
    }

    private class FetchLatestPodCastEpisodeResult {
        private ItunesSearchAPI.PodCastSmall podCastSmall;
        private String podCastId;
        private List<String> subscribers;
        private boolean shouldSendPush;
        private boolean isNewSubscription;

        FetchLatestPodCastEpisodeResult(ItunesSearchAPI.PodCastSmall podCastSmall,
                                        String podCastId, List<String> subscribers,
                                        boolean shouldSendPush, boolean isNewSubscription) {
            this.podCastSmall = podCastSmall;
            this.podCastId = podCastId;
            this.subscribers = subscribers;
            this.shouldSendPush = shouldSendPush;
            this.isNewSubscription = isNewSubscription;
        }
    }
}

