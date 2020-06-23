package com.podcastcatalog.service.podcastcatalog;

import com.podcastcatalog.model.PodCastCatalogMetaData;
import com.podcastcatalog.model.podcastcatalog.*;
import com.podcastcatalog.model.podcastsearch.PodCastEpisodeResultItem;
import com.podcastcatalog.model.podcastsearch.PodCastResultItem;
import com.podcastcatalog.model.podcastsearch.PodCastInfo;
import com.podcastcatalog.model.podcastsearch.ResultItem;
import com.podcastcatalog.model.podcastsearch.ResultType;
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
import java.util.stream.Collectors;

public class PodCastCatalogService {

    private final static Logger LOG = Logger.getLogger(PodCastCatalogService.class.getName());

    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();
    private final Map<PodCastCatalogLanguage, PodCastCatalogMetaData> podCastCatalogMetaDataLang = new HashMap<>();

    private final ExecutorService bundleBuilderExecutor = Executors.newFixedThreadPool(10);
    private final ExecutorService podCastCatalogUpdateExecutor = Executors.newFixedThreadPool(1);

    private static final PodCastCatalogService INSTANCE = new PodCastCatalogService();

    public static PodCastCatalogService getInstance() {
        return INSTANCE;
    }

    public void register(PodCastCatalogLanguage podCastCatalogLanguage, PodCastCatalogMetaData podCastCatalogMetaData) {
        writeLock.lock();
        try {
            podCastCatalogMetaDataLang.put(podCastCatalogLanguage, podCastCatalogMetaData);

            LOG.info("Register MetaData (" + podCastCatalogLanguage.name() + ") " + podCastCatalogMetaData
                    + ",loaded (" + podCastCatalogMetaDataLang.size() +
                    " of " + PodCastCatalogLanguage.values().length + ")");

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

    public List<PodCastInfo> getPodCastTitles(PodCastCatalogLanguage podCastCatalogLanguage) {

        PodCastCatalogMetaData podCastCatalogMetaData = getPodCastCatalogMetaData(podCastCatalogLanguage);
        if (podCastCatalogMetaData == null) {
            return Collections.emptyList();
        }
        return podCastCatalogMetaData.podCastTitles;
    }

    public List<PodCastInfo> getPodCastTitlesTrending(PodCastCatalogLanguage podCastCatalogLanguage) {

        PodCastCatalogMetaData podCastCatalogMetaData = getPodCastCatalogMetaData(podCastCatalogLanguage);
        if (podCastCatalogMetaData == null) {
            return Collections.emptyList();
        }
        return podCastCatalogMetaData.podCastTitlesTrending;
    }

    public List<SearchTerm> getPopularSearchTerms(PodCastCatalogLanguage podCastCatalogLanguage) {

        PodCastCatalogMetaData podCastCatalogMetaData = getPodCastCatalogMetaData(podCastCatalogLanguage);
        if (podCastCatalogMetaData == null) {
            return Collections.emptyList();
        }
        return podCastCatalogMetaData.popularSearchQueries;
    }


    public List<ResultItem> search(PodCastCatalogLanguage podCastCatalogLanguage, String queryParam) {

        String queryParamTrimmed = StringUtils.trimToNull(queryParam);
        if (queryParamTrimmed == null) {
            return Collections.emptyList();
        }

        String encodedQueryParam;
        try {
            encodedQueryParam = URLEncoder.encode(queryParam, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.warning("Failed to encode search param " + queryParam);
            return Collections.emptyList();
        }

        Set<ResultItem> resultItems = new HashSet<>();

        readLock.lock();
        try {

            //1# First search in local podCastCatalogMetaDataLang
            PodCastCatalogMetaData podCastCatalogMetaData = podCastCatalogMetaDataLang.get(podCastCatalogLanguage);
            if (podCastCatalogMetaData != null) {
                List<ResultItem> podCastEpisodeResults = podCastCatalogMetaData.textSearchIndex.lookupPodCast(queryParam,10);
                resultItems.addAll(podCastEpisodeResults);

                List<ResultItem> podCastResults = podCastCatalogMetaData.textSearchIndex.lookupPodCastEpisodes(queryParam,10);
                resultItems.addAll(podCastResults);
            }
        } finally {
            readLock.unlock();
        }

        String limit = "limit=" + (resultItems.size() >= 20 ? 6 : 12);
        String parameters = "term="
                + encodedQueryParam + "&entity=podcast&" + limit + "&attribute=titleTerm&country=" + podCastCatalogLanguage.name();

        if (encodedQueryParam.length() >= 6) {
            //No attribute=titleTerm
            parameters = "term="
                    + encodedQueryParam + "&entity=podcast&" + limit;
        } else if (encodedQueryParam.length() >= 12) {
            parameters = "term="
                    + encodedQueryParam + "&entity=podcast&" + limit;
        }

        List<PodCastResultItem> podCasts = ItunesSearchAPI.searchPodCasts(parameters);
        resultItems.addAll(podCasts);


        return new ArrayList<>(resultItems);
    }

    public void buildPodCastCatalogsAsync(PodCastCatalogBuilder podCastCatalogBuilder) {
        podCastCatalogUpdateExecutor.submit(new BuildPodCastCatalogAction(podCastCatalogBuilder));
    }

    public boolean isMetaDataRegistered(PodCastCatalogLanguage language) {
        return getPodCastCatalogMetaData(language) != null;
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

            try {
                PodCastCatalogLanguage podCastCatalogLang = podCastCatalogBuilder.getPodCastCatalogLang();
                LOG.info("Start building PodCastCatalog " + podCastCatalogLang + " ...");
                PodCastCatalog podCastCatalog = buildPodCastCatalog(podCastCatalogBuilder);
                LOG.info("Done building PodCastCatalog Lang=" + podCastCatalogLang + " Catalog=" + podCastCatalog);
                if (podCastCatalog == null) {
                    LOG.warning("Failed building PodCastCatalog Lang=" + podCastCatalogLang);
                    return;
                }
                FtpOneClient.getInstance().upload(podCastCatalog);

                Set<PodCast> podCasts = new HashSet<>();
                Set<PodCastEpisode> podCastEpisodes = new HashSet<>();

                TextSearchIndex newTextSearchIndex = new TextSearchIndex();

                for (Bundle bundle : podCastCatalog.getBundles()) {
                    if (bundle instanceof PodCastCategoryBundle) {
                        List<PodCast> collect = ((PodCastCategoryBundle) bundle).getBundleItems().stream()
                                .map(PodCastCategory::getPodCasts).flatMap(Collection::stream)
                                .collect(Collectors.toList());
                        podCasts.addAll(collect);
                    } else if (bundle instanceof PodCastBundle) {
                        List<PodCast> bundleItems = new ArrayList<>(((PodCastBundle) bundle).getBundleItems());
                        podCasts.addAll(bundleItems);
                    }
                }

                List<PodCast> noVirtualPodCasts = new ArrayList<>();
                for (PodCast podCast : podCasts) {
                    if(podCast.isVirtualPodCast()) {
                        continue;
                    }
                    noVirtualPodCasts.add(podCast);
                }

                for (PodCast podCast : noVirtualPodCasts) {
                        List<PodCastEpisode> podCastEpisodesInternal = podCast.getPodCastEpisodesInternal();
                        podCastEpisodesInternal.forEach(podCastEpisode -> podCastEpisode.setArtworkUrl600(podCast.getArtworkUrl600()));
                        podCastEpisodes.addAll(podCastEpisodesInternal);
                }

                for (PodCast podCast : noVirtualPodCasts) {
                        PodCastResultItem podCastResultItem = new PodCastResultItem(podCast.getCollectionId(),
                                podCast.getTitle(), podCast.getArtworkUrl600());
                        String text = podCast.getTitle() + " " + podCast.getDescription(); //FIXME?
                        newTextSearchIndex.addText(text, podCastResultItem);
                    }


                LOG.info("Start TextSearchIndex noVirtualPodCasts=" + noVirtualPodCasts.size() + ", podCastEpisodes=" + podCastEpisodes.size());

                List<PodCastEpisode> episodesToIndex = new ArrayList<>(podCastEpisodes);

                for (PodCastEpisode podCastEpisode : episodesToIndex) {
                    PodCastEpisodeResultItem resultItem = new PodCastEpisodeResultItem(podCastEpisode);
                    //// FIXME Need more JVM memory to index?
                    String text = podCastEpisode.getTitle();//+ podCastEpisode.getDescription();
                    newTextSearchIndex.addText(text, resultItem);
                }
                newTextSearchIndex.buildIndex();

                //Export + update MetaData...
                List<PodCastInfo> podCastTitles = new ArrayList<>();
                List<PodCastInfo> podCastTitlesTrending = new ArrayList<>();

                LOG.info("Start building SearchSuggestions + trending pods for lang=" + podCastCatalogLang);

                for (PodCastIdCollector.Category categoryName : PodCastIdCollector.Category.values()) {
                    List<Long> ids = PodCastIdCollector.createPodCastIdCollector(podCastCatalogLang, categoryName).getPodCastIds();
                    List<ItunesSearchAPI.PodCastSearchResult.Row> podCastTitlesRows = ItunesSearchAPI.lookupPodCastsByIds(ids);

                    PodCastInfo podCastTitleTrending = null; //Register 1 per category toplist
                    for (ItunesSearchAPI.PodCastSearchResult.Row row : podCastTitlesRows) {
                        String collectionId = row.getCollectionId();
                        if (collectionId == null) {
                            continue;
                        }

                        PodCastInfo podCastInfo = new PodCastInfo(row.getCollectionId(), row.getCollectionName());

                        if (podCastTitles.contains(podCastInfo)) {
                            continue;
                        }

                        podCastTitles.add(podCastInfo);

                        if (podCastTitleTrending == null) {
                            podCastTitlesTrending.add(podCastInfo);
                            podCastTitleTrending = podCastInfo;
                        }
                    }
                }

                PodCastCatalogMetaData podCastCatalogMetaData;
                writeLock.lock();
                try {
                    podCastCatalogMetaData =
                            podCastCatalogMetaDataLang.computeIfAbsent(podCastCatalogLang, podCastCatalogLanguage -> new PodCastCatalogMetaData());
                    podCastCatalogMetaData.textSearchIndex = newTextSearchIndex;
                    podCastCatalogMetaData.podCastTitles = podCastTitles;
                    podCastCatalogMetaData.podCastTitlesTrending = podCastTitlesTrending;
                } finally {
                    writeLock.unlock();
                }

                FtpOneClient.getInstance().upload(podCastCatalogMetaData, podCastCatalogLang);
                register(podCastCatalogLang, podCastCatalogMetaData);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "BuildPodCastCatalogAction: Failed building catalog lang= "
                        + podCastCatalogBuilder.getPodCastCatalogLang(), e);
            }
        }
    }

    private PodCastCatalog buildPodCastCatalog(PodCastCatalogBuilder podCastCatalogBuilder) {

        Set<BundleBuilder> bundleBuilders = podCastCatalogBuilder.getBundleBuilders();
        List<Bundle> bundles = new ArrayList<>();
        try {
            List<Future<Bundle>> futureBundles = bundleBuilderExecutor.invokeAll(bundleBuilders);
            for (Future<Bundle> futureBundle : futureBundles) {
                Bundle bundle = futureBundle.get(30, TimeUnit.MINUTES);//FIXME Max timeout??
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
