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


    public enum Lang {SWE, ENG}

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


        String getTitle(Lang language) {

            switch (this) {
                case  half_hour:
                    return language == Lang.SWE ? "ca 30 minuter" : "about 30 minutes";
                case  hour:
                    return language == Lang.SWE ? "ca 1 timme" : "about 1 hour";
                case  one_half_hour:
                    return language == Lang.SWE ? "ca 1,5 timme" : "about 1,5 hours";
                case  two_hour:
                    return language == Lang.SWE ? "ca 2 timmar" : "about 2 hours";
                default:
                    return "Spring"; //FIXME Not used
            }
        }
    }

    private final List<PodCast> podCasts;
    private final List<PodCastCategory> podCastCategories;
    private final Map<DurationInterval, PodCast.Builder>  podCastsByDuration;
    private final Lang language;

    public TimeDurationPodCastBundleBuilder(Lang language, List<PodCast> podCasts, List<PodCastCategory> podCastCategories) {
        this.podCasts = podCasts;
        this.podCastCategories = podCastCategories;
        podCastsByDuration = new HashMap<>();
        this.language = language;
    }

    public PodCastBundle createPodCastBundle(String bundleName) {

        int success = 0;
        int failed = 0;

        for (PodCastCategory podCastCategory : podCastCategories) {
            for (PodCast podCast : podCastCategory.getPodCasts()) {
                for (PodCastEpisode podCastEpisode : podCast.getPodCastEpisodes()) {

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

            List<PodCastEpisode> episodesInternal = podCast.getPodCastEpisodes();

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


        podCasts.add(podCastsByDuration.get(DurationInterval.half_hour).build());
        podCasts.add(podCastsByDuration.get(DurationInterval.hour).build());
        podCasts.add(podCastsByDuration.get(DurationInterval.one_half_hour).build());
        podCasts.add(podCastsByDuration.get(DurationInterval.two_hour).build());


        return new PodCastBundle(bundleName, "not used", podCasts);
    }

    private void updatePodCastByDuration(PodCast podCast, PodCastEpisode podCastEpisode, DurationInterval durationInterval) {
        PodCast.Builder podCastBuilder = podCastsByDuration.get(durationInterval);

        if(podCastBuilder== null) {
            podCastBuilder = PodCast.newBuilder().
                    publisher("virtualPodCast").
                    setArtworkUrl600(durationInterval.getImageUrl()).
                    description("not used").title(durationInterval.getTitle(language));
            podCastsByDuration.put(durationInterval,podCastBuilder);
        }

        podCastBuilder.setPodCastCategories(Collections.singletonList(PodCastCategoryType.ARTS));
        podCastBuilder
                .collectionId(podCast.getCollectionId()).createdDate(podCast.getCreatedDate())
                .feedURL(podCast.getFeedURL());
        podCastBuilder.addPodCastEpisode(podCastEpisode);
    }
}