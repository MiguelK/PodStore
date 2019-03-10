package com.podcastcatalog.modelbuilder.language;

import com.podcastcatalog.model.podcastcatalog.Bundle;
import com.podcastcatalog.model.podcastcatalog.BundleName;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastBundle;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastcatalog.PodCastCategory;
import com.podcastcatalog.modelbuilder.BundleBuilder;
import com.podcastcatalog.modelbuilder.PodCastBundleBuilder;
import com.podcastcatalog.modelbuilder.PodCastCatalogBuilder;
import com.podcastcatalog.modelbuilder.PodCastCategoryBundleBuilder;
import com.podcastcatalog.modelbuilder.RandomPodCastEpisodeBundleBuilder;
import com.podcastcatalog.modelbuilder.TimeDurationPodCastBundleBuilder;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCategoryCollectorOkihika;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class PodCastCatalogBuilderBase implements PodCastCatalogBuilder {

    static class CategoryName {
        private PodCastIdCollector.Category category;
        private String categoryTitle;

        private CategoryName(PodCastIdCollector.Category category, String categoryTitle) {
            this.category = category;
            this.categoryTitle = categoryTitle;
        }
        static CategoryName create(PodCastIdCollector.Category category, String categoryTitle) {
                return new CategoryName(category, categoryTitle);
        }

        public PodCastIdCollector.Category getCategory() {
            return category;
        }

        String getCategoryTitle() {
            return categoryTitle;
        }
    }

    class CategoryContext {
        private List<CategoryName> categoryNames = new ArrayList<>();

        void add(PodCastIdCollector.Category category, String categoryTitle){
            categoryNames.add(new CategoryName(category, categoryTitle));
        }

        public List<CategoryName> getCategoryNames() {
            return categoryNames;
        }
    }

    @Override
    public Set<BundleBuilder> getBundleBuilders() {

        Set<BundleBuilder> bundleBuilders = new HashSet<>();

        PodCastCatalogLanguage podCastCatalogLang = getPodCastCatalogLang();

        PodCastBundleBuilder podCastBundle = BundleBuilder.newPodCastBundleBuilder(BundleName.PodCastTopList.name(), "???");//FIXME Image

        PodCastIdCollector podCastIdCollector1 = PodCastIdCollector.createPodCastIdCollector(podCastCatalogLang, PodCastIdCollector.Category.TOPLIST_COUNTRY);
        //podCastBundle.addCollector(new PodCastCollectorOkihika(podCastCatalogLang, PodCastCollectorOkihika.TopList.TOPLIST_COUNTRY, 30));
        podCastBundle.addCollector(podCastIdCollector1);

        bundleBuilders.add(podCastBundle);

        PodCastCategoryBundleBuilder categoryBundle = BundleBuilder.newPodCastCategoryBundleBuilder(BundleName.PodCastCategoryTopLists.name(), "???");

        CategoryContext categoryContext = new CategoryContext();
        addCategoryNames(categoryContext);

        for (CategoryName categoryName : categoryContext.getCategoryNames()) {
            PodCastIdCollector podCastIdCollector = new PodCastIdCollector(podCastCatalogLang,
                    categoryName.getCategory(), categoryName.getCategoryTitle());
            categoryBundle.addCollector(podCastIdCollector);
        }

        bundleBuilders.add(categoryBundle);

        return bundleBuilders;
    }

    @Override
    public List<Bundle> createFromFetchedData(List<PodCast> podCasts, List<PodCastCategory> podCastCategories) {

        List<Bundle> bundles = new ArrayList<>();

        RandomPodCastEpisodeBundleBuilder randomPodCastEpisodeBundleBuilder = new RandomPodCastEpisodeBundleBuilder(podCasts, podCastCategories);
        Bundle bundle = randomPodCastEpisodeBundleBuilder.createEpisodeBundle(BundleName.RandomPodCastEpisodes.name());
        bundles.add(bundle);

        TimeDurationPodCastBundleBuilder timeDurationPodCastBundleBuilder = new TimeDurationPodCastBundleBuilder(podCasts, podCastCategories);
        PodCastBundle podCastBundle = timeDurationPodCastBundleBuilder.createPodCastBundle(BundleName.TimedPodCastCollections.name());
        bundles.add(podCastBundle);

        return bundles;
    }

    abstract  void addCategoryNames(CategoryContext categoryContext);
}
