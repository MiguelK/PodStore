package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.bundle.Bundle;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

public abstract class BundleBuilder implements Callable<Bundle> {

    private final static Logger LOG = Logger.getLogger(BundleBuilder.class.getName());

    protected String title;
    protected String description;
    protected String imageURL;

    public static PodCastBundleBuilder newPodCastBundleBuilder(String imageURL, String title, String description) {
        return new PodCastBundleBuilder(imageURL, title, description);
    }

    public static PodCastCategoryBundleBuilder newPodCastCategoryBundleBuilder(String imageURL,
                                                                               String title, String description) {
        return new PodCastCategoryBundleBuilder(imageURL, title, description);
    }

    public static PodCastEpisodeBundleBuilder newPodCastEpisodeBundleBuilder(String imageURL,
                                                                             String title, String description) {
        return new PodCastEpisodeBundleBuilder(imageURL, title, description);
    }


    public BundleBuilder(String imageURL, String title, String description) {
        if (StringUtils.isEmpty(title)) {
            throw new IllegalArgumentException();
        }
        if (StringUtils.isEmpty(description)) {
            throw new IllegalArgumentException();
        }

        this.imageURL = imageURL;
        this.title = title;
        this.description = description;
    }

    @Override
    public Bundle call() throws Exception {
        return createBundle(imageURL, title, description);
    }

    protected abstract Bundle createBundle(String imageURL, String title, String description);
}
