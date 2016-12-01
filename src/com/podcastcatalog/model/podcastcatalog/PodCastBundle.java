package com.podcastcatalog.model.podcastcatalog;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PodCastBundle extends Bundle {

    private final List<PodCast> podCasts;

    public PodCastBundle(String title, String description, List<PodCast> podCasts) {
        super(title, description, BundleType.PodCast);
        this.podCasts = Collections.unmodifiableList(podCasts);
    }

    @Override
    public List<PodCast> getBundleItems() {
        return podCasts;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String description;
        private final List<PodCast> podCasts = new ArrayList<>();

        public Builder title(String title) {
            this.title = StringUtils.trimToNull(title);
            return this;
        }

        public Builder description(String description) {
            this.description = StringUtils.trimToNull(description);
            return this;
        }

        public Builder podCast(PodCast podCast) {
            this.podCasts.add(podCast);
            return this;
        }

        public Builder podCasts(List<PodCast> podCasts) {
            this.podCasts.addAll(podCasts);
            return this;
        }

        public PodCastBundle build() {

            if (description == null) {
                throw new IllegalArgumentException("description is mandatory");
            }
            if (title == null) {
                throw new IllegalArgumentException("title is mandatory");
            }

            if (podCasts.isEmpty()) {
                throw new IllegalArgumentException("No podCasts exists in this bundle");
            }

            podCasts.forEach(p -> {
                if (p == null) {
                    throw new IllegalArgumentException();
                }
            });


            return new PodCastBundle(title, description, podCasts);
        }
    }

    @Override
    public String toString() {
        return "PodCastBundle{" +
                "podCasts=" + podCasts.size() +
                '}';
    }
}
