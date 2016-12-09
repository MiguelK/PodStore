package com.podcastcatalog.service.job;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeTest;
import com.podcastcatalog.model.podcastcatalog.PodCastTest;
import com.podcastcatalog.model.subscription.Subscription;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.podcastcatalog.service.job.SubscriptionNotifierJob.PUSH_PAYLOAD_NEW_LINE;

public class SubscriptionNotifierJobTest {


    @Test
    public void new_episode_check_push_messag() {
        StringBuilder pushMessage = new StringBuilder();

        PodCast podCast = PodCastTest.createValid().build();
        String podCastEpisodeTitle = "Ericsson och mutskandalen";
        PodCastEpisode podCastEpisode = PodCastEpisodeTest.createValid().id("nytt avsnitts id 888").title(podCastEpisodeTitle).createdDate(LocalDateTime.now()
                .plusHours(1)).build();

        Subscription subscription = new Subscription("1234");

        SubscriptionNotifierJob job = new SubscriptionNotifierJob() {
            @Override
            List<Subscription> getSubscriptions() {
                return Collections.singletonList(subscription);
            }

            @Override
            void pushMessage(String message, String contentId) {
                pushMessage.append(message);
            }

            @Override
            PodCast getPodCast(String podCastId) {
                return podCast;
            }

            @Override
            Optional<PodCastEpisode> getLatestPodCastEpisodeFromSourceServer(PodCast podCast) {
                return Optional.ofNullable(podCastEpisode);
            }
        };

        job.doWork();

        String expected = podCast.getTitle() + PUSH_PAYLOAD_NEW_LINE+  " Nytt avsnitt: " + podCastEpisodeTitle;

        Assert.assertEquals(pushMessage.toString(), expected); //FIXME New line? in push payload
    }

}