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
LOG.info("Started UpdateSearchSuggestionsJob... language=" + language.name());

        //for (PodCastCatalogLanguage castCatalogLanguage : PodCastCatalogLanguage.values()) {
            buildSuggestionsFor(language);
        //}


        /*if(ServerInfo.isUSMode()) {
            buildSuggestionsFor(PodCastCatalogLanguage.US);
        }

        if(ServerInfo.isSWEMode()) {
            buildSuggestionsFor(PodCastCatalogLanguage.SE);
        }*/
    }

    private void buildSuggestionsFor(PodCastCatalogLanguage lang) {
        List<PodCastTitle> podCastTitles = new ArrayList<>();
        List<PodCastTitle>  podCastTitlesTrending = new ArrayList<>();


        PodCastCatalogLanguage language = PodCastCatalogLanguage.valueOf(lang.name());

        LOG.info("Start building SearchSuggestions + trending pods for lang=" + language);

        for (PodCastCollectorOkihika.TopList categoryName : PodCastCollectorOkihika.TopList.values()) {

            List<Long> ids = PodCastCategoryCollectorOkihika.parse(language,categoryName, 10).getPodCastIds();

          /*  if(lang == PodCastCatalogLanguage.US) {
                ids = PodCastCategoryCollectorOkihika.parseUS(categoryName, 100).getPodCastIds();
            }

            if(lang == PodCastCatalogLanguage.SE) {
                ids = PodCastCategoryCollectorOkihika.parseSWE(categoryName, 100).getPodCastIds();
            }*/

            List<ItunesSearchAPI.PodCastSearchResult.Row> podCastTitlesRows = ItunesSearchAPI.lookupPodCastsByIds(ids);

            LOG.info("Fetch " + podCastTitlesRows.size() + " PodCastTitles for " + categoryName.name() + ",language=" + language);

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

        LOG.info("Done building SearchSuggestions podCastTitles= " + podCastTitles.size() + " lang=" + language);

        SearchSuggestionService.getInstance().setPodCastTitles(lang, podCastTitles);
        SearchSuggestionService.getInstance().setPodCastTitlesTrending(lang, podCastTitlesTrending);
    }
}