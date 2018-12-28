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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    private static final int THREADS = 25;
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(PodCastCatalogService.class.getName());

    private final Map<PodCastCatalogLanguage, PodCastCatalog> podCastCatalogByLang;
    private final List<PodCastCatalogBuilder> podCastCatalogBuilders;

    private TextSearchIndex<ResultItem> podCastEpisodeIndexSWE; //FIXME One per language, podCastEpisodeIndexSWE
    private TextSearchIndex<ResultItem> podCastEpisodeIndexUS; //FIXME One per language, podCastEpisodeIndexSWE
    private final PodCastIndex podCastCatalogIndex;
   // private final PodCastIndex podCastIndex;
    private ServiceDataStorage storage;
    private final ExecutorService executorService;
    private final ExecutorService asyncExecutor;
    private volatile boolean buildingInProgress;
    private volatile PodCastCatalogLanguage buildingInProgressLang;

    private static final PodCastCatalogService INSTANCE = new PodCastCatalogService();

    private PodCastCatalogService() {
        podCastCatalogIndex = PodCastIndex.create();
     //   podCastIndex = PodCastIndex.create();
        podCastEpisodeIndexSWE = new TextSearchIndex<>();
        podCastEpisodeIndexUS = new TextSearchIndex<>();
        podCastCatalogBuilders = new ArrayList<>();
        podCastCatalogByLang = new HashMap<>();
        asyncExecutor = Executors.newFixedThreadPool(1); //Single builder Thread memory issue
        executorService = Executors.newFixedThreadPool(5);//Important single thread!
    }

    public static PodCastCatalogService getInstance() {
        return INSTANCE;
    }

    public Optional<PodCast> getPodCastById(String id) {
        readLock.lock();
        try {
            //podCastIndex updated every 10th minute @see PodCastSubscriptionUpdater.java
           /* Optional<PodCast> podCast = podCastIndex.lookup(id);
            if (podCast.isPresent()) {
                return podCast;
            }*/

            return podCastCatalogIndex.lookup(id);//All podCast inMemory from the inMemory catalog(s)
        } finally {
            readLock.unlock();
        }
    }

    /*public void updatePodCastIndex(PodCast podCast) {
        writeLock.lock();

        try {
            podCastIndex.update(podCast);
        } finally {
            writeLock.unlock();
        }
    }*/

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
            return podCastEpisodeIndexSWE.getStatus() + " US=" + podCastEpisodeIndexUS.getStatus();
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
        try {
            podCastCatalogByLang.put(podCastCatalog.getPodCastCatalogLanguage(), podCastCatalog);
        } finally {
            writeLock.unlock();
        }
    }


    public List<ResultItem> search(PodCastCatalogLanguage podCastCatalogLanguage, String queryParam) {

        String queryParamTrimmed = StringUtils.trimToNull(queryParam);
        if (queryParamTrimmed == null) {
            return Collections.emptyList();
        }

        //FIXME Sort algoritm? limit 5 etc...
        List<ResultItem> resultItems = new ArrayList<>();

        String encodedQueryParam = null;
        try {
            encodedQueryParam = URLEncoder.encode(queryParam, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.warning("Failed to encode search param " + queryParam);
        }

        String itunesSearchLang = podCastCatalogLanguage.name();

        //FIXME set lang in search?
        if(encodedQueryParam!=null){
            List<PodCastResultItem> podCasts = ItunesSearchAPI.searchPodCasts("term=" + encodedQueryParam + "&entity=podcast&limit=12&country=" + itunesSearchLang);
            resultItems.addAll(podCasts);
        }

        readLock.lock();
        try {

            List<ResultItem> result;
            if(podCastCatalogLanguage == PodCastCatalogLanguage.SE){
                 result = podCastEpisodeIndexSWE.lookup(queryParam);
            } else {
                result = podCastEpisodeIndexUS.lookup(queryParam);

            }

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

        PodCastCatalogBuilder podCastCatalogBuilder = podCastCatalogBuilders.stream().filter(b -> b.getPodCastCatalogLang() == language
        ).findFirst().get();

        return asyncExecutor.submit(new BuildPodCastCatalogAction(podCastCatalogBuilder));
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

        private PodCastCatalogBuilder podCastCatalogBuilder;

        BuildPodCastCatalogAction(PodCastCatalogBuilder podCastCatalogBuilder) {
            this.podCastCatalogBuilder = podCastCatalogBuilder;
        }

        @Override
        public void run() {

            readLock.lock();

            PodCastCatalog catalog;
            PodCastCatalogLanguage podCastCatalogLang = podCastCatalogBuilder.getPodCastCatalogLang();

            try {
                    LOG.info("Start building PodCastCatalog " + podCastCatalogLang + " ...");

                    catalog = buildPodcastCatalog(podCastCatalogBuilder);

                    LOG.info("Done building PodCastCatalog Lang=" + podCastCatalogLang + " Catalog=" + catalog);

                    if (catalog != null) {
                        storage.save(catalog);
                    }
            } finally {
                readLock.unlock();
            }

            writeLock.lock();
            try {

                if (catalog != null && podCastCatalogLang.isInMemory()) { //FIXME can work with memory?
                    LOG.info("PodCastCatalog " + podCastCatalogLang + " was updated with new version");
                    podCastCatalogByLang.put(podCastCatalogLang, catalog);
                }

            } finally {
                writeLock.unlock();
            }

            if(podCastCatalogLang.isInMemoryIndex()) { //FIXME can work with memory?
                buildIndexAsync(podCastCatalogLang);
            }

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

            try{

            BundleItemVisitor bundleItemVisitor = new BundleItemVisitor();

                LOG.info("Build Index Action " + podCastCatalogLanguage);
            readLock.lock();
            try {
                for (Map.Entry<PodCastCatalogLanguage, PodCastCatalog> catalogEntry : podCastCatalogByLang.entrySet()) {

                    if (catalogEntry.getKey() != podCastCatalogLanguage) {
                        continue; //Only buidl for one Language at a time :)
                    }

                    LOG.info("Start building search index : " + podCastCatalogLanguage);

                    for (Bundle bundle : catalogEntry.getValue().getBundles()) {
                        for (BundleItem bundleItem : bundle.getBundleItems()) {
                            bundleItem.accept(bundleItemVisitor);
                        }
                    }
                }

            } finally {
                readLock.unlock();
            }

            TextSearchIndex<ResultItem> newTextSearchIndex = new TextSearchIndex<>();

            //boolean swedishCatalog = podCastCatalogLanguage == PodCastCatalogLanguage.SE;

                //FIXME test
                List<PodCastEpisode> podCastEpisodes = bundleItemVisitor.getPodCastEpisodes();
                for (PodCastEpisode podCastEpisode : podCastEpisodes) {
                    //FIXME add Podcast titil + imageURL

                    PodCastEpisodeResultItem resultItem = new PodCastEpisodeResultItem(podCastEpisode);
                    String text = podCastEpisode.getTitle() ;// FIXME Need more JVM memory to index? + " " + podCastEpisode.getDescription();
                    newTextSearchIndex.addText(text, TextSearchIndex.Prio.HIGH, resultItem);
                }

                newTextSearchIndex.buildIndex();

                writeLock.lock();
                try {

              if(podCastCatalogLanguage == PodCastCatalogLanguage.US) { //ServerInfo.isUSMode()){
                  podCastEpisodeIndexUS = newTextSearchIndex;
                  LOG.info("Done building podCastEpisodeIndexUS=" + podCastEpisodeIndexUS.getStatus());
              } else {
                  podCastEpisodeIndexSWE = newTextSearchIndex;
                  LOG.info("Done building podCastEpisodeIndexSWE=" + podCastEpisodeIndexSWE.getStatus());
                  podCastCatalogIndex.buildIndex(bundleItemVisitor.getPodCasts()); //Only update for swe
              }

            } finally {
                writeLock.unlock();
            }

            }catch (Exception e){
                LOG.log(Level.SEVERE,"Error " + BuildIndexAction.class.getSimpleName(), e);

            }
        }
    }

    private PodCastCatalog buildPodcastCatalog(PodCastCatalogBuilder podCastCatalogBuilder) {

        Set<BundleBuilder> bundleBuilders = podCastCatalogBuilder.getBundleBuilders();
        List<Bundle> bundles = new ArrayList<>();
        try {
            List<Future<Bundle>> futureBundles = executorService.invokeAll(bundleBuilders);
            for (Future<Bundle> futureBundle : futureBundles) {
                Bundle bundle = futureBundle.get(10, TimeUnit.MINUTES);//FIXME Max timeout??
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

        Collections.reverse(bundles);
        return PodCastCatalog.create(podCastCatalogBuilder.getPodCastCatalogLang(), bundles);
    }
}
