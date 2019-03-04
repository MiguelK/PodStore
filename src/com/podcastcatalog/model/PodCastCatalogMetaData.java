package com.podcastcatalog.model;

import com.podcastcatalog.model.podcastsearch.PodCastTitle;
import com.podcastcatalog.model.podcastsearch.ResultItem;
import com.podcastcatalog.service.podcastcatalog.TextSearchIndex;
import com.podcastcatalog.service.search.SearchTerm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * All In-Memory for one lang ->  PodCastCatalogMetaData-swe.zip (one.com)
 */ // PodCastCatalogMetaData
public class PodCastCatalogMetaData implements Serializable {

    private static final Logger LOG = Logger.getLogger(PodCastCatalogMetaData.class.getName());

    public TextSearchIndex<ResultItem> textSearchIndex = new TextSearchIndex<>();
    public List<PodCastTitle> podCastTitles = new ArrayList<>();
    public List<PodCastTitle> podCastTitlesTrending = new ArrayList<>();
    public List<SearchTerm> popularSearchQueries = new ArrayList<>();

    @Override
    public String toString() {
        return "PodCastCatalogMetaData{" +
                "textSearchIndex=" + textSearchIndex.getStatus() +
                ", podCastTitles=" + podCastTitles.size() +
                ", podCastTitlesTrending=" + podCastTitlesTrending.size() +
                ", popularSearchQueries=" + popularSearchQueries.size() +
                '}';
    }
}
