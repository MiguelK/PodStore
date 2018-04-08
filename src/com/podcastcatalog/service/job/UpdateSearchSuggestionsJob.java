package com.podcastcatalog.service.job;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastsearch.PodCastTitle;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCategoryCollectorOkihika;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;
import com.podcastcatalog.service.search.SearchSuggestionService;
import com.podcastcatalog.util.ServerInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UpdateSearchSuggestionsJob implements Job {

    private final static Logger LOG = Logger.getLogger(UpdateSearchSuggestionsJob.class.getName());


    private PodCastCatalogLanguage language;


    public UpdateSearchSuggestionsJob(PodCastCatalogLanguage language) {
        this.language = language;
    }

    @Override
    public void doWork() {
            buildSuggestionsFor(language);
    }

    private void buildSuggestionsFor(PodCastCatalogLanguage language) {
        List<PodCastTitle> podCastTitles = new ArrayList<>();
        List<PodCastTitle>  podCastTitlesTrending = new ArrayList<>();

        LOG.info("Start building SearchSuggestions + trending pods for lang=" + language);


        for (PodCastCollectorOkihika.TopList categoryName : PodCastCollectorOkihika.TopList.values()) {

            int fetchSize = 40;
            if (ServerInfo.isLocalDevMode()) {
                fetchSize = 3;
            }

            List<Long> ids = PodCastCategoryCollectorOkihika.parse(language,categoryName, fetchSize).getPodCastIds();


            List<ItunesSearchAPI.PodCastSearchResult.Row> podCastTitlesRows = ItunesSearchAPI.lookupPodCastsByIds(ids);

            LOG.info("Fetched from Itunes=" + podCastTitlesRows.size() + " PodCastTitles for " + categoryName.name() + ",language=" + language);

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

        LOG.info("Done building SearchSuggestions podCastTitles= " + podCastTitles.size() +
                ",podCastTitlesTrending=" + podCastTitlesTrending.size() + " lang=" + language);

        SearchSuggestionService.getInstance().setPodCastTitles(language, podCastTitles);
        SearchSuggestionService.getInstance().setPodCastTitlesTrending(language, podCastTitlesTrending);
    }
}