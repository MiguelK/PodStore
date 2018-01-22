package com.podcastcatalog.model.podcastcatalog;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//https://www.podcastmotor.com/itunes-podcast-category-list/
public enum PodCastCategoryType {

    ARTS("ARTS"),
    BUSINESS("BUSINESS"),
    COMEDY("COMEDY"),
    EDUCATION("EDUCATION"),
    GAMES_HOBBIES("Games & HOBBIES"),
    GOVERNMENT_ORGANIZATIONS("Government & Organizations"),
    HEALTH("HEALTH"),
    MUSIC("MUSIC"),
    NEWS_POLITICS("News & Politics"),
    SCIENCE_MEDICINE("Science & MEDICINE"),
    SOCIETY_CULTURE("Society & Culture"),
    SPORTS_RECREATION("Sports & Recreation"),
    TECHNOLOGY("TECHNOLOGY"), //Subcategories
    DESIGN("DESIGN", Collections.singletonList(PodCastCategoryType.ARTS)),
    FASHION_BEAUTY("Fashion & Beauty", Collections.singletonList(PodCastCategoryType.ARTS)),
    FOOD("FOOD", Collections.singletonList(PodCastCategoryType.ARTS)),
    LITERATURE("LITERATURE", Collections.singletonList(PodCastCategoryType.ARTS)),
    PERFORMING_ARTS("Performing ARTS", Collections.singletonList(PodCastCategoryType.ARTS)),
    VISUAL_ARTS("Visual ARTS", Collections.singletonList(PodCastCategoryType.ARTS)),
    BUSINESS_NEWS("BUSINESS News", Collections.singletonList(PodCastCategoryType.BUSINESS)),
    CAREERS("CAREERS", Collections.singletonList(PodCastCategoryType.BUSINESS)),
    INVESTING("INVESTING", Collections.singletonList(PodCastCategoryType.BUSINESS)),
    MANAGEMENT_MARKETING("Management & Marketing", Collections.singletonList(PodCastCategoryType.BUSINESS)),
    SHOPPING("SHOPPING", Collections.singletonList(PodCastCategoryType.BUSINESS)),
    EDUCATIONAL_TECHNOLOGY("Educational TECHNOLOGY", Collections.singletonList(PodCastCategoryType.EDUCATION)),
    HIGHER_EDUCATION("Higher EDUCATION", Collections.singletonList(PodCastCategoryType.EDUCATION)),
    K_12("K-12", Collections.singletonList(PodCastCategoryType.EDUCATION)),
    LANGUAGE_COURSES("Language Courses", Collections.singletonList(PodCastCategoryType.EDUCATION)),
    TRAINING("TRAINING", Collections.singletonList(PodCastCategoryType.EDUCATION)),
    AUTOMOTIVE("AUTOMOTIVE", Collections.singletonList(PodCastCategoryType.GAMES_HOBBIES)),
    AVIATION("AVIATION", Collections.singletonList(PodCastCategoryType.GAMES_HOBBIES)),
    HOBBIES("HOBBIES", Collections.singletonList(PodCastCategoryType.GAMES_HOBBIES)),
    OTHER_GAMES("OTHER Games", Collections.singletonList(PodCastCategoryType.GAMES_HOBBIES)),
    VIDEO_GAMES("Video Games", Collections.singletonList(PodCastCategoryType.GAMES_HOBBIES)),
    LOCAL("LOCAL", Collections.singletonList(PodCastCategoryType.GOVERNMENT_ORGANIZATIONS)),
    NATIONAL("NATIONAL", Collections.singletonList(PodCastCategoryType.GOVERNMENT_ORGANIZATIONS)),
    NON_PROFIT("Non-Profit", Collections.singletonList(PodCastCategoryType.GOVERNMENT_ORGANIZATIONS)),
    REGIONAL("REGIONAL", Collections.singletonList(PodCastCategoryType.GOVERNMENT_ORGANIZATIONS)),
    ALTERNATIVE_HEALTH("Alternative HEALTH", Collections.singletonList(PodCastCategoryType.HEALTH)),
    FITNESS_NUTRITION("Fitness & Nutrition", Collections.singletonList(PodCastCategoryType.HEALTH)),
    SELF_HELP("Self-Help", Collections.singletonList(PodCastCategoryType.HEALTH)),
    SEXUALITY("SEXUALITY", Collections.singletonList(PodCastCategoryType.HEALTH)),
    KIDS_FAMILY("Kids & Family", Collections.singletonList(PodCastCategoryType.HEALTH)),

    RELIGION_SPIRITUALITY("Religion & SPIRITUALITY", Collections.singletonList(PodCastCategoryType.NEWS_POLITICS)),
    BUDDHISM("BUDDHISM", Arrays.asList(PodCastCategoryType.NEWS_POLITICS, PodCastCategoryType.RELIGION_SPIRITUALITY)),
    CHRISTIANITY("CHRISTIANITY", Arrays.asList(PodCastCategoryType.NEWS_POLITICS, PodCastCategoryType.RELIGION_SPIRITUALITY)),
    HINDUISM("HINDUISM", Arrays.asList(PodCastCategoryType.NEWS_POLITICS, PodCastCategoryType.RELIGION_SPIRITUALITY)),
    ISLAM("ISLAM", Arrays.asList(PodCastCategoryType.NEWS_POLITICS, PodCastCategoryType.RELIGION_SPIRITUALITY)),
    JUDAISM("JUDAISM", Arrays.asList(PodCastCategoryType.NEWS_POLITICS, PodCastCategoryType.RELIGION_SPIRITUALITY)),
    OTHER("OTHER", Arrays.asList(PodCastCategoryType.NEWS_POLITICS, PodCastCategoryType.RELIGION_SPIRITUALITY)),
    SPIRITUALITY("SPIRITUALITY", Arrays.asList(PodCastCategoryType.NEWS_POLITICS, PodCastCategoryType.RELIGION_SPIRITUALITY)),
    MEDICINE("MEDICINE", Collections.singletonList(PodCastCategoryType.SCIENCE_MEDICINE)),
    NATURAL_SCIENCES("Natural Sciences", Collections.singletonList(PodCastCategoryType.SCIENCE_MEDICINE)),
    SOCIAL_SCIENCES("Social Sciences", Collections.singletonList(PodCastCategoryType.SCIENCE_MEDICINE)),
    HISTORY("HISTORY", Collections.singletonList(PodCastCategoryType.SOCIAL_SCIENCES)),
    PERSONAL_JOURNALS("Personal Journals", Collections.singletonList(PodCastCategoryType.SOCIAL_SCIENCES)),
    PHILOSOPHY("PHILOSOPHY", Collections.singletonList(PodCastCategoryType.SOCIAL_SCIENCES)),
    PLACES_TRAVEL("Places & Travel", Collections.singletonList(PodCastCategoryType.SOCIAL_SCIENCES)),
    AMATEUR("AMATEUR", Collections.singletonList(PodCastCategoryType.SPORTS_RECREATION)),
    COLLEGE_HIGH_SCHOOL("College & High School", Collections.singletonList(PodCastCategoryType.SPORTS_RECREATION)),
    OUTDOOR("OUTDOOR", Collections.singletonList(PodCastCategoryType.SPORTS_RECREATION)),
    PROFESSIONAL("PROFESSIONAL", Collections.singletonList(PodCastCategoryType.SPORTS_RECREATION)),
    TV_FILM("TV & Film", Collections.singletonList(PodCastCategoryType.SPORTS_RECREATION)),
    GADGETS("GADGETS", Collections.singletonList(PodCastCategoryType.TECHNOLOGY)),
    PODCASTING("PODCASTING", Collections.singletonList(PodCastCategoryType.TECHNOLOGY)),
    SOFTWARE_HOW_TO("Software How-To", Collections.singletonList(PodCastCategoryType.TECHNOLOGY)),
    TECH_NEWS("Tech News", Collections.singletonList(PodCastCategoryType.TECHNOLOGY));



    //Acast Kid_Family
    private final int level;
    private final String displayName;

    final List<PodCastCategoryType> cat = new ArrayList<>();
    PodCastCategoryType(String displayName, List<PodCastCategoryType> parents) {
        this.level = parents.size() + 1;
        this.displayName = displayName;
        cat.addAll(parents);
        cat.add(this);
    }
    PodCastCategoryType(String displayName) {
        this.level = 1;
        this.displayName = displayName;
        cat.add(this);
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    public List<PodCastCategoryType> getCategories() {
        return cat;
    }

    public static List<PodCastCategoryType> fromString(String category) {
        if(category==null){
            return Collections.emptyList();
        }

        String trimmedCategoryName = category.trim();

        for (PodCastCategoryType podCastCategoryTypeX : PodCastCategoryType.values()) {
            if(podCastCategoryTypeX.getDisplayName().equalsIgnoreCase(trimmedCategoryName)){
                return podCastCategoryTypeX.getCategories();
            }
        }

        return Collections.emptyList();
    }

    public static List<PodCastCategoryType> fromString(String[] category) {
        if(category==null){
            return Collections.emptyList();
        }

        //FXIME Only support for 1 category?
        for (String c : category) {
            for (PodCastCategoryType podCastCategoryTypeX : PodCastCategoryType.values()) {
                if(podCastCategoryTypeX.getDisplayName().equalsIgnoreCase(c)){
                    return podCastCategoryTypeX.getCategories();
                }
            }
        }


        return Collections.emptyList();
    }

}
