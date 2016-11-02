package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.bundle.Bundle;
import com.podcastcatalog.api.response.bundle.BundleType;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

public abstract class BundleBuilder implements Callable<Bundle> {

    private final static Logger LOG = Logger.getLogger(BundleBuilder.class.getName());

    protected String title;
    protected String description;
    protected String imageURL;
    private BundleType bundleType;

     BundleBuilder(String imageURL, String title, String description, BundleType bundleType) {
        if(StringUtils.isEmpty(title)){
            throw new IllegalArgumentException();
        }
        if(StringUtils.isEmpty(description)){
            throw new IllegalArgumentException();
        }

        this.bundleType = bundleType;
        this.imageURL = imageURL;
        this.title = title;
        this.description = description;
    }

    @Override
    public Bundle call() throws Exception {
        return null;
    }
}
