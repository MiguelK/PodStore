package com.podcastcatalog.service.job;

import com.podcastcatalog.model.podcastsearch.PodCastTitle;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCategoryCollectorOkihika;
import com.podcastcatalog.modelbuilder.collector.okihika.PodCastCollectorOkihika;
import com.podcastcatalog.service.search.SearchSuggestionService;

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

        for (PodCastCollectorOkihika.TopList list : PodCastCollectorOkihika.TopList.values()) {
            List<Long> ids = PodCastCategoryCollectorOkihika.parse(list, 100).getPodCastIds();
            List<ItunesSearchAPI.PodCastSearchResult.Row> podCastTitlesRows = ItunesSearchAPI.lookupPodCastsByIds(ids);

            LOG.info("Fetch PodCastTitles for " + list.name());

            for(int i=0; i<podCastTitlesRows.size(); i++) {
                ItunesSearchAPI.PodCastSearchResult.Row row = podCastTitlesRows.get(i);


                String trimmedTitle = org.apache.commons.lang3.StringUtils.trimToNull(row.getCollectionName());
                if(trimmedTitle == null){
                    continue;
                }

                PodCastTitle podCastTitle = new PodCastTitle(trimmedTitle);

                if(podCastTitles.contains(podCastTitle)) {
                    continue;
                }

                podCastTitles.add(podCastTitle);

                if(i==0) {
                    podCastTitlesTrending.add(podCastTitle);
                }
            }
        }

        LOG.info("Done building SearchSuggestions podCastTitles= " + podCastTitles.size());

        SearchSuggestionService.getInstance().setPodCastTitles(podCastTitles);
        SearchSuggestionService.getInstance().setPodCastTitlesTrending(podCastTitlesTrending);
    }
}