package com.podcastcatalog.service.search;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastsearch.PodCastTitle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class SearchSuggestionService {

    private static final SearchSuggestionService INSTANCE = new SearchSuggestionService();
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(SearchSuggestionService.class.getName());

    private Map<PodCastCatalogLanguage,  List<PodCastTitle>> podCastTitles = new HashMap<>();
    private Map<PodCastCatalogLanguage,  List<PodCastTitle>> podCastTitlesTrending = new HashMap<>();

    public static SearchSuggestionService getInstance() {
        return INSTANCE;
    }

    public void setPodCastTitles(PodCastCatalogLanguage lang, List<PodCastTitle> podCastTitles) {
        writeLock.lock();
        try {
            this.podCastTitles.put(lang,Collections.unmodifiableList(podCastTitles));
        } finally {
            writeLock.unlock();
        }
    }

    public void setPodCastTitlesTrending(PodCastCatalogLanguage lang, List<PodCastTitle> podCastTitlesTrending) {
        writeLock.lock();
        try {
            this.podCastTitlesTrending.put(lang, Collections.unmodifiableList(podCastTitlesTrending));
        } finally {
            writeLock.unlock();
        }
    }

    public List<PodCastTitle> getPodCastTitles(PodCastCatalogLanguage lang) {
        readLock.lock();
        try {
            return podCastTitles.get(lang)== null ?
                    Collections.emptyList() : podCastTitles.get(lang);
        } finally {
            readLock.unlock();
        }
    }

    public List<PodCastTitle> getPodCastTitlesTrending(PodCastCatalogLanguage lang) {
        readLock.lock();
        try {
            return podCastTitlesTrending.get(lang) == null ?
                    Collections.emptyList() : podCastTitlesTrending.get(lang);
        } finally {
            readLock.unlock();
        }
    }

}
