package com.podcastcatalog.model.podcastsearch;

import java.io.Serializable;

public class PodCastInfo implements Serializable {

    private final String pid;
    private final String title;

    public PodCastInfo(String pid, String title) {
        this.pid = pid;
        this.title = title;
    }

    public String getTitle() {
        return title;
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
