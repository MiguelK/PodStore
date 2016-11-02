package com.podcastcatalog;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.api.response.bundle.Bundle;
import com.podcastcatalog.builder.BundleBuilder;
import com.podcastcatalog.builder.PodCastCatalogBuilder;
import com.podcastcatalog.store.Storage;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PodCastCatalogService {

    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(PodCastCatalogService.class.getName());

    private final Map<PodCastCatalogLanguage, PodCastCatalog> podCastCatalogByLang;
    private final List<PodCastCatalogBuilder> podCastCatalogBuilders;
    private Storage storage;
    private final ExecutorService executorService;
    private final ExecutorService ayncExecutor;

    private static final PodCastCatalogService INSTANCE = new PodCastCatalogService();

    private PodCastCatalogService() {
        podCastCatalogBuilders = new ArrayList<>();
        podCastCatalogByLang = new HashMap<>();
        ayncExecutor = Executors.newFixedThreadPool(5);
        executorService = Executors.newSingleThreadExecutor();//Important single thread!
    }

    public static PodCastCatalogService getInstance() {
        return INSTANCE;
    }

    public void registerPodCastCatalogBuilder(PodCastCatalogBuilder builder) {
        podCastCatalogBuilders.add(builder);
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public void buildPodCastCatalogsAsync() {
        validateState();

        ayncExecutor.submit(new RebuildCatalogAction());
    }

    private void validateState() {
        if (storage == null) {
            throw new IllegalStateException("Configure storage");
        }
    }

    public void buildPodCastCatalogs() {
        validateState();

        try {
            ayncExecutor.submit(new RebuildCatalogAction()).get(60,TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Unable to rebuild catalogs ", e);
        }
    }

    private class RebuildCatalogAction implements Callable<Void> {
        @Override
        public Void call() throws Exception {
            rebuildCatalogs();
            return null;
        }
    }

    private void rebuildCatalogs() {

        writeLock.lock();
        LOG.info("buildPodCastCatalogs() podCastCatalogBuilders=" + podCastCatalogBuilders.size());

        try {
            for (PodCastCatalogBuilder podCastCatalogBuilder : podCastCatalogBuilders) {

                PodCastCatalogLanguage lang = podCastCatalogBuilder.getPodCastCatalogLang();

                Optional<PodCastCatalog> podCastCatalog = storage.load(lang);

                if (podCastCatalog.isPresent()) {
                    podCastCatalogByLang.put(lang, podCastCatalog.get());
                    LOG.info("Loaded PodCastCatalog " + lang + " from storage");
                } else {
                    LOG.info("Start building PodCastCatalog " + lang + " ...");

                    PodCastCatalog catalog = buildPodcastCatalog(podCastCatalogBuilder);

                    if (catalog != null) {
                        storage.save(catalog);
                        podCastCatalogByLang.put(lang, catalog);
                    }
                }
            }
            //Start runner that rebuilds each catalog periodically once a day? //FIXME
        } finally {
            writeLock.unlock();
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
            List<Future<Bundle>> futures = executorService.invokeAll(bundleBuilders);
            for (Future<Bundle> future : futures) {
                Bundle bundle = future.get();//FIXME Max timeout??
                bundles.add(bundle);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed building catalog " + podCastCatalogBuilder.getPodCastCatalogLang(), e);
            return null;
        }

        return PodCastCatalog.create(podCastCatalogBuilder.getPodCastCatalogLang(), bundles);
    }
}
