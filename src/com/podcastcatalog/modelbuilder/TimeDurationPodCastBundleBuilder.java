package com.podcastcatalog.modelbuilder;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastBundle;
import com.podcastcatalog.model.podcastcatalog.PodCastCategory;
import com.podcastcatalog.model.podcastcatalog.PodCastCategoryType;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeDuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TimeDurationPodCastBundleBuilder {

    private static final int MAX_EPISODE_SIZE = 20;

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
             return "http://pods.one/site/images/DSC_2321.jpg";
         }

        String getTitle() {

            switch (this) {
                case  half_hour:
                    return "30 minuter (5km)";
                case  hour:
                    return "1 timme (10km)";
                case  one_half_hour:
                    return "1:30 minuter";
                case  two_hour:
                    return "2 timmar";
                default:
                    return "Spring";
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

    public PodCastBundle createPodCastBundle() {

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

        for (PodCast.Builder builder : podCastsByDuration.values()) {

            PodCast podCast = builder.build();
            podCasts.add(podCast);
        }

        return new PodCastBundle("För löppasset", "not used", podCasts);
    }

    private void updatePodCastByDuration(PodCast podCast, PodCastEpisode podCastEpisode, DurationInterval durationInterval) {
        PodCast.Builder podCastBuilder = podCastsByDuration.get(durationInterval);

        if(podCastBuilder== null) {
            podCastBuilder = PodCast.newBuilder().
                    publisher("virtualPodCast").
                    setArtworkUrl600(durationInterval.getImageUrl()).
                    description("not used").title(durationInterval.getTitle());
            podCastsByDuration.put(durationInterval,podCastBuilder);
        }

        podCastBuilder.setPodCastCategories(Collections.singletonList(PodCastCategoryType.ARTS));
        podCastBuilder
                .collectionId(podCast.getCollectionId()).createdDate(podCast.getCreatedDate())
                .feedURL(podCast.getFeedURL());
        podCastBuilder.addPodCastEpisode(podCastEpisode);
    }
}
