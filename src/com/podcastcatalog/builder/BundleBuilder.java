package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.bundle.Bundle;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.Callable;

public abstract class BundleBuilder implements Callable<Bundle> {

    private String title;
    private String description;

    public static PodCastBundleBuilder newPodCastBundleBuilder(String title, String description) {
        return new PodCastBundleBuilder(title, description);
    }

    public static PodCastCategoryBundleBuilder newPodCastCategoryBundleBuilder(String title, String description) {
        return new PodCastCategoryBundleBuilder(title, description);
    }

    public static PodCastEpisodeBundleBuilder newPodCastEpisodeBundleBuilder(String title, String description) {
        return new PodCastEpisodeBundleBuilder(title, description);
    }


    BundleBuilder(String title, String description) {
        if (StringUtils.trimToNull(title)==null) {
            throw new IllegalArgumentException("Missing title");
        }
        if (StringUtils.trimToNull(description)==null) {
            throw new IllegalArgumentException("Missing description");
        }


        this.title = title.trim();
        this.description = description.trim();
    }

    @Override
    public Bundle call() throws Exception {
        return createBundle(title, description);
    }

    protected abstract Bundle createBundle(String title, String description);
}
