package com.podcastcatalog;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.api.response.bundle.Bundle;
import com.podcastcatalog.builder.BundleBuilder;
import com.podcastcatalog.builder.PodCastCatalogBuilder;
import com.podcastcatalog.store.Storage;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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

    public static final PodCastCatalogService INSTANCE = new PodCastCatalogService();

    private PodCastCatalogService() {
        podCastCatalogBuilders = new ArrayList<>();
        podCastCatalogByLang = new HashMap<>();
        executorService = Executors.newSingleThreadExecutor();
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

    public void startLoadCatalogs() {
        //FIXME valoidate state not asynch
        if(storage==null){
            throw new IllegalStateException("Configure storage");
        }

        writeLock.lock();
        LOG.info("startLoadCatalogs() podCastCatalogBuilders=" + podCastCatalogBuilders.size());

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

                    storage.save(catalog);

                    podCastCatalogByLang.put(lang, catalog);
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

    public PodCastCatalog buildPodcastCatalog(PodCastCatalogBuilder podCastCatalogBuilder){

        Set<BundleBuilder> bundles = podCastCatalogBuilder.getBundleBuilders();
        List<Bundle> podCastBundle1s = invoke(bundles);

        return PodCastCatalog.create(podCastCatalogBuilder.getPodCastCatalogLang(), podCastBundle1s);
    }

    private List<Bundle> invoke(Set<BundleBuilder>  tasks){
        List<Bundle> podCastBundle1s = new ArrayList<>();
        try {
            List<Future<Bundle>> futures = executorService.invokeAll(tasks);
            for (Future<Bundle> future : futures) {
                Bundle podCastBundle1 = future.get();
                podCastBundle1s.add(podCastBundle1);
            }
        } catch (Exception e) {
            e.printStackTrace();//FIXME
        }
        return podCastBundle1s;
    }

}
