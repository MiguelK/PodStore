package com.podcastcatalog.service.job;

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

    @Override
    public void doWork() {

         List<PodCastTitle> podCastTitles = new ArrayList<>();
         List<PodCastTitle>  podCastTitlesTrending = new ArrayList<>();

        LOG.info("Start fetching SearchSuggestions...");

        for (PodCastCollectorOkihika.TopList categoryName : PodCastCollectorOkihika.TopList.values()) {

            List<Long> ids;

            if(ServerInfo.isUSMode()) {
                ids = PodCastCategoryCollectorOkihika.parseUS(categoryName, 100).getPodCastIds();
            } else {
                ids = PodCastCategoryCollectorOkihika.parseSWE(categoryName, 100).getPodCastIds();
            }

            List<ItunesSearchAPI.PodCastSearchResult.Row> podCastTitlesRows = ItunesSearchAPI.lookupPodCastsByIds(ids);

            LOG.info("Fetch PodCastTitles for " + categoryName.name());

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

        LOG.info("Done building SearchSuggestions podCastTitles= " + podCastTitles.size());

        SearchSuggestionService.getInstance().setPodCastTitles(podCastTitles);
        SearchSuggestionService.getInstance().setPodCastTitlesTrending(podCastTitlesTrending);
    }
}