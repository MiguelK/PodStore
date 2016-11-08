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

    public void loadPodCastCatalog(PodCastCatalog podCastCatalog) {

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

    private void validateState() {
        if (storage == null) {
            throw new IllegalStateException("Configure storage");
        }
    }

    public void buildPodCastCatalogs() {
        validateState();

        try {
            ayncExecutor.submit(new RebuildCatalogAction()).get(3, TimeUnit. MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Unable to rebuild catalogs ", e);
        }
    }

    public void start() {
        //FIXME validate +
        //Start runner that rebuilds each catalog periodically once a day? //FIXME

    }

    private class RebuildCatalogAction implements Callable<Void> {
        @Override
        public Void call() throws Exception {

            writeLock.lock();
            LOG.info("rebuildCatalogs() registered podCastCatalogBuilders=" + podCastCatalogBuilders.size());

            try {
                for (PodCastCatalogBuilder podCastCatalogBuilder : podCastCatalogBuilders) {
                    LOG.info("Start building PodCastCatalog " + podCastCatalogBuilder.getPodCastCatalogLang() + " ...");

                    PodCastCatalog catalog = buildPodcastCatalog(podCastCatalogBuilder);

                    if (catalog != null) {
                        LOG.info("PodcastCatalo " + podCastCatalogBuilder.getPodCastCatalogLang() + " was updated with new version");
                        storage.save(catalog);
                        podCastCatalogByLang.put(podCastCatalogBuilder.getPodCastCatalogLang(), catalog);
                    }
                }
            } finally {
                writeLock.unlock();
            }
            return null;
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
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed building catalog " + podCastCatalogBuilder.getPodCastCatalogLang(), e);
            return null;
        }

        return PodCastCatalog.create(podCastCatalogBuilder.getPodCastCatalogLang(), bundles);
    }
}
