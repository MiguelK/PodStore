package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.bundle.Bundle;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

public abstract class BundleBuilder implements Callable<Bundle> {

    private final static Logger LOG = Logger.getLogger(BundleBuilder.class.getName());

    private String title;
    private String description;
    private String imageURL;

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


    BundleBuilder(String imageURL, String title, String description) {
        if (StringUtils.trimToNull(title)==null) {
            throw new IllegalArgumentException("Missing title");
        }
        if (StringUtils.trimToNull(description)==null) {
            throw new IllegalArgumentException("Missing description");
        }
        if (StringUtils.trimToNull(imageURL)==null) {
            throw new IllegalArgumentException("Missing imageURL");
        }

        this.imageURL = imageURL.trim();
        this.title = title.trim();
        this.description = description.trim();
    }

    @Override
    public Bundle call() throws Exception {
        return createBundle(imageURL, title, description);
    }

    protected abstract Bundle createBundle(String imageURL, String title, String description);
}
