package com.podcastcatalog.api.response;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//https://www.podcastmotor.com/itunes-podcast-category-list/
public enum PodCastCategoryType {

    Arts("Arts"),
    Business("Business"),
    Comedy("Comedy"),
    Education("Education"),
    Games_Hobbies("Games & Hobbies"),
    Government_Organizations("Government & Organizations"),
    Health("Health"),
    Music("Music"),
    News_Politics("News & Politics"),
    Science_Medicine("Science & Medicine"),
    Society_Culture("Society & Culture"),
    Sports_Recreation("Sports & Recreation"),
    Technology("Technology"), //Subcategories
    Design("Design", Collections.singletonList(PodCastCategoryType.Arts)),
    Fashion_Beauty("Fashion & Beauty", Collections.singletonList(PodCastCategoryType.Arts)),
    Food("Food", Collections.singletonList(PodCastCategoryType.Arts)),
    Literature("Literature", Collections.singletonList(PodCastCategoryType.Arts)),
    Performing_Arts("Performing Arts", Collections.singletonList(PodCastCategoryType.Arts)),
    Visual_Arts("Visual Arts", Collections.singletonList(PodCastCategoryType.Arts)),
    Business_News("Business News", Collections.singletonList(PodCastCategoryType.Business)),
    Careers("Careers", Collections.singletonList(PodCastCategoryType.Business)),
    Investing("Investing", Collections.singletonList(PodCastCategoryType.Business)),
    Management_Marketing("Management & Marketing", Collections.singletonList(PodCastCategoryType.Business)),
    Shopping("Shopping", Collections.singletonList(PodCastCategoryType.Business)),
    Educational_Technology("Educational Technology", Collections.singletonList(PodCastCategoryType.Education)),
    Higher_Education("Higher Education", Collections.singletonList(PodCastCategoryType.Education)),
    K_12("K-12", Collections.singletonList(PodCastCategoryType.Education)),
    Language_Courses("Language Courses", Collections.singletonList(PodCastCategoryType.Education)),
    Training("Training", Collections.singletonList(PodCastCategoryType.Education)),
    Automotive("Automotive", Collections.singletonList(PodCastCategoryType.Games_Hobbies)),
    Aviation("Aviation", Collections.singletonList(PodCastCategoryType.Games_Hobbies)),
    Hobbies("Hobbies", Collections.singletonList(PodCastCategoryType.Games_Hobbies)),
    Other_Games("Other Games", Collections.singletonList(PodCastCategoryType.Games_Hobbies)),
    Video_Games("Video Games", Collections.singletonList(PodCastCategoryType.Games_Hobbies)),
    Local("Local", Collections.singletonList(PodCastCategoryType.Government_Organizations)),
    National("National", Collections.singletonList(PodCastCategoryType.Government_Organizations)),
    Non_Profit("Non-Profit", Collections.singletonList(PodCastCategoryType.Government_Organizations)),
    Regional("Regional", Collections.singletonList(PodCastCategoryType.Government_Organizations)),
    Alternative_Health("Alternative Health", Collections.singletonList(PodCastCategoryType.Health)),
    Fitness_Nutrition("Fitness & Nutrition", Collections.singletonList(PodCastCategoryType.Health)),
    Self_Help("Self-Help", Collections.singletonList(PodCastCategoryType.Health)),
    Sexuality("Sexuality", Collections.singletonList(PodCastCategoryType.Health)),
    Kids_Family("Kids & Family", Collections.singletonList(PodCastCategoryType.Health)),

    Religion_Spirituality("Religion & Spirituality", Collections.singletonList(PodCastCategoryType.News_Politics)),
    Buddhism("Buddhism", Arrays.asList(PodCastCategoryType.News_Politics, PodCastCategoryType.Religion_Spirituality)),
    Christianity("Christianity", Arrays.asList(PodCastCategoryType.News_Politics, PodCastCategoryType.Religion_Spirituality)),
    Hinduism("Hinduism", Arrays.asList(PodCastCategoryType.News_Politics, PodCastCategoryType.Religion_Spirituality)),
    Islam("Islam", Arrays.asList(PodCastCategoryType.News_Politics, PodCastCategoryType.Religion_Spirituality)),
    Judaism("Judaism", Arrays.asList(PodCastCategoryType.News_Politics, PodCastCategoryType.Religion_Spirituality)),
    Other("Other", Arrays.asList(PodCastCategoryType.News_Politics, PodCastCategoryType.Religion_Spirituality)),
    Spirituality("Spirituality", Arrays.asList(PodCastCategoryType.News_Politics, PodCastCategoryType.Religion_Spirituality)),
    Medicine("Medicine", Collections.singletonList(PodCastCategoryType.Science_Medicine)),
    Natural_Sciences("Natural Sciences", Collections.singletonList(PodCastCategoryType.Science_Medicine)),
    Social_Sciences("Social Sciences", Collections.singletonList(PodCastCategoryType.Science_Medicine)),
    History("History", Collections.singletonList(PodCastCategoryType.Social_Sciences)),
    Personal_Journals("Personal Journals", Collections.singletonList(PodCastCategoryType.Social_Sciences)),
    Philosophy("Philosophy", Collections.singletonList(PodCastCategoryType.Social_Sciences)),
    Places_Travel("Places & Travel", Collections.singletonList(PodCastCategoryType.Social_Sciences)),
    Amateur("Amateur", Collections.singletonList(PodCastCategoryType.Sports_Recreation)),
    College_High_School("College & High School", Collections.singletonList(PodCastCategoryType.Sports_Recreation)),
    Outdoor("Outdoor", Collections.singletonList(PodCastCategoryType.Sports_Recreation)),
    Professional("Professional", Collections.singletonList(PodCastCategoryType.Sports_Recreation)),
    TV_Film("TV & Film", Collections.singletonList(PodCastCategoryType.Sports_Recreation)),
    Gadgets("Gadgets", Collections.singletonList(PodCastCategoryType.Technology)),
    Podcasting("Podcasting", Collections.singletonList(PodCastCategoryType.Technology)),
    Software_How_To("Software How-To", Collections.singletonList(PodCastCategoryType.Technology)),
    Tech_News("Tech News", Collections.singletonList(PodCastCategoryType.Technology));



    //Acast Kid_Family
    private final int level;
    private String displayName;
    static List<PodCastCategoryType> sub_PodCast_category_Buddhism = Arrays.asList(News_Politics,Religion_Spirituality,Buddhism);

    List<PodCastCategoryType> cat = new ArrayList<>();
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


}
