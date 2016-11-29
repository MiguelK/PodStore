package com.podcastcatalog.api.util;

import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCategory;
import com.podcastcatalog.api.response.PodCastCategoryType;
import com.podcastcatalog.api.response.bundle.Bundle;
import com.podcastcatalog.api.response.bundle.BundleType;
import com.podcastcatalog.api.response.bundle.PodCastBundle;
import com.podcastcatalog.api.response.bundle.PodCastCategoryBundle;

import java.util.List;

public class StringFormatter {
    private final PodCastCatalog podCastCatalog;

    private StringFormatter(PodCastCatalog podCastCatalog) {
        this.podCastCatalog = podCastCatalog;
    }

    public static StringFormatter create(PodCastCatalog podCastCatalog) {
        return new StringFormatter(podCastCatalog);
    }

    @SuppressWarnings("unchecked")
    public String format() {

        if(podCastCatalog==null){
            return "PodCastCatalog not ready yet.?";
        }
        if(true){
            return "Disabled test... FIXME";//FIXME
        }

        StringBuilder result = new StringBuilder();

        result.append("<body>");


        int podcastCount = 0;
        int episodeCount = 0;
        for (Bundle bundle : podCastCatalog.getBundles()) {
            switch (bundle.getBundleType()) {

                case Category:
                    PodCastCategoryBundle c = (PodCastCategoryBundle)bundle;
                    for (PodCastCategory podCastCategory : c.getBundleItems()) {
                        List<PodCast> podCasts = podCastCategory.getPodCasts();
                        podcastCount += podCasts.size();
                        for (PodCast podCast : podCasts) {
                            episodeCount += podCast.getPodCastEpisodes().size();
                        }
                    }
                    break;

                case Episode:
                    episodeCount +=  bundle.getBundleItems().size();
                    break;

                case PodCast:
                    PodCastBundle podCastBundle = (PodCastBundle)bundle;
                    List<PodCast> podCasts = podCastBundle.getBundleItems();
                    podcastCount += podCasts.size();

                    for (PodCast podCast : podCasts) {
                        episodeCount += podCast.getPodCastEpisodes().size();
                    }
                    break;
            }
        }

        result.append("Total PodCasts = ").append(podcastCount).append("<br>");
        result.append("Total Episodes = ").append(episodeCount).append("<br>");

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
