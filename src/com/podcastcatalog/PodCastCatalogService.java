package com.podcastcatalog;

import com.podcastcatalog.builder.PodCastCatalogBuilder;
import com.podcastcatalog.builder.PodCastCatalogBuilderSE;
import com.podcastcatalog.builder.PodCastCatalogBuilderService;
import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import com.podcastcatalog.store.Storage;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class PodCastCatalogService implements PodCastCatalogBuilderService.Callback {

    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(PodCastCatalogService.class.getName());

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private PodCastCatalogBuilderService podCastCatalogBuilderService = new PodCastCatalogBuilderService();

    private final Map<PodCastCatalogLanguage, PodCastCatalog> podCastCatalogByLang = new HashMap<>();

    private List<PodCastCatalogBuilder> podCastCatalogBuilders = Collections.singletonList(new PodCastCatalogBuilderSE());//FIXME add more?

    public static final PodCastCatalogService INSTANCE = new PodCastCatalogService();

    public static PodCastCatalogService getInstance() {
        return INSTANCE;
    }

    public void startAsync() {

        writeLock.lock();
        LOG.info("Starting " + getClass().getSimpleName() + " podCastCatalogBuilders=" + podCastCatalogBuilders.size());

        try {
            for (PodCastCatalogBuilder podCastCatalogBuilder : podCastCatalogBuilders) {
                PodCastCatalogLanguage podCastCatalogLanguage = podCastCatalogBuilder.getPodCastCatalogLang();
                Optional<PodCastCatalog> podCastCatalog = Storage.load(podCastCatalogLanguage);
                if (podCastCatalog.isPresent()) {
                    podCastCatalogByLang.put(podCastCatalogLanguage, podCastCatalog.get());
                    LOG.info("Loaded PodCastCatalog " + podCastCatalogLanguage + " from storage");
                } else {
                    LOG.info("Start building PodCastCatalog " + podCastCatalogLanguage + " ...");

                    PodCastCatalog catalog = podCastCatalogBuilderService.buildPodcastCatalog(podCastCatalogBuilder);

                    Storage.save(catalog);

                    podCastCatalogByLang.put(podCastCatalogLanguage, catalog);
                }
            }
            //Start runner that rebuilds each catalog periodically once a day?
            podCastCatalogBuilderService.registerListener(this);
        } finally {
            writeLock.unlock();

        }
    }

    @Override
    public void handleNewCatalog(PodCastCatalog podCastCatalog) {
        //FIXME new catalog is recived
        PodCastCatalogLanguage podCastCatalogLanguage = podCastCatalog.getPodCastCatalogLanguage();
        podCastCatalogByLang.put(podCastCatalogLanguage, podCastCatalog);//FIXME lock
    }

    public PodCastCatalog getPodCastCatalog(PodCastCatalogLanguage podCastCatalogLanguage) {
        return podCastCatalogByLang.get(podCastCatalogLanguage);
    }

    public List<PodCast> search(String text) {

        readLock.lock();
        try {
            //Index search //FIXME
//            ItunesSearchAPI searchAPI = ItunesSearchAPI.search(text); //Callable ?
//            executorService.submit()

            return null;

        } finally {
            readLock.unlock();
        }
    }
}
