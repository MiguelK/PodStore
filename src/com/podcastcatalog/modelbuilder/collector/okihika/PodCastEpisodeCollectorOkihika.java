package com.podcastcatalog.modelbuilder.collector.okihika;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.modelbuilder.collector.PodCastEpisodeCollector;

import java.util.ArrayList;
import java.util.List;

public class PodCastEpisodeCollectorOkihika extends PodCastCollectorOkihika implements PodCastEpisodeCollector {


    private final int episodeCount;

    public PodCastEpisodeCollectorOkihika(PodCastCollectorOkihika.TopList toplist, int podCasts, int episodeCount) {
        super(toplist, podCasts);
        this.episodeCount = episodeCount;
    }

    @Override
    public List<PodCastEpisode> collectEpisodes() {

        List<PodCastEpisode> episodes = new ArrayList<>();
        List<PodCast> podCasts = collectPodCasts();

        for (PodCast podCast : podCasts) {

            int j = 0;
            for (PodCastEpisode podCastEpisode : podCast.getPodCastEpisodes()) {
                if (j >= episodeCount) {
                    break;
                }
                episodes.add(podCastEpisode);
                j++;
            }

        }

        return episodes;
    }
}
