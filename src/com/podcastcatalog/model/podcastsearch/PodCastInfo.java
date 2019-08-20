package com.podcastcatalog.model.podcastsearch;

import java.io.Serializable;

public class PodCastInfo implements Serializable {

    private final String pid;

    public PodCastInfo(String pid) {
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PodCastInfo that = (PodCastInfo) o;

        return pid.equals(that.pid);
    }

    @Override
    public int hashCode() {
        return pid.hashCode();
    }
}
