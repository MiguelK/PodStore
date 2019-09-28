package com.podcastcatalog.model.podcastcatalog;


import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//https://www.podcastmotor.com/itunes-podcast-category-list/
//Map categories from Podcast [String] used by App client.
public enum PodCastCategoryType {

    TOPLIST_COUNTRY("Not Used In App"),

    //Arts
    ARTS("ARTS"),
    BOOKS("BOOKS"),
    DESIGN("DESIGN"),
    FASHION_BEAUTY("Fashion & Beauty"),
    FOOD("FOOD"),
    PERFORMING_ARTS("Performing ARTS"),
    VISUAL_ARTS("Visual ARTS"),


    //Business
    BUSINESS("BUSINESS"),
    CAREERS("CAREERS"),
    ENTREPRENEURSHIP("ENTREPRENEURSHIP"),
    INVESTING("INVESTING"),
    MANAGEMENT("MANAGEMENT"),
    MARKETING("MARKETING"),
    NON_PROFIT("Non-Profit"),

    //Comedy
    COMEDY("COMEDY"),
    COMEDY_INTERVIEWS("COMEDY INTERVIEWS"),
    IMPROV("IMPROV"),
    STAND_UP("Stand-Up"),

    //Education
    EDUCATION("EDUCATION"),
    COURSES("COURSES"),
    HOW_TO("HOW TO"),
    LANGUAGE_LEARNING("LANGUAGE LEARNING"),
    SELF_IMPROVEMENT("SELF IMPROVEMENT"),


    //Fiction
    FICTION("FICTION"),
    COMEDY_FICTION("COMEDY FICTION"),
    DRAMA("DRAMA"),
    SCIENCE_FICTION("SCIENCE FICTION"),

    //Government
    GOVERNMENT(Arrays.asList("Government & Organizations", "Government")),


    //Health-Fitness
    HEALTH_FITNESS(Arrays.asList("HEALTH & FITNESS", "HEALTH")),
    ALTERNATIVE_HEALTH_FITNESS("Alternative HEALTH_FITNESS"),
    FITNESS("FITNESS"),
    MEDICINE("MEDICINE"),
    MENTAL_HEALTH_FITNESS("MENTAL HEALTH FITNESS"),
    NUTRITION("NUTRITION"),
    SEXUALITY("SEXUALITY"),

    //History
    HISTORY("HISTORY"),

    //Kids & Family
    KIDS_FAMILY("Kids & Family"),
    EDUCATION_FOR_KIDS("EDUCATION FOR KIDS"),
    PARENTING("PARENTING"),
    PETS_ANIMALS("PETS_ANIMALS"),
    STORIES_FOR_KIDS("STORIES FOR KIDS"),

    //Leisure
    LEISURE("LEISURE"),
    ANIMATION_MANGA("Animation & Manga"),
    AUTOMOTIVE("AUTOMOTIVE"),
    AVIATION("AVIATION"),
    CRAFTS("CRAFTS"),
    GAMES("Games & HOBBIES"),
    HOBBIES("HOBBIES"),
    HOME_GARDEN("HOME_GARDEN"),
    VIDEO_GAMES("Video Games"),

    //Music
    MUSIC("MUSIC"),
    MUSIC_COMMENTARY("MUSIC COMMENTARY"),
    MUSIC_HISTORY("MUSIC HISTORY"),
    MUSIC_INTERVIEWS("MUSIC INTERVIEWS"),

    //News
    NEWS("NEWS"),
    BUSINESS_NEWS("BUSINESS & NEWS"),
    DAILY_NEWS("DAILY NEWS"),
    ENTERTAINMENT_NEWS("ENTERTAINMENT NEWS"),
    NEWS_COMMENTARY("NEWS COMMENTARY"),
    POLITICS(Arrays.asList("POLITICS", "News & Politics")),
    SPORTS_NEWS("SPORTS_NEWS"),
    TECH_NEWS("TECH_NEWS"),

    //Religion & Spirituality
    RELIGION_SPIRITUALITY("RELIGION & SPIRITUALITY"),
    BUDDHISM("BUDDHISM"),
    CHRISTIANITY("CHRISTIANITY"),
    HINDUISM("HINDUISM"),
    ISLAM("ISLAM"),
    JUDAISM("JUDAISM"),
    RELIGION("RELIGION"),
    SPIRITUALITY("SPIRITUALITY"),

    //Science
    SCIENCE(Arrays.asList("SCIENCE", "Science & Medicine")),
    ASTRONOMY("ASTRONOMY"),
    CHEMISTRY("CHEMISTRY"),
    EARTH_SCIENCES("EARTH SCIENCES"),
    LIFE_SCIENCES("LIFE SCIENCES"),
    MATHEMATICS("MATHEMATICS"),
    NATURAL_SCIENCES("NATURAL SCIENCES"),
    NATURE("NATURE"),
    PHYSICS("PHYSICS"),
    SOCIAL_SCIENCES("SOCIAL & SCIENCES"),


    //Society & Culture
    SOCIETY_CULTURE("Society & Culture"),
    DOCUMENTARY("DOCUMENTARY"),
    PERSONAL_JOURNALS("PERSONAL JOURNALS"),
    PHILOSOPHY("PHILOSOPHY"),
    PLACES_TRAVEL("PLACES TRAVEL"),
    RELATIONSHIPS("RELATIONSHIPS"),

    //Sports
    SPORTS(Arrays.asList("SPORTS", "Sports & Recreation")),
    BASEBALL("BASEBALL"),
    BASKETBALL("BASKETBALL"),
    CRICKET("CRICKET"),
    FANTASY_SPORTS("FANTASY SPORTS"),
    FOOTBALL("FOOTBALL"),
    GOLF("GOLF"),
    HOCKEY("HOCKEY"),
    RUGBY("RUGBY"),
    RUNNING("RUNNING"),
    SOCCER("SOCCER"),
    SWIMMING("SWIMMING"),
    TENNIS("TENNIS"),
    VOLLEYBALL("VOLLEYBALL"),
    WILDERNESS("WILDERNESS"),
    WRESTLING("WRESTLING"),

    //TV & Film
    TV_FILM("TV & Film"),
    AFTER_SHOWS("After Shows"),
    FILM_HISTORY("Film & History"),
    FILM_INTERVIEWS("FILM INTERVIEWS"),
    FILM_REVIEWS("Film Reviews"),
    TV_REVIEWS("TV Reviews"),

    //Technology
    TECHNOLOGY("TECHNOLOGY"),
    TRUE_CRIME("True Crime");

   // private final String displayName;
    private final List<String> displayNames;


    PodCastCategoryType(String displayName) {
        String displayNameTrimmed = StringUtils.trimToNull(displayName.toUpperCase());
        this.displayNames = new ArrayList<>();
        this.displayNames.add(displayNameTrimmed);
    }

    PodCastCategoryType(List<String> displayNames) {
        this.displayNames = new ArrayList<>();
        for (String displayName : displayNames) {
            String displayNameTrimmed = StringUtils.trimToNull(displayName.toUpperCase());
            this.displayNames.add(displayNameTrimmed);
        }
    }

    public boolean isSameCategoryType(String s) {
        for (String displayName : displayNames) {
            if(s.toUpperCase().equalsIgnoreCase(displayName)) {
                return true;
            }
        }
        return false;
    }

    public static List<PodCastCategoryType> fromString(String[] categories) {

        List<PodCastCategoryType> values = new ArrayList<>();
        for (String category : categories) {

            List<PodCastCategoryType> podCastCategoryTypes = getPodCastCategoryType(category);
            values.addAll(podCastCategoryTypes);
            }

        return values.stream().distinct().collect(Collectors.toList());
    }

    private static List<PodCastCategoryType> getPodCastCategoryType(String category) {


        List<PodCastCategoryType> values = new ArrayList<>();

        String[] parts = category.split(">");
        for (String part : parts) {

            String s = StringUtils.trimToNull(part);
            if(s==null) {
                continue;
            }

            for (PodCastCategoryType categoryType : PodCastCategoryType.values()) {
                if(categoryType.isSameCategoryType(s)) {
                    values.add(categoryType);
                }
            }
        }

        if(values.isEmpty()) {
            System.out.println(category + ",parsed PodCastCategoryType=" + values);
        }

        return values;
    }

}
