package com.podcastcatalog.service.podcastcatalog;

import com.podcastcatalog.model.PodCastCatalogMetaData;
import com.podcastcatalog.model.podcastcatalog.*;
import com.podcastcatalog.model.podcastsearch.PodCastEpisodeResultItem;
import com.podcastcatalog.model.podcastsearch.PodCastResultItem;
import com.podcastcatalog.model.podcastsearch.PodCastTitle;
import com.podcastcatalog.model.podcastsearch.ResultItem;
import com.podcastcatalog.modelbuilder.BundleBuilder;
import com.podcastcatalog.modelbuilder.PodCastCatalogBuilder;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.modelbuilder.collector.itunes.PodCastIdCollector;
import com.podcastcatalog.service.search.SearchTerm;
import com.podcastcatalog.service.subscription.FtpOneClient;
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

    private final static Logger LOG = Logger.getLogger(PodCastCatalogService.class.getName());

    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();
    private final Map<PodCastCatalogLanguage, PodCastCatalog> podCastCatalogByLang = new HashMap<>();
    private final Map<PodCastCatalogLanguage, PodCastCatalogMetaData> podCastCatalogMetaDataLang = new HashMap<>();

    private final ExecutorService bundleBuilderExecutor = Executors.newFixedThreadPool(2);
    private final ExecutorService podCastCatalogUpdateExecutor = Executors.newFixedThreadPool(1);

    private static final PodCastCatalogService INSTANCE = new PodCastCatalogService();

    public static PodCastCatalogService getInstance() {
        return INSTANCE;
    }

    public void register(PodCastCatalogLanguage podCastCatalogLanguage, PodCastCatalogMetaData podCastCatalogMetaData) {
        writeLock.lock();
        try {
            LOG.info("Register MetaData (" + podCastCatalogLanguage.name() + ") " + podCastCatalogMetaData);
            podCastCatalogMetaDataLang.put(podCastCatalogLanguage, podCastCatalogMetaData);
        } finally {
            writeLock.unlock();
        }
    }

    private PodCastCatalogMetaData getPodCastCatalogMetaData(PodCastCatalogLanguage podCastCatalogLanguage) {
        readLock.lock();
        try {
            return podCastCatalogMetaDataLang.get(podCastCatalogLanguage);
        } finally {
            readLock.unlock();
        }
    }

    public List<PodCastTitle> getPodCastTitles(PodCastCatalogLanguage podCastCatalogLanguage) {

        PodCastCatalogMetaData podCastCatalogMetaData = getPodCastCatalogMetaData(podCastCatalogLanguage);
        if(podCastCatalogMetaData == null) {
            return Collections.emptyList();
        }
        return podCastCatalogMetaData.podCastTitles;
    }

    public List<PodCastTitle> getPodCastTitlesTrending(PodCastCatalogLanguage podCastCatalogLanguage) {

        PodCastCatalogMetaData podCastCatalogMetaData = getPodCastCatalogMetaData(podCastCatalogLanguage);
        if(podCastCatalogMetaData == null) {
            return Collections.emptyList();
        }
        return podCastCatalogMetaData.podCastTitlesTrending;
    }

    public List<SearchTerm> getPopularSearchTerms(PodCastCatalogLanguage podCastCatalogLanguage) {

        PodCastCatalogMetaData podCastCatalogMetaData = getPodCastCatalogMetaData(podCastCatalogLanguage);
        if(podCastCatalogMetaData == null) {
            return Collections.emptyList();
        }
        return podCastCatalogMetaData.popularSearchQueries;
    }



    public PodCastCatalog getPodCastCatalog(PodCastCatalogLanguage podCastCatalogLanguage) {
        readLock.lock();
        try {
            return podCastCatalogByLang.get(podCastCatalogLanguage);
        } finally {
            readLock.unlock();
        }
    }

    public void addPopularSearchTerm(PodCastCatalogLanguage lang, String searchTerm) {


        List<SearchTerm> updatedList = null;
        PodCastCatalogMetaData podCastCatalogMetaData = null;

        readLock.lock();
        try {

            podCastCatalogMetaData = podCastCatalogMetaDataLang.get(lang);

            if (podCastCatalogMetaData == null) {
                return;
            }

            List<SearchTerm> popular = new ArrayList<>(podCastCatalogMetaData.popularSearchQueries);

            Optional<SearchTerm> first =
                    popular.stream().filter(r -> searchTerm.equalsIgnoreCase(r.getTerm())).findFirst();

            if (first.isPresent()) {
                first.get().increaseCounter();
            } else {
                popular.add(new SearchTerm(0, searchTerm));
            }

            Collections.sort(popular);
            if (popular.size() >= 100) {
                updatedList = popular.subList(0, popular.size() - 1);
            }

        } finally {
            readLock.unlock();
        }

        if (updatedList == null) {
            return;
        }

        writeLock.lock();

        try {
            podCastCatalogMetaData.popularSearchQueries = updatedList;
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

        String encodedQueryParam;
        try {
            encodedQueryParam = URLEncoder.encode(queryParam, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.warning("Failed to encode search param " + queryParam);
            return Collections.emptyList();
        }

        String itunesSearchLang = podCastCatalogLanguage.name();

        List<PodCastResultItem> podCasts = ItunesSearchAPI.searchPodCasts("term=" + encodedQueryParam + "&entity=podcast&limit=12&country=" + itunesSearchLang);
        resultItems.addAll(podCasts);

        readLock.lock();
        try {

            PodCastCatalogMetaData podCastCatalogMetaData = podCastCatalogMetaDataLang.get(podCastCatalogLanguage);
            if (podCastCatalogMetaData != null) {
                List<ResultItem> result = podCastCatalogMetaData.textSearchIndex.lookup(queryParam);
                resultItems.addAll(result);
            }

            Collections.sort(resultItems, ResultItem.SORT_BY_POD_CAST_NAME);

            return resultItems;
        } finally {
            readLock.unlock();
        }
    }

    public void buildPodCastCatalogsAsync(PodCastCatalogBuilder podCastCatalogBuilder) {
        podCastCatalogUpdateExecutor.submit(new BuildPodCastCatalogAction(podCastCatalogBuilder));
    }

    //1. Build InMemoryCatalog for the lang
    //2. Index +search suggestions0trending
    //3 export to one.com
    //4 register MetaData
    private class BuildPodCastCatalogAction implements Runnable {
        private PodCastCatalogBuilder podCastCatalogBuilder;

        private BuildPodCastCatalogAction(PodCastCatalogBuilder podCastCatalogBuilder) {
            this.podCastCatalogBuilder = podCastCatalogBuilder;
        }

        @Override
        public void run() {
            readLock.lock();

            try {
                PodCastCatalog podCastCatalog;
                PodCastCatalogLanguage podCastCatalogLang = podCastCatalogBuilder.getPodCastCatalogLang();
                try {
                    LOG.info("Start building PodCastCatalog " + podCastCatalogLang + " ...");
                    podCastCatalog = buildPodCastCatalog(podCastCatalogBuilder);
                    LOG.info("Done building PodCastCatalog Lang=" + podCastCatalogLang + " Catalog=" + podCastCatalog);
                    if (podCastCatalog == null) {
                        LOG.warning("Failed building PodCastCatalog Lang=" + podCastCatalogLang);
                        return;
                    }
                    FtpOneClient.getInstance().upload(podCastCatalog, podCastCatalogLang);
                } finally {
                    readLock.unlock();
                }

                List<PodCastEpisode> podCastEpisodes = new ArrayList<>();
                TextSearchIndex<ResultItem> newTextSearchIndex = new TextSearchIndex<>();

                for (Bundle bundle : podCastCatalog.getBundles()) {
                    if (bundle instanceof PodCastBundle) {
                        PodCastBundle podCastBundle = (PodCastBundle) bundle;
                        for (PodCast podCast : podCastBundle.getBundleItems()) {

                            PodCastResultItem podCastResultItem = new PodCastResultItem(podCast.getCollectionId(), podCast.getTitle(), podCast.getArtworkUrl600());
                            String text = podCast.getTitle() + " " + podCast.getDescription();
                            newTextSearchIndex.addText(text, TextSearchIndex.Prio.HIGHEST, podCastResultItem);

                            List<PodCastEpisode> podCastEpisodesInternal = podCast.getPodCastEpisodesInternal();
                            podCastEpisodesInternal.forEach(podCastEpisode -> podCastEpisode.setArtworkUrl600(podCast.getArtworkUrl600()));
                            podCastEpisodes.addAll(podCastEpisodesInternal);
                        }
                    }
                }

                LOG.info("Start TextSearchIndex " + podCastEpisodes.size() + " podCastEpisodes");

                for (PodCastEpisode podCastEpisode : podCastEpisodes) {
                    PodCastEpisodeResultItem resultItem = new PodCastEpisodeResultItem(podCastEpisode);
                    //// FIXME Need more JVM memory to index?
                    String text = podCastEpisode.getTitle() + " " + podCastEpisode.getDescription();
                    newTextSearchIndex.addText(text, TextSearchIndex.Prio.HIGH, resultItem);
                }
                newTextSearchIndex.buildIndex();

                //Export + update MetaData...
                writeLock.lock();
                try {
                    PodCastCatalogMetaData podCastCatalogMetaData =
                            podCastCatalogMetaDataLang.computeIfAbsent(podCastCatalogLang, podCastCatalogLanguage -> new PodCastCatalogMetaData());

                    podCastCatalogMetaData.textSearchIndex = newTextSearchIndex;

                    List<PodCastTitle> podCastTitles = new ArrayList<>();
                    List<PodCastTitle> podCastTitlesTrending = new ArrayList<>();

                    LOG.info("Start building SearchSuggestions + trending pods for lang=" + podCastCatalogLang);

                    for (PodCastIdCollector.Category categoryName : PodCastIdCollector.Category.values()) {
                        List<Long> ids = PodCastIdCollector.createPodCastIdCollector(podCastCatalogLang, categoryName).getPodCastIds();
                        List<ItunesSearchAPI.PodCastSearchResult.Row> podCastTitlesRows = ItunesSearchAPI.lookupPodCastsByIds(ids);

                        PodCastTitle podCastTitleTrending = null; //Register 1 per category toplist
                        for (ItunesSearchAPI.PodCastSearchResult.Row row : podCastTitlesRows) {
                            String trimmedTitle = StringUtils.trimToNull(row.getCollectionName());
                            if (trimmedTitle == null) {
                                continue;
                            }

                            PodCastTitle podCastTitle = new PodCastTitle(trimmedTitle);

                            if (podCastTitles.contains(podCastTitle)) {
                                continue;
                            }

                            podCastTitles.add(podCastTitle);

                            if (podCastTitleTrending == null) {
                                podCastTitlesTrending.add(podCastTitle);
                                podCastTitleTrending = podCastTitle;
                            }
                        }
                    }
                    podCastCatalogMetaData.podCastTitles = podCastTitles;
                    podCastCatalogMetaData.podCastTitlesTrending = podCastTitlesTrending;

                    FtpOneClient.getInstance().upload(podCastCatalogMetaData, podCastCatalogLang);
                    PodCastCatalogService.getInstance().register(podCastCatalogLang, podCastCatalogMetaData);
                } finally {
                    writeLock.unlock();
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "BuildPodCastCatalogAction: Failed building catalog ", e);
            }
        }
    }

    private PodCastCatalog buildPodCastCatalog(PodCastCatalogBuilder podCastCatalogBuilder) {

        Set<BundleBuilder> bundleBuilders = podCastCatalogBuilder.getBundleBuilders();
        List<Bundle> bundles = new ArrayList<>();
        try {
            List<Future<Bundle>> futureBundles = bundleBuilderExecutor.invokeAll(bundleBuilders);
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
