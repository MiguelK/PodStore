package com.podcastcatalog.service.podcastcatalog;

import com.podcastcatalog.model.podcastcatalog.*;
import com.podcastcatalog.model.podcastsearch.PodCastEpisodeResultItem;
import com.podcastcatalog.model.podcastsearch.PodCastResultItem;
import com.podcastcatalog.model.podcastsearch.ResultItem;
import com.podcastcatalog.modelbuilder.BundleBuilder;
import com.podcastcatalog.modelbuilder.PodCastCatalogBuilder;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.service.datastore.ServiceDataStorage;
import com.podcastcatalog.util.ServerInfo;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

        String itunesSearchLang = podCastCatalogLanguage ==PodCastCatalogLanguage.SWE ? "SE" : "US";

        //FIXME set lang in search?
        if(encodedQueryParam!=null){
            List<PodCastResultItem> podCasts = ItunesSearchAPI.searchPodCasts("term=" + encodedQueryParam + "&entity=podcast&limit=7&country=" + itunesSearchLang);
            resultItems.addAll(podCasts);
        }

        readLock.lock();
        try {

            List<ResultItem> result;
            if(podCastCatalogLanguage == PodCastCatalogLanguage.SWE){
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

     /*   if(podCastCatalogLanguage==PodCastCatalogLanguage.US){
            LOG.info("Currently no episodeIndex is built for " + podCastCatalogLanguage + " more JVM memory needed");
            return;//FIXME
        }*/

        asyncExecutor.submit(new BuildIndexAction(podCastCatalogLanguage));
    }

    public Future buildPodCastCatalogsAsync(PodCastCatalogLanguage language) {
        validateState();

       /* if(buildingInProgress && language == buildingInProgressLang){
            LOG.info("Building is in progress language=" + language + " try again later.?");
           return null;
        }

        buildingInProgress = true;
        buildingInProgressLang = language; */ //FIXME

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
            //LOG.info(BuildPodCastCatalogAction.class.getSimpleName() + ", registered podCastCatalogBuilders=" + podCastCatalogBuilders.size());

            Map<PodCastCatalogLanguage, PodCastCatalog> newCatalogs = new HashMap<>();
            try {
                for (PodCastCatalogBuilder podCastCatalogBuilder : podCastCatalogBuilders) {

                    if (podCastCatalogBuilder.getPodCastCatalogLang() != podCastCatalogLanguage) {
                        continue; //Only build one catalog = podCastCatalogLanguage
                    }

                    LOG.info("Start building PodCastCatalog " + podCastCatalogBuilder.getPodCastCatalogLang() + " ...");

                    PodCastCatalog catalog = buildPodcastCatalog(podCastCatalogBuilder);

                    LOG.info("Done building PodCastCatalog Lang=" + podCastCatalogBuilder.getPodCastCatalogLang() + " Catalog=" + catalog);

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

            //boolean swedishCatalog = podCastCatalogLanguage == PodCastCatalogLanguage.SWE;

                //FIXME test
                List<PodCastEpisode> podCastEpisodes = bundleItemVisitor.getPodCastEpisodes();
                for (PodCastEpisode podCastEpisode : podCastEpisodes) {
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

        Collections.reverse(bundles);
        return PodCastCatalog.create(podCastCatalogBuilder.getPodCastCatalogLang(), bundles);
    }
}
