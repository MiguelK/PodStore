package com.podcastcatalog.service.appstatistic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppStatisticData implements Serializable {

    public List<PodCastStatistic> popularPodCasts = new ArrayList<>();
    public List<PodCastEpisodeStatistic> popularPodCastEpisodes = new ArrayList<>();

}
