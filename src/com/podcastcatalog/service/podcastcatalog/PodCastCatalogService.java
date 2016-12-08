package com.podcastcatalog.service.podcastcatalog;

import com.podcastcatalog.model.podcastcatalog.*;
import com.podcastcatalog.model.podcastsearch.PodCastEpisodeResultItem;
import com.podcastcatalog.model.podcastsearch.PodCastResultItem;
import com.podcastcatalog.model.podcastsearch.ResultItem;
import com.podcastcatalog.modelbuilder.BundleBuilder;
import com.podcastcatalog.modelbuilder.PodCastCatalogBuilder;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.service.ServiceDataStorage;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PodCastCatalogService {

    private static final int THREADS = 5;
    private static final int MAX_BUILD_CATALOG_TIMEOUT_IN_MINUTES = 15;
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(PodCastCatalogService.class.getName());

    private final Map<PodCastCatalogLanguage, PodCastCatalog> podCastCatalogByLang;
    private final List<PodCastCatalogBuilder> podCastCatalogBuilders;

    private final TextSearchEngine<ResultItem> textSearchEngine;
    private final PodCastIndex podCastIndex;
    private ServiceDataStorage storage;
    private final ExecutorService executorService;
    private final ExecutorService ayncExecutor;

    private static final PodCastCatalogService INSTANCE = new PodCastCatalogService();

    private PodCastCatalogService() {
        podCastIndex = PodCastIndex.create();
        textSearchEngine = new TextSearchEngine<>();
        podCastCatalogBuilders = new ArrayList<>();
        podCastCatalogByLang = new HashMap<>();
        ayncExecutor = Executors.newFixedThreadPool(THREADS);
        executorService = Executors.newSingleThreadExecutor();//Important single thread!
    }

    public static PodCastCatalogService getInstance() {
        return INSTANCE;
    }

    public Optional<PodCast> getPodCastById(String id) {
        return podCastIndex.lookup(id);
    }

    public void registerPodCastCatalogBuilder(PodCastCatalogBuilder builder) {
        podCastCatalogBuilders.add(builder);
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
            return textSearchEngine.getStatus();

        } finally {
            readLock.unlock();
        }
    }

    public String getPodCastIndexStatus() {
        readLock.lock();
        try {
            return podCastIndex.getStatus();

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
        //FIXME Sort algoritm? limit 5 etc...
        List<ResultItem> resultItems = new ArrayList<>();
        List<PodCastResultItem> podCasts = ItunesSearchAPI.search("term=" + queryParam + "&entity=podcast&limit=5").searchPodCast();
        resultItems.addAll(podCasts);

        List<ResultItem> result = textSearchEngine.lookup(queryParam);
        resultItems.addAll(result);

        return resultItems;
    }

    public void buildPodCastCatalogsAsync() {
        validateState();

        ayncExecutor.submit(new BuildPodCastCatalogAction());
    }

    public void buildIndexAsync() {
        ayncExecutor.submit(new BuildIndexAction());
    }

    //FIXME Test only delete?
    public void buildPodCastCatalogs() {
        validateState();

        try {
            ayncExecutor.submit(new BuildPodCastCatalogAction()).get(MAX_BUILD_CATALOG_TIMEOUT_IN_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Unable to rebuild catalogs ", e);
        }
    }

    private void validateState() {
        if (storage == null) {
            throw new IllegalStateException("Configure storage, storage is null");
        }
    }

    private class BuildPodCastCatalogAction implements Runnable {

        @Override
        public void run() {
            readLock.lock();
            LOG.info("rebuildCatalogs() registered podCastCatalogBuilders=" + podCastCatalogBuilders.size());

            List<PodCastCatalogBuilder> snapShot = new ArrayList<>(podCastCatalogBuilders);

            Map<PodCastCatalogLanguage, PodCastCatalog> newCatalogs = new HashMap<>();
            try {
                for (PodCastCatalogBuilder podCastCatalogBuilder : snapShot) {
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

            buildIndexAsync();
        }
    }

    private class BuildIndexAction implements Runnable {
        @Override
        public void run() {
            LOG.info("Start " + BuildIndexAction.class.getSimpleName() + " catalogs=" + podCastCatalogByLang.size() + "...");

            BundleItemVisitor bundleItemVisitor = new BundleItemVisitor();

            readLock.lock();
            try {
                for (Map.Entry<PodCastCatalogLanguage, PodCastCatalog> catalogEntry : podCastCatalogByLang.entrySet()) {
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
            try {
                List<PodCastEpisode> podCastEpisodes = bundleItemVisitor.getPodCastEpisodes();
                for (PodCastEpisode podCastEpisode : podCastEpisodes) {
                    PodCastEpisodeResultItem resultItem = new PodCastEpisodeResultItem(podCastEpisode.getTitle(),
                            podCastEpisode.getDescription(), podCastEpisode.getPodCastCollectionId(), podCastEpisode.getTargetURL());

                    String text = podCastEpisode.getTitle() + " " + podCastEpisode.getDescription();
                    textSearchEngine.addText(text, TextSearchEngine.Prio.HIGH, resultItem);
                }

                textSearchEngine.buildIndex();

                podCastIndex.buildIndex(bundleItemVisitor.getPodCasts());

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
