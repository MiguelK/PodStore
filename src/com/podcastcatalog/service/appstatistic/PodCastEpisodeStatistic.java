package com.podcastcatalog.service.appstatistic;

public class PodCastEpisodeStatistic extends StatisticCounter {

    public String pid;
    public String eid;

    public PodCastEpisodeStatistic(String pid, String eid) {
        this.pid = pid;
        this.eid = eid;
    }
}
