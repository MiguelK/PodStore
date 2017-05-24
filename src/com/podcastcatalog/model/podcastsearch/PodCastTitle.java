package com.podcastcatalog.model.podcastsearch;

import java.io.Serializable;

public class PodCastTitle implements Serializable {

    private final String title;

    public PodCastTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PodCastTitle that = (PodCastTitle) o;

        return title != null ? title.equals(that.title) : that.title == null;

    }

    @Override
    public int hashCode() {
        return title != null ? title.hashCode() : 0;
    }
}
