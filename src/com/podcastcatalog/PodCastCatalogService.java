package com.podcastcatalog;

import com.podcastcatalog.api.response.*;
import com.podcastcatalog.api.response.bundle.Bundle;
import com.podcastcatalog.api.response.bundle.PodCastBundle;
import com.podcastcatalog.api.response.bundle.PodCastCategoryBundle;
import com.podcastcatalog.api.response.search.PodCastEpisodeResultItem;
import com.podcastcatalog.api.response.search.PodCastSearchResponse;
import com.podcastcatalog.api.response.search.ResultItem;
import com.podcastcatalog.api.util.PodCastEpisodeVisitor;
import com.podcastcatalog.builder.BundleBuilder;
import com.podcastcatalog.builder.PodCastCatalogBuilder;
import com.podcastcatalog.builder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.store.DataStorage;

import java.util.*;
import java.util.concurrent.*;
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
//    private List<PodCastEpisodeResultItem> podCastEpisodeSearchResults = new ArrayList<>();

    private final TextSearchEngine textSearchEngine;
    private DataStorage storage;
    private final ExecutorService executorService;
    private final ExecutorService ayncExecutor;

    private static final PodCastCatalogService INSTANCE = new PodCastCatalogService();

    private PodCastCatalogService() {
        textSearchEngine = new TextSearchEngine<ResultItem>();
        podCastCatalogBuilders = new ArrayList<>();
        podCastCatalogByLang = new HashMap<>();
        ayncExecutor = Executors.newFixedThreadPool(THREADS);
        executorService = Executors.newSingleThreadExecutor();//Important single thread!
    }

    public static PodCastCatalogService getInstance() {
        return INSTANCE;
    }

    void registerPodCastCatalogBuilder(PodCastCatalogBuilder builder) {
        podCastCatalogBuilders.add(builder);
    }

    void setStorage(DataStorage storage) {
        this.storage = storage;
    }

    void loadPodCastCatalog(PodCastCatalog podCastCatalog) {

        writeLock.lock();
        LOG.info("Loading stored podCastCatalog " + podCastCatalog);

        try {
            podCastCatalogByLang.put(podCastCatalog.getPodCastCatalogLanguage(), podCastCatalog);
        } finally {
            writeLock.unlock();
        }
    }

    public void buildPodCastCatalogsAsync() {
        validateState();

        ayncExecutor.submit(new RebuildCatalogAction());
    }

    public void buildPodCastEpisodeIndexAsync() {
        LOG.info("buildPodCastEpisodeIndexAsync() called");
        ayncExecutor.submit(new RebuildPodCastEpisodeIndex());

    }

    private void validateState() {
        if (storage == null) {
            throw new IllegalStateException("Configure storage, storage is null");
        }
    }

    //FIXME Test only delete?
    void buildPodCastCatalogs() {
        validateState();

        try {
            ayncExecutor.submit(new RebuildCatalogAction()).get(MAX_BUILD_CATALOG_TIMEOUT_IN_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Unable to rebuild catalogs ", e);
        }
    }

    public List<ResultItem> searchEpisodes(String queryParam) {

       /* if (podCastEpisodeSearchResults.isEmpty()) {
            return Collections.emptyList();
        }

        if (podCastEpisodeSearchResults.size() <= 10) {
            return podCastEpisodeSearchResults;
        }*/
        List<ResultItem> resultItems = new ArrayList<>();
        List<PodCastSearchResponse> podCasts = ItunesSearchAPI.search("term=" + queryParam + "&entity=podcast&limit=5").searchPodCast();
        LOG.info("Search: PodCasts=" + podCasts.size() + ",queryParam=" + queryParam);
        resultItems.addAll(podCasts);

        List<PodCastEpisodeResultItem> result = this.textSearchEngine.lookup(queryParam);
        LOG.info("Search: Episodes=" + result.size() + ",queryParam=" + queryParam);
        resultItems.addAll(result);
/*

        //FIXME Search in episodes...
        //FIXME Perform search ... queryParam
        List<PodCastEpisodeResultItem> result = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            result.add(podCastEpisodeSearchResults.get(i));//FIXME
        }

*/

        //FIXME filter max result =20?
        return resultItems;
    }

    private class RebuildCatalogAction implements Callable<Void> {
        @Override
        public Void call() throws Exception {

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

            buildPodCastEpisodeIndexAsync();

            return null;
        }
    }

    private class RebuildPodCastEpisodeIndex implements Runnable {
        @Override
        public void run() {
            LOG.info("Started RebuildPodCastEpisodeIndex...");
            List<PodCastEpisodeResultItem> results = new ArrayList<>();

            PodCastEpisodeVisitor visitor = new PodCastEpisodeVisitor();

            readLock.lock();
            try {
                for (Map.Entry<PodCastCatalogLanguage, PodCastCatalog> catalogEntry : podCastCatalogByLang.entrySet()) {
                    for (Bundle bundle : catalogEntry.getValue().getBundles()) {
                        for (BundleItem bundleItem : bundle.getBundleItems()) {
                            bundleItem.accept(visitor);
                        }
                    }
                }

            } finally {
                readLock.unlock();
            }

            writeLock.lock();
            LOG.info("Rebuilding textSearchEngine size=" + results.size());
            try {
                for (PodCastEpisode podCastEpisode : visitor.getPodCastEpisodes()) {
                    PodCastEpisodeResultItem resultItem = new PodCastEpisodeResultItem(podCastEpisode.getTitle(),
                            podCastEpisode.getDescription(), podCastEpisode.getPodCastCollectionId(), podCastEpisode.getTargetURL());

                    String text = podCastEpisode.getTitle() + " " + podCastEpisode.getDescription();
                    textSearchEngine.addText(text, TextSearchEngine.Prio.HIGH, resultItem);
                }

                textSearchEngine.buildIndex();
            } finally {
                writeLock.unlock();
            }

            LOG.info("Done RebuildPodCastEpisodeIndex...");
        }
    }

    public PodCastCatalog getPodCastCatalog(PodCastCatalogLanguage podCastCatalogLanguage) {
        readLock.lock();
        try {
            return podCastCatalogByLang.get(podCastCatalogLanguage);

        } finally {
            readLock.unlock();
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
            bundles.stream().filter(b -> b instanceof PodCastBundle).forEach(e -> podCasts.addAll(((PodCastBundle) e).getBundleItems()));

            List<PodCastCategory> podCastCategories = new ArrayList<>();
            bundles.stream().filter(b -> b instanceof PodCastCategoryBundle).forEach(e -> podCastCategories.addAll(((PodCastCategoryBundle) e).getBundleItems()));

            bundles.addAll(podCastCatalogBuilder.createFromFetchedData(podCasts, podCastCategories));

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed building catalog " + podCastCatalogBuilder.getPodCastCatalogLang(), e);
            return null;
        }

        return PodCastCatalog.create(podCastCatalogBuilder.getPodCastCatalogLang(), bundles);
    }
}
