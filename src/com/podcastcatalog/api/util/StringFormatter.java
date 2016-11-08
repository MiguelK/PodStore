package com.podcastcatalog.api.util;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.PodCastCategoryType;
import com.podcastcatalog.api.response.bundle.Bundle;
import com.podcastcatalog.api.response.bundle.BundleType;

import java.util.List;

public class StringFormatter {
    private final PodCastCatalog podCastCatalog;

    private StringFormatter(PodCastCatalog podCastCatalog) {
        this.podCastCatalog = podCastCatalog;
    }

    public static StringFormatter create(PodCastCatalog podCastCatalog) {
        return new StringFormatter(podCastCatalog);
    }

    public String format() {

        if(podCastCatalog==null){
            return "PodCastCatalog not ready yet.?";
        }

        StringBuilder result = new StringBuilder();

        result.append("<body>");

        result.append(" Lang=").append(podCastCatalog.getPodCastCatalogLanguage()).append("<br>");
        result.append(" Created=").append(podCastCatalog.getCreated()).append("<br>");
        List<Bundle> bundles = podCastCatalog.getBundles();
        result.append(" Bundle size=").append(bundles.size()).append("<br>");

        for (Bundle bundle : bundles) {
            BundleType bundleType = bundle.getBundleType();

            StringBuilder part = new StringBuilder();
            result.append(" BundleType=").append(bundleType);
            result.append(" size=").append(bundle.getBundleItems().size()).append("<br>");

            switch (bundle.getBundleType()) {
                case Category:
                    List<PodCastCategory> bundleItems = (List<PodCastCategory>) bundle.getBundleItems();
                    for (PodCastCategory bundleItem : bundleItems) {
                        PodCastCategoryType categoryType = bundleItem.getPodCastCategoryType();
                        part.append(categoryType).append(" podCasts = ").append(bundleItem.getPodCasts().size()).append("<br>");
                    }
                    break;

                case Episode:
                    break;

                case PodCast:
                    break;
            }

            result.append(part.toString()).append("<br>");
        }

        return result.append("</body>").toString();
    }
}
