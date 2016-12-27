package com.podcastcatalog.util;

import com.podcastcatalog.model.podcastcatalog.*;
import com.podcastcatalog.service.ServiceDataStorage;

import java.util.List;
import java.util.Optional;

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

        List<ServiceDataStorage.PodCastCatalogVersion> allVersions = ServiceDataStorage.useDefault().getAllVersions(PodCastCatalogLanguage.SWE);
        result.append("PodCastCatalogVersion(s) = ").append(allVersions.size()).append("<br>");
        Optional<ServiceDataStorage.PodCastCatalogVersion> currentVersion = ServiceDataStorage.useDefault().getCurrentVersion(PodCastCatalogLanguage.SWE);
        currentVersion.ifPresent(podCastCatalogVersion -> result.append("Latest PodCastCatalogVersion = ").append(podCastCatalogVersion).append("<br>"));

        //FIXME Slow perf
/*
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
        }*/

        return result.append("</body>").toString();
    }
}
