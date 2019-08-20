package com.podcastcatalog.service.appstatistic;

import java.io.Serializable;

public abstract class StatisticCounter implements Serializable, Comparable<StatisticCounter>   {
    private int counter;

    public void increaseCounter() {
        this.counter = counter + 1;
    }

    @Override
    public int compareTo(StatisticCounter o) {
        if(this.counter==o.counter){
            return 0;
        }
        else if(this.counter < o.counter){
            return 1;
        }
        else{
            return -1;
        }
    }
}
