package com.podcastcatalog.service.podcastcatalog;

import com.podcastcatalog.model.podcastcatalog.*;
import com.podcastcatalog.model.podcastsearch.PodCastEpisodeResultItem;
import com.podcastcatalog.model.podcastsearch.PodCastResultItem;
import com.podcastcatalog.model.podcastsearch.ResultItem;
import com.podcastcatalog.modelbuilder.BundleBuilder;
import com.podcastcatalog.modelbuilder.PodCastCatalogBuilder;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.service.datastore.ServiceDataStorage;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PodCastCatalogService {

    private static final int THREADS = 5;
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(PodCastCatalogService.class.getName());

    private final Map<PodCastCatalogLanguage, PodCastCatalog> podCastCatalogByLang;
    private final List<PodCastCatalogBuilder> podCastCatalogBuilders;

    private TextSearchIndex<ResultItem> textSearchIndex;
    private final PodCastIndex podCastCatalogIndex;
    private final PodCastIndex podCastIndex;
    private ServiceDataStorage storage;
    private final ExecutorService executorService;
    private final ExecutorService asyncExecutor;
    private volatile boolean buildingInProgress;

    private static final PodCastCatalogService INSTANCE = new PodCastCatalogService();

    private PodCastCatalogService() {
        podCastCatalogIndex = PodCastIndex.create();
        podCastIndex = PodCastIndex.create();
        textSearchIndex = new TextSearchIndex<>();
        podCastCatalogBuilders = new ArrayList<>();
        podCastCatalogByLang = new HashMap<>();
        asyncExecutor = Executors.newFixedThreadPool(THREADS);
        executorService = Executors.newFixedThreadPool(5);//Important single thread!
    }

    public static PodCastCatalogService getInstance() {
        return INSTANCE;
    }

    public Optional<PodCast> getPodCastById(String id) {
        readLock.lock();
        try {
            //podCastIndex updated every 10th minute @see PodCastSubscriptionUpdater.java
            Optional<PodCast> podCast = podCastIndex.lookup(id);
            if (podCast.isPresent()) {
                return podCast;
            }

            return podCastCatalogIndex.lookup(id);
        } finally {
            readLock.unlock();
        }
    }

    public void updatePodCastIndex(PodCast podCast) {
        writeLock.lock();

        try {
            podCastIndex.update(podCast);
        } finally {
            writeLock.unlock();
        }
    }

    public void registerPodCastCatalogBuilder(PodCastCatalogBuilder builder) {
        writeLock.lock();

        try {
            podCastCatalogBuilders.add(builder);
        } finally {
            writeLock.unlock();
        }
    }

    public void setStorage(ServiceDataStorage storage) {
        this.storage = storage;
    }

    public PodCastCatalog getPodCastCatalog(PodCastCatalogLanguage podCastCatalogLanguage) {
        readLock.lock();
        try {
            return podCastCatalogByLang.get(podCastCatalogLanguage);

        } finally {
            readLock.unlock();
        }
    }

    public String getTextSearchEngineStatus() {
        readLock.lock();
        try {
            return textSearchIndex.getStatus();
        } finally {
            readLock.unlock();
        }
    }

    public String getPodCastCatalogIndexStatus() {
        readLock.lock();
        try {
            return podCastCatalogIndex.getStatus();

        } finally {
            readLock.unlock();
        }
    }

    public void loadPodCastCatalog(PodCastCatalog podCastCatalog) {

        writeLock.lock();
        LOG.info("Loading stored podCastCatalog " + podCastCatalog);

        try {
            podCastCatalogByLang.put(podCastCatalog.getPodCastCatalogLanguage(), podCastCatalog);
        } finally {
            writeLock.unlock();
        }
    }


    public List<ResultItem> search(String queryParam) {

        String queryParamTrimmed = StringUtils.trimToNull(queryParam);
        if (queryParamTrimmed == null) {
            return Collections.emptyList();
        }

        //FIXME Sort algoritm? limit 5 etc...
        List<ResultItem> resultItems = new ArrayList<>();
        List<PodCastResultItem> podCasts = ItunesSearchAPI.searchPodCasts("term=" + queryParam + "&entity=podcast&limit=5");
        resultItems.addAll(podCasts);

        readLock.lock();
        try {
            List<ResultItem> result = textSearchIndex.lookup(queryParam);
            resultItems.addAll(result);
            return resultItems;
        } finally {
            readLock.unlock();
        }
    }

    public void buildIndexAsync(PodCastCatalogLanguage podCastCatalogLanguage) {
        asyncExecutor.submit(new BuildIndexAction(podCastCatalogLanguage));
    }

    public Future buildPodCastCatalogsAsync(PodCastCatalogLanguage language) {
        validateState();

        if(buildingInProgress){
            LOG.info("Buulding in progress language=" + language + " try again later.?");
           return null;
        }

        buildingInProgress = true;

        return asyncExecutor.submit(new BuildPodCastCatalogAction(language));
    }

    public boolean isBuildingInProgress() {
        return buildingInProgress;
    }

    private void validateState() {
        if (storage == null) {
            throw new IllegalStateException("Configure storage, storage is null");
        }

        if (podCastCatalogBuilders.isEmpty()) {
            throw new IllegalStateException("No podCastCatalogBuilders registered");
        }

    }

    private class BuildPodCastCatalogAction implements Runnable {

        private PodCastCatalogLanguage podCastCatalogLanguage;

        public BuildPodCastCatalogAction(PodCastCatalogLanguage podCastCatalogLanguage) {
            this.podCastCatalogLanguage = podCastCatalogLanguage;
        }

        @Override
        public void run() {
            readLock.lock();
            LOG.info(BuildPodCastCatalogAction.class.getSimpleName() + ", registered podCastCatalogBuilders=" + podCastCatalogBuilders.size());

            Map<PodCastCatalogLanguage, PodCastCatalog> newCatalogs = new HashMap<>();
            try {
                for (PodCastCatalogBuilder podCastCatalogBuilder : podCastCatalogBuilders) {

                    if (podCastCatalogBuilder.getPodCastCatalogLang() != podCastCatalogLanguage) {
                        continue; //Only build one catalog = podCastCatalogLanguage
                    }

                    LOG.info("Start building PodCastCatalog " + podCastCatalogBuilder.getPodCastCatalogLang() + " ...");

                    PodCastCatalog catalog = buildPodcastCatalog(podCastCatalogBuilder);

                    if (catalog != null) {
                        storage.save(catalog);
                        newCatalogs.put(podCastCatalogBuilder.getPodCastCatalogLang(), catalog);
                    }
                }
            } finally {
                readLock.unlock();
            }

            writeLock.lock();
            try {
                for (PodCastCatalogLanguage language : newCatalogs.keySet()) {
                    LOG.info("PodCastCatalog " + language + " was updated with new version");
                    podCastCatalogByLang.put(language, newCatalogs.get(language));
                }

            } finally {
                writeLock.unlock();
            }

            buildIndexAsync(podCastCatalogLanguage);

            buildingInProgress = false; //FIXME
        }
    }

    private class BuildIndexAction implements Runnable {

        private final PodCastCatalogLanguage podCastCatalogLanguage;

        public BuildIndexAction(PodCastCatalogLanguage podCastCatalogLanguage) {
            this.podCastCatalogLanguage = podCastCatalogLanguage;
        }

        @Override
        public void run() {
            LOG.info("Start " + BuildIndexAction.class.getSimpleName() + " catalogs=" + podCastCatalogByLang.size() + "...");

            BundleItemVisitor bundleItemVisitor = new BundleItemVisitor();

            readLock.lock();
            try {
                for (Map.Entry<PodCastCatalogLanguage, PodCastCatalog> catalogEntry : podCastCatalogByLang.entrySet()) {

                    if (catalogEntry.getKey() != podCastCatalogLanguage) {
                        continue;
                    }

                    LOG.info("Start building index for PodCastCatalog " + podCastCatalogLanguage);

                    for (Bundle bundle : catalogEntry.getValue().getBundles()) {
                        for (BundleItem bundleItem : bundle.getBundleItems()) {
                            bundleItem.accept(bundleItemVisitor);
                        }
                    }
                }

            } finally {
                readLock.unlock();
            }

            writeLock.lock();
            TextSearchIndex<ResultItem> newTextSearchIndex = new TextSearchIndex<>();

            try {
                List<PodCastEpisode> podCastEpisodes = bundleItemVisitor.getPodCastEpisodes();
                for (PodCastEpisode podCastEpisode : podCastEpisodes) {
                    PodCastEpisodeResultItem resultItem = new PodCastEpisodeResultItem(podCastEpisode.getTitle(),
                            podCastEpisode.getDescription(), podCastEpisode.getPodCastCollectionId(), podCastEpisode.getTargetURL());

                    String text = podCastEpisode.getTitle() + " " + podCastEpisode.getDescription();
                    newTextSearchIndex.addText(text, TextSearchIndex.Prio.HIGH, resultItem);
                }
                newTextSearchIndex.buildIndex();

                textSearchIndex = newTextSearchIndex;

                podCastCatalogIndex.buildIndex(bundleItemVisitor.getPodCasts());

            } finally {
                writeLock.unlock();
            }

            LOG.info("Done " + BuildIndexAction.class.getSimpleName());
        }
    }

    private PodCastCatalog buildPodcastCatalog(PodCastCatalogBuilder podCastCatalogBuilder) {

        Set<BundleBuilder> bundleBuilders = podCastCatalogBuilder.getBundleBuilders();
        List<Bundle> bundles = new ArrayList<>();
        try {
            List<Future<Bundle>> futureBundles = executorService.invokeAll(bundleBuilders);
            for (Future<Bundle> futureBundle : futureBundles) {
                Bundle bundle = futureBundle.get();//FIXME Max timeout??
                bundles.add(bundle);
            }

            List<PodCast> podCasts = new ArrayList<>();
            bundles.stream().filter(b -> b instanceof PodCastBundle).forEach(e ->
                    podCasts.addAll(((PodCastBundle) e).getBundleItems()));

            List<PodCastCategory> podCastCategories = new ArrayList<>();
            bundles.stream().filter(b -> b instanceof PodCastCategoryBundle).forEach(e ->
                    podCastCategories.addAll(((PodCastCategoryBundle) e).getBundleItems()));

            bundles.addAll(podCastCatalogBuilder.createFromFetchedData(podCasts, podCastCategories));

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed building catalog " + podCastCatalogBuilder.getPodCastCatalogLang(), e);
            return null;
        }

        return PodCastCatalog.create(podCastCatalogBuilder.getPodCastCatalogLang(), bundles);
    }
}
