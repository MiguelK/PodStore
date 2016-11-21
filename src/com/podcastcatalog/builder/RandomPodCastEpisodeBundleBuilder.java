package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.PodCastCategoryType;
import com.podcastcatalog.api.response.PodCastEpisode;
import com.podcastcatalog.api.response.bundle.Bundle;
import com.podcastcatalog.api.response.bundle.PodCastEpisodeBundle;

import java.util.ArrayList;
import java.util.List;

public class RandomPodCastEpisodeBundleBuilder {

    private final List<PodCast> podCas;
    private final List<PodCastCategory> podCastCategories;

    public RandomPodCastEpisodeBundleBuilder(List<PodCast> podCas, List<PodCastCategory> podCastCategories) {
        this.podCas = podCas;
        this.podCastCategories = podCastCategories;
    }

    public Bundle createEpisodeBundle() {

        List<PodCastEpisode> podCastEpisodes = new ArrayList<>();

        for (PodCast podCast : podCas) {
            PodCastEpisode podCastEpisode = podCast.getPodCastEpisodes().get(0);
            podCastEpisodes.add(podCastEpisode);
        }

        for (PodCastCategory podCastCategory : podCastCategories) {
            PodCastCategoryType podCastCategoryType = podCastCategory.getPodCastCategoryType();
            if(podCastCategoryType == PodCastCategoryType.ARTS || podCastCategoryType==PodCastCategoryType.HISTORY){
                PodCastEpisode podCastEpisode = podCastCategory.getPodCasts().get(1).getPodCastEpisodes().get(0);
                podCastEpisodes.add(podCastEpisode);
            }
        }

        return new PodCastEpisodeBundle("title", "description", "imageURL", podCastEpisodes);//FIXME
    }
}
