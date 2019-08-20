package com.podcastcatalog.service.appstatistic;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.service.subscription.FtpOneClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class AppStatisticService {

    private final static Logger LOG = Logger.getLogger(PodCastCatalogService.class.getName());

    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private AppStatisticDataContainer appStatisticDataContainer = new AppStatisticDataContainer();
    private static final AppStatisticService INSTANCE = new AppStatisticService();

    public static AppStatisticService getInstance() {
        return INSTANCE;
    }

    public void load() {
        appStatisticDataContainer = FtpOneClient.getInstance().loadAppStatistics();
        if (appStatisticDataContainer == null) {
            appStatisticDataContainer = new AppStatisticDataContainer();
            for (PodCastCatalogLanguage language : PodCastCatalogLanguage.values()) {
                appStatisticDataContainer.appStatisticDataLang.put(language, new AppStatisticData());
            }

        }
        LOG.info("Loading AppStatistics " + appStatisticDataContainer.appStatisticDataLang.size()); //AppStatistics
    }

    public void uploadToOne() {
        FtpOneClient.getInstance().upload(appStatisticDataContainer);
    }

    public AppStatisticData getAppStatisticData(PodCastCatalogLanguage podCastCatalogLanguage) {
        readLock.lock();
        try {
            return appStatisticDataContainer.appStatisticDataLang.get(podCastCatalogLanguage);
        } finally {
            readLock.unlock();
        }
    }

    public void addUserEngaged(String pid, String eid,
                               PodCastCatalogLanguage podCastCatalogLanguage) {
        AppStatisticData appStatisticData = getAppStatisticData(podCastCatalogLanguage);

        if (appStatisticData == null) {
            appStatisticData = new AppStatisticData();
            register(podCastCatalogLanguage, appStatisticData);
        }

        List<PodCastEpisodeStatistic> popular = new ArrayList<>(appStatisticData.popularPodCastEpisodes);

        Optional<PodCastEpisodeStatistic> first =
                popular.stream().filter(r -> pid.equals(r.pid) && eid.equals(r.eid)).findFirst();

        if (first.isPresent()) {
            first.get().increaseCounter();
        } else {
            popular.add(new PodCastEpisodeStatistic(pid, eid));
        }

        Collections.sort(popular);

        if (popular.size() >= 100) {
            popular = popular.subList(0, popular.size() - 1);
        }

        writeLock.lock();

        try {
            appStatisticData.popularPodCastEpisodes = popular;
            //        FtpOneClient.getInstance().upload(subscriptionData); //FIXME
        } finally {
            writeLock.unlock();
        }
    }

    public void addUserEngaged(String pid, PodCastCatalogLanguage podCastCatalogLanguage) {
        AppStatisticData appStatisticData = getAppStatisticData(podCastCatalogLanguage);

        if (appStatisticData == null) {
            appStatisticData = new AppStatisticData();
            register(podCastCatalogLanguage, appStatisticData);
        }

        List<PodCastStatistic> popular = new ArrayList<>(appStatisticData.popularPodCasts);

        Optional<PodCastStatistic> first =
                popular.stream().filter(r -> pid.equalsIgnoreCase(r.pid)).findFirst();

        if (first.isPresent()) {
            first.get().increaseCounter();
        } else {
            popular.add(new PodCastStatistic(pid));
        }

        Collections.sort(popular);

        if (popular.size() >= 100) {
            popular = popular.subList(0, popular.size() - 1);
        }

        writeLock.lock();

        try {
            appStatisticData.popularPodCasts = popular;
            //        FtpOneClient.getInstance().upload(subscriptionData); //FIXME
        } finally {
            writeLock.unlock();
        }
    }


    private void register(PodCastCatalogLanguage podCastCatalogLanguage, AppStatisticData appStatisticData) {
        writeLock.lock();
        try {
            LOG.info("Register AppStatisticData (" + podCastCatalogLanguage.name() + ") " + appStatisticData);
            appStatisticDataContainer.appStatisticDataLang.put(podCastCatalogLanguage, appStatisticData);
        } finally {
            writeLock.unlock();
        }
    }

}
