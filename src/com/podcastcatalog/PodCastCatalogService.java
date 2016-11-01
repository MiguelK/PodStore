package com.podcastcatalog;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.builder.PodCastCatalogBuilder;
import com.podcastcatalog.builder.PodCastCatalogBuilderService;
import com.podcastcatalog.store.Storage;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class PodCastCatalogService {

    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(PodCastCatalogService.class.getName());

    private PodCastCatalogBuilderService podCastCatalogBuilderService;
    private final Map<PodCastCatalogLanguage, PodCastCatalog> podCastCatalogByLang;
    private final List<PodCastCatalogBuilder> podCastCatalogBuilders;
    private Storage storage;

    public static final PodCastCatalogService INSTANCE = new PodCastCatalogService();

    private PodCastCatalogService() {
        podCastCatalogBuilders = new ArrayList<>();
        podCastCatalogByLang = new HashMap<>();
        podCastCatalogBuilderService = new PodCastCatalogBuilderService();
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

    public void startAsync() {
        //FIXME valoidate state not asynch

        writeLock.lock();
        LOG.info("startAsync() podCastCatalogBuilders=" + podCastCatalogBuilders.size());

        try {
            for (PodCastCatalogBuilder podCastCatalogBuilder : podCastCatalogBuilders) {

                PodCastCatalogLanguage lang = podCastCatalogBuilder.getPodCastCatalogLang();

                Optional<PodCastCatalog> podCastCatalog = storage.load(lang);

                if (podCastCatalog.isPresent()) {
                    podCastCatalogByLang.put(lang, podCastCatalog.get());
                    LOG.info("Loaded PodCastCatalog " + lang + " from storage");
                } else {
                    LOG.info("Start building PodCastCatalog " + lang + " ...");

                    PodCastCatalog catalog = podCastCatalogBuilderService.buildPodcastCatalog(podCastCatalogBuilder);

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
}
