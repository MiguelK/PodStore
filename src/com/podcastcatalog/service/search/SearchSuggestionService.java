package com.podcastcatalog.service.search;

import com.podcastcatalog.model.podcastsearch.PodCastTitle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class SearchSuggestionService {

    private static final SearchSuggestionService INSTANCE = new SearchSuggestionService();
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(SearchSuggestionService.class.getName());

    private List<PodCastTitle>  podCastTitles = new ArrayList<>();
    private List<PodCastTitle>  podCastTitlesTrending = new ArrayList<>();

    public static SearchSuggestionService getInstance() {
        return INSTANCE;
    }

    public void setPodCastTitles(List<PodCastTitle> podCastTitles) {
        writeLock.lock();
        try {
            this.podCastTitles = podCastTitles;
        } finally {
            writeLock.unlock();
        }
    }

    public void setPodCastTitlesTrending(List<PodCastTitle> podCastTitlesTrending) {
        writeLock.lock();
        try {
            this.podCastTitlesTrending = podCastTitlesTrending;
        } finally {
            writeLock.unlock();
        }
    }

    public List<PodCastTitle> getPodCastTitles() {
        readLock.lock();
        try {
            return podCastTitles;
        } finally {
            readLock.unlock();
        }
    }

    public List<PodCastTitle> getPodCastTitlesTrending() {
        readLock.lock();
        try {
            return podCastTitlesTrending;
        } finally {
            readLock.unlock();
        }
    }

}
