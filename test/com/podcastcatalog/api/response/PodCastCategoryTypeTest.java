package com.podcastcatalog.api.response;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PodCastCategoryTypeTest {


    @Test
    public void fromString_invalid() {
        Assert.assertTrue(PodCastCategoryType.fromString("unknown value").isEmpty());
        Assert.assertTrue(PodCastCategoryType.fromString(" ").isEmpty());
        Assert.assertTrue(PodCastCategoryType.fromString(null).isEmpty());
    }

    @Test
    public void category_levels() {
        int level1 =0;
        int level2 = 0 ;
        int level3 = 0;
        for (PodCastCategoryType podCastCategoryType : PodCastCategoryType.values()) {
            if(podCastCategoryType.getLevel()==1){
                level1++;
            }
            if(podCastCategoryType.getLevel()==2){
                level2++;
            }
            if(podCastCategoryType.getLevel()==3){
                level3++;
            }
        }

        Assert.assertTrue(level1==13);
        Assert.assertTrue(level2==47);
        Assert.assertTrue(level3==7);
    }

    @Test
    public void categoriesLevel1() {
        assertSize(1, PodCastCategoryType.Arts,"Arts","arts","ARTS"," ARts "," arts");
        assertSize(1, PodCastCategoryType.Business,"Business"," Business ","business"," busiNess ");
        assertSize(1, PodCastCategoryType.Comedy,"Comedy","comedy ","Comedy");
        assertSize(1, PodCastCategoryType.Education,"Education"," Education  "," education");
        assertSize(1, PodCastCategoryType.Games_Hobbies,"Games & Hobbies"," games & Hobbies  ","games & HObbies ");
        assertSize(1, PodCastCategoryType.Government_Organizations,"Government & Organizations"," Government & OrganizationS  ");
        assertSize(1, PodCastCategoryType.Health,"Health"," Health ", " HEALth");
        assertSize(1, PodCastCategoryType.Music,"Music"," music ", " MUSIC");
        assertSize(1, PodCastCategoryType.News_Politics,"News & Politics","  News & Politics  ", " news & Politics");
        assertSize(1, PodCastCategoryType.Science_Medicine,"Science & Medicine","  Science & Medicine  ", " Science & MeDicine");
        assertSize(1, PodCastCategoryType.Society_Culture,"Society & Culture","  Society & Culture  ", " SocieTy & Culture");
        assertSize(1, PodCastCategoryType.Sports_Recreation,"Sports & Recreation","  Sports & Recreation  ", " SporTS & Recreation");
        assertSize(1, PodCastCategoryType.Technology,"Technology","  technology  ", " TechnologY ");
    }

    @Test
    public void categoriesLevel2() {
        assertSize(2, PodCastCategoryType.Gadgets,"Gadgets"," gadgets ");
        assertSize(2, PodCastCategoryType.Podcasting,"Podcasting"," podcasting ");
        assertSize(2, PodCastCategoryType.Software_How_To,"Software How-To"," software How-To ");
        assertSize(2, PodCastCategoryType.Tech_News,"Tech News"," Tech news ");
        assertSize(2, PodCastCategoryType.Amateur,"Amateur"," amateur ");
        assertSize(2, PodCastCategoryType.College_High_School,"College & High School"," college & High School ");
        assertSize(2, PodCastCategoryType.Outdoor,"Outdoor","Outdoor ");
        assertSize(2, PodCastCategoryType.Professional,"Professional"," Professional ");
        assertSize(2, PodCastCategoryType.TV_Film,"TV & Film"," TV & Film ","tv & film");
        assertSize(2, PodCastCategoryType.History,"History"," History ");
        assertSize(2, PodCastCategoryType.Personal_Journals,"Personal Journals"," Personal Journals ");
        assertSize(2, PodCastCategoryType.Philosophy,"Philosophy"," Philosophy ");
        assertSize(2, PodCastCategoryType.Places_Travel,"Places & Travel"," Places & Travel ");
        assertSize(2, PodCastCategoryType.Medicine,"Medicine"," Medicine ");
        assertSize(2, PodCastCategoryType.Natural_Sciences,"Natural Sciences"," Natural Sciences ");
        assertSize(2, PodCastCategoryType.Social_Sciences,"Social Sciences"," Social Sciences ");
        assertSize(2, PodCastCategoryType.Religion_Spirituality,"Religion & Spirituality","Religion & Spirituality ");
        assertSize(2, PodCastCategoryType.Alternative_Health,"Alternative Health"," Alternative Health ");
        assertSize(2, PodCastCategoryType.Fitness_Nutrition,"Fitness & Nutrition"," Fitness & Nutrition ");
        assertSize(2, PodCastCategoryType.Self_Help,"Self-Help","Self-help ");
        assertSize(2, PodCastCategoryType.Sexuality,"Sexuality"," Sexuality ");
        assertSize(2, PodCastCategoryType.Kids_Family,"Kids & Family"," Kids & family ");
        assertSize(2, PodCastCategoryType.Local,"Local"," Local ");
        assertSize(2, PodCastCategoryType.National,"National"," National ");
        assertSize(2, PodCastCategoryType.Non_Profit,"Non-Profit"," Non-ProfiT ");
        assertSize(2, PodCastCategoryType.Regional,"Regional"," Regional ");
        assertSize(2, PodCastCategoryType.Automotive,"Automotive"," Automotive ");
        assertSize(2, PodCastCategoryType.Aviation,"Aviation"," Aviation ");
        assertSize(2, PodCastCategoryType.Hobbies,"Hobbies"," Hobbies ");
        assertSize(2, PodCastCategoryType.Other_Games,"Other Games"," Other Games ");
        assertSize(2, PodCastCategoryType.Video_Games,"Video Games"," Video Games ");
        assertSize(2, PodCastCategoryType.Educational_Technology,"Educational Technology"," Educational Technology ");
        assertSize(2, PodCastCategoryType.Higher_Education,"Higher Education"," Higher EducatioN ");
        assertSize(2, PodCastCategoryType.K_12,"K-12"," K-12 ","k-12");
        assertSize(2, PodCastCategoryType.Language_Courses,"Language Courses"," Language Courses ");
        assertSize(2, PodCastCategoryType.Training,"Training"," TrainIng ");
        assertSize(2, PodCastCategoryType.Business_News,"Business News"," Business News ");
        assertSize(2, PodCastCategoryType.Careers,"Careers"," Careers ");
        assertSize(2, PodCastCategoryType.Investing,"Investing"," Investing");
        assertSize(2, PodCastCategoryType.Management_Marketing,"Management & Marketing"," Management & Marketing ");
        assertSize(2, PodCastCategoryType.Shopping,"Shopping"," ShoppinG ");
        assertSize(2, PodCastCategoryType.Design,"Design"," Design ");
        assertSize(2, PodCastCategoryType.Fashion_Beauty,"Fashion & Beauty"," Fashion & Beauty ");
        assertSize(2, PodCastCategoryType.Food,"Food"," Food");
        assertSize(2, PodCastCategoryType.Literature,"Literature"," Literature ");
        assertSize(2, PodCastCategoryType.Performing_Arts,"Performing Arts"," performing Arts ");
        assertSize(2, PodCastCategoryType.Visual_Arts,"Visual Arts"," Visual Arts ");
    }

    @Test
    public void categoriesLevel3() {
        assertSize(3, PodCastCategoryType.Buddhism,"Buddhism"," Buddhism ");
        assertSize(3, PodCastCategoryType.Christianity,"Christianity"," CHristianity ");
        assertSize(3, PodCastCategoryType.Hinduism,"Hinduism"," Hinduism ");
        assertSize(3, PodCastCategoryType.Islam,"Islam"," Islam ");
        assertSize(3, PodCastCategoryType.Judaism,"Judaism"," Judaism ");
        assertSize(3, PodCastCategoryType.Other,"Other"," Other ");
        assertSize(3, PodCastCategoryType.Spirituality,"Spirituality"," SpiritualitY ");
    }

    private void assertSize(int size, PodCastCategoryType podCastCategoryType, String... rawNames){
        Assert.assertTrue(podCastCategoryType.getCategories().size()==size,"was=" + podCastCategoryType.getCategories().size());
        Assert.assertTrue(podCastCategoryType.getLevel()==size);
        for (String rawName : rawNames) {
            Assert.assertTrue(PodCastCategoryType.fromString(rawName).size()==size);
            Assert.assertTrue(PodCastCategoryType.fromString(rawName).contains(podCastCategoryType));
        }
        Assert.assertEquals(podCastCategoryType.getDisplayName(),rawNames[0]);
    }
}