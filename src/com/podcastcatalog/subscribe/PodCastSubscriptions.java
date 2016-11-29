package com.podcastcatalog.subscribe;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class PodCastSubscriptions {

    private static final PodCastSubscriptions INSTANCE = new PodCastSubscriptions();
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    private final static Logger LOG = Logger.getLogger(PodCastSubscriptions.class.getName());

    public static PodCastSubscriptions getInstance() {
        return INSTANCE;
    }


    public static void subscribe(String deviceToken, String contentId, ContentIdValidator contentIdValidator) {
//        INSTANCE.
    }

    public static void unsubscribe(String deviceToken, String contentId) {
    }

    public static void deleteSubscriber(String deviceToken) {
    }

    public static void registerSubscriber(String deviceToken) {
    }

    public static void pushMessage(String message, String contentId) {

    }

}
