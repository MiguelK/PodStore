package com.podcastcatalog.modelbuilder;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastCategory;
import com.podcastcatalog.model.podcastcatalog.PodCastCategoryType;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.model.podcastcatalog.Bundle;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeBundle;

import java.util.ArrayList;
import java.util.List;

public class RandomPodCastEpisodeBundleBuilder {

    private final List<PodCast> podCas;
    private final List<PodCastCategory> podCastCategories;

    public RandomPodCastEpisodeBundleBuilder(List<PodCast> podCas, List<PodCastCategory> podCastCategories) {
        this.podCas = podCas;
        this.podCastCategories = podCastCategories;
    }

    public Bundle createEpisodeBundle(String bundleName) {

        List<PodCastEpisode> podCastEpisodes = new ArrayList<>();

        for (PodCast podCast : podCas) {
            PodCastEpisode podCastEpisode = podCast.getPodCastEpisodesInternal().get(0);
            podCastEpisodes.add(podCastEpisode);
        }

        for (PodCastCategory podCastCategory : podCastCategories) {
            PodCastCategoryType podCastCategoryType = podCastCategory.getPodCastCategoryType();
            if(podCastCategoryType == PodCastCategoryType.ARTS || podCastCategoryType==PodCastCategoryType.HISTORY){

                if(!podCastCategory.getPodCasts().isEmpty()){
                    PodCastEpisode podCastEpisode = podCastCategory.getPodCasts().get(0).getPodCastEpisodesInternal().get(0);
                    podCastEpisodes.add(podCastEpisode);
                }
            }
        }

        if(podCastEpisodes.isEmpty()){
            for (PodCastCategory podCastCategory : podCastCategories) {
                    if(!podCastCategory.getPodCasts().isEmpty()){
                        PodCastEpisode podCastEpisode = podCastCategory.getPodCasts().get(0).getPodCastEpisodesInternal().get(0);
                        podCastEpisodes.add(podCastEpisode);
                    }
            }
        }

        return new PodCastEpisodeBundle(bundleName, "?description?", podCastEpisodes);//FIXME
    }
}
