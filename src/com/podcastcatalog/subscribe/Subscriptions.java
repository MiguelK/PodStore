package com.podcastcatalog.subscribe;

public class Subscriptions {

    private static final Subscriptions INSTANCE = new Subscriptions();

    private Subscriptions() {
    }

    public static void subscribe(String deviceToken, String contentId, ContentIdValidator contentIdValidator) {
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
