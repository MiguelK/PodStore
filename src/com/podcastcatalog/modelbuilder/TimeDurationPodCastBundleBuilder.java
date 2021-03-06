package com.podcastcatalog.modelbuilder;

import com.google.common.collect.Lists;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastBundle;
import com.podcastcatalog.model.podcastcatalog.PodCastCategory;
import com.podcastcatalog.model.podcastcatalog.PodCastCategoryType;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeDuration;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class TimeDurationPodCastBundleBuilder {

    private enum DurationInterval {
        half_hour,hour,one_half_hour,two_hour;

         static DurationInterval fromTotalTimeInSeconds(int seconds){

            if (seconds > 1500 && seconds <2100) { //25-25min
                    return DurationInterval.half_hour;
            } else if(seconds > 3300 && seconds <= 3900) { //55-65min
                return  DurationInterval.hour;
            }else if(seconds > 5100 && seconds < 5700) { //85-95min
                return  DurationInterval.one_half_hour;
            }else if(seconds > 6900 && seconds <= 7500) { //115-125min
                return  DurationInterval.two_hour;
            }

            return null;
        }

         String getImageUrl() {
             switch (this) {
                 case  half_hour:
                     return "http://pods.one/site/images/half_hour.jpg";
                 case  hour:
                     return "http://pods.one/site/images/hour.jpg";
                 case  one_half_hour:
                     return "http://pods.one/site/images/one_half_hour.jpg";
                 case  two_hour:
                     return "http://pods.one/site/images/two_hour.jpg";
                 default:
                     return "http://pods.one/site/images/half_hour.jpg"; //FIXME Not used
             }

         }


        String getTitle() {

            switch (this) {
                case  half_hour:
                    return "30 min";
                case  hour:
                    return "60 min";
                case  one_half_hour:
                    return "90 min";
                case  two_hour:
                    return "120 min";
                default:
                    return "45 min"; //FIXME Not used
            }
        }
    }

    private final List<PodCast> podCasts;
    private final List<PodCastCategory> podCastCategories;
    private final Map<DurationInterval, PodCast.Builder>  podCastsByDuration;

    public TimeDurationPodCastBundleBuilder(List<PodCast> podCasts, List<PodCastCategory> podCastCategories) {
        this.podCasts = podCasts;
        this.podCastCategories = podCastCategories;
        podCastsByDuration = new HashMap<>();
    }

    public PodCastBundle createPodCastBundle(String bundleName) {

        int success = 0;
        int failed = 0;

        for (PodCastCategory podCastCategory : podCastCategories) {
            for (PodCast podCast : podCastCategory.getPodCasts()) {
                for (PodCastEpisode podCastEpisode : podCast.getPodCastEpisodesInternal()) {

                    PodCastEpisodeDuration duration = podCastEpisode.getDuration();
                    if(duration == null){
                        failed++;
                        continue;
                    }

                    DurationInterval durationInterval = DurationInterval.fromTotalTimeInSeconds(duration.getTotalTimeInSeconds());

                    if(durationInterval != null) {
                        success++;
                        updatePodCastByDuration(podCast, podCastEpisode, durationInterval);
                    }
                }
            }
        }


       for (PodCast podCast : podCasts) {

            List<PodCastEpisode> episodesInternal = podCast.getPodCastEpisodesInternal();

            for (PodCastEpisode podCastEpisode : episodesInternal) {
                PodCastEpisodeDuration duration = podCastEpisode.getDuration();

                if(duration == null){
                    failed++;
                    continue;
                }

                DurationInterval durationInterval = DurationInterval.fromTotalTimeInSeconds(duration.getTotalTimeInSeconds());

                if(durationInterval != null) {
                    success++;
                    updatePodCastByDuration(podCast, podCastEpisode, durationInterval);
                }
            }
        }

//        System.out.println(success + " ,failed= " + failed + " podCasts**** " + podCasts.size());

        List<PodCast> podCasts = new ArrayList<>();


        PodCast.Builder builder3 = podCastsByDuration.get(DurationInterval.half_hour);
        if(builder3!=null) {
            builder3.shufflePodCastEpisodes();
            podCasts.add(builder3.build(100));
        }
        PodCast.Builder builder2 = podCastsByDuration.get(DurationInterval.hour);
        if(builder2!=null) {
            builder2.shufflePodCastEpisodes();
            podCasts.add(builder2.build(100));
        }
        PodCast.Builder builder1 = podCastsByDuration.get(DurationInterval.one_half_hour);

        if(builder1!=null) {
            builder1.shufflePodCastEpisodes();
            podCasts.add(builder1.build(100));
        }

        PodCast.Builder builder = podCastsByDuration.get(DurationInterval.two_hour);
        if(builder!=null) {
            builder.shufflePodCastEpisodes();
            podCasts.add(builder.build(100));
        }

        return new PodCastBundle(bundleName, "not used", podCasts);
    }

    private void updatePodCastByDuration(PodCast podCast, PodCastEpisode podCastEpisode, DurationInterval durationInterval) {
        PodCast.Builder podCastBuilder = podCastsByDuration.get(durationInterval);

        if(podCastBuilder== null) {
            podCastBuilder = PodCast.newBuilder().
                    publisher(PodCast.VIRTUAL_PODCAST_PUBLISHER).
                    setArtworkUrl600(durationInterval.getImageUrl()).
                    description("not used").title(durationInterval.getTitle());
            podCastsByDuration.put(durationInterval,podCastBuilder);
        }

        podCastBuilder.setPodCastCategories(Collections.singletonList(PodCastCategoryType.ARTS));
        podCastBuilder
                .collectionId(podCast.getCollectionId()).createdDate(podCast.getCreatedDate())
                .feedURL(podCast.getFeedURL());

        String podCastEpisodeTitle = podCastEpisode.getTitle();

        if(podCastEpisodeTitle.contains("Del") || podCastEpisodeTitle.contains("Episode")) {
            //Skip episoe delar e.g Del 1/3
            return;
        }

        podCastBuilder.addPodCastEpisode(podCastEpisode);
    }
}
