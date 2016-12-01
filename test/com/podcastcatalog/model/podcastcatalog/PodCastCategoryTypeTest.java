package com.podcastcatalog.model.podcastcatalog;

import com.podcastcatalog.model.podcastcatalog.PodCastCategoryType;
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
        assertSize(1, PodCastCategoryType.ARTS,"ARTS","arts","ARTS"," ARts "," arts");
        assertSize(1, PodCastCategoryType.BUSINESS,"BUSINESS"," BUSINESS ","business"," busiNess ");
        assertSize(1, PodCastCategoryType.COMEDY,"COMEDY","comedy ","COMEDY");
        assertSize(1, PodCastCategoryType.EDUCATION,"EDUCATION"," EDUCATION  "," education");
        assertSize(1, PodCastCategoryType.GAMES_HOBBIES,"Games & HOBBIES"," games & HOBBIES  ","games & HObbies ");
        assertSize(1, PodCastCategoryType.GOVERNMENT_ORGANIZATIONS,"Government & Organizations"," Government & OrganizationS  ");
        assertSize(1, PodCastCategoryType.HEALTH,"HEALTH"," HEALTH ", " HEALth");
        assertSize(1, PodCastCategoryType.MUSIC,"MUSIC"," music ", " MUSIC");
        assertSize(1, PodCastCategoryType.NEWS_POLITICS,"News & Politics","  News & Politics  ", " news & Politics");
        assertSize(1, PodCastCategoryType.SCIENCE_MEDICINE,"Science & MEDICINE","  Science & MEDICINE  ", " Science & MeDicine");
        assertSize(1, PodCastCategoryType.SOCIETY_CULTURE,"Society & Culture","  Society & Culture  ", " SocieTy & Culture");
        assertSize(1, PodCastCategoryType.SPORTS_RECREATION,"Sports & Recreation","  Sports & Recreation  ", " SporTS & Recreation");
        assertSize(1, PodCastCategoryType.TECHNOLOGY,"TECHNOLOGY","  technology  ", " TechnologY ");
    }

    @Test
    public void categoriesLevel2() {
        assertSize(2, PodCastCategoryType.GADGETS,"GADGETS"," gadgets ");
        assertSize(2, PodCastCategoryType.PODCASTING,"PODCASTING"," podcasting ");
        assertSize(2, PodCastCategoryType.SOFTWARE_HOW_TO,"Software How-To"," software How-To ");
        assertSize(2, PodCastCategoryType.TECH_NEWS,"Tech News"," Tech news ");
        assertSize(2, PodCastCategoryType.AMATEUR,"AMATEUR"," amateur ");
        assertSize(2, PodCastCategoryType.COLLEGE_HIGH_SCHOOL,"College & High School"," college & High School ");
        assertSize(2, PodCastCategoryType.OUTDOOR,"OUTDOOR","OUTDOOR ");
        assertSize(2, PodCastCategoryType.PROFESSIONAL,"PROFESSIONAL"," PROFESSIONAL ");
        assertSize(2, PodCastCategoryType.TV_FILM,"TV & Film"," TV & Film ","tv & film");
        assertSize(2, PodCastCategoryType.HISTORY,"HISTORY"," HISTORY ");
        assertSize(2, PodCastCategoryType.PERSONAL_JOURNALS,"Personal Journals"," Personal Journals ");
        assertSize(2, PodCastCategoryType.PHILOSOPHY,"PHILOSOPHY"," PHILOSOPHY ");
        assertSize(2, PodCastCategoryType.PLACES_TRAVEL,"Places & Travel"," Places & Travel ");
        assertSize(2, PodCastCategoryType.MEDICINE,"MEDICINE"," MEDICINE ");
        assertSize(2, PodCastCategoryType.NATURAL_SCIENCES,"Natural Sciences"," Natural Sciences ");
        assertSize(2, PodCastCategoryType.SOCIAL_SCIENCES,"Social Sciences"," Social Sciences ");
        assertSize(2, PodCastCategoryType.RELIGION_SPIRITUALITY,"Religion & SPIRITUALITY","Religion & SPIRITUALITY ");
        assertSize(2, PodCastCategoryType.ALTERNATIVE_HEALTH,"Alternative HEALTH"," Alternative HEALTH ");
        assertSize(2, PodCastCategoryType.FITNESS_NUTRITION,"Fitness & Nutrition"," Fitness & Nutrition ");
        assertSize(2, PodCastCategoryType.SELF_HELP,"Self-Help","Self-help ");
        assertSize(2, PodCastCategoryType.SEXUALITY,"SEXUALITY"," SEXUALITY ");
        assertSize(2, PodCastCategoryType.KIDS_FAMILY,"Kids & Family"," Kids & family ");
        assertSize(2, PodCastCategoryType.LOCAL,"LOCAL"," LOCAL ");
        assertSize(2, PodCastCategoryType.NATIONAL,"NATIONAL"," NATIONAL ");
        assertSize(2, PodCastCategoryType.NON_PROFIT,"Non-Profit"," Non-ProfiT ");
        assertSize(2, PodCastCategoryType.REGIONAL,"REGIONAL"," REGIONAL ");
        assertSize(2, PodCastCategoryType.AUTOMOTIVE,"AUTOMOTIVE"," AUTOMOTIVE ");
        assertSize(2, PodCastCategoryType.AVIATION,"AVIATION"," AVIATION ");
        assertSize(2, PodCastCategoryType.HOBBIES,"HOBBIES"," HOBBIES ");
        assertSize(2, PodCastCategoryType.OTHER_GAMES,"OTHER Games"," OTHER Games ");
        assertSize(2, PodCastCategoryType.VIDEO_GAMES,"Video Games"," Video Games ");
        assertSize(2, PodCastCategoryType.EDUCATIONAL_TECHNOLOGY,"Educational TECHNOLOGY"," Educational TECHNOLOGY ");
        assertSize(2, PodCastCategoryType.HIGHER_EDUCATION,"Higher EDUCATION"," Higher EducatioN ");
        assertSize(2, PodCastCategoryType.K_12,"K-12"," K-12 ","k-12");
        assertSize(2, PodCastCategoryType.LANGUAGE_COURSES,"Language Courses"," Language Courses ");
        assertSize(2, PodCastCategoryType.TRAINING,"TRAINING"," TrainIng ");
        assertSize(2, PodCastCategoryType.BUSINESS_NEWS,"BUSINESS News"," BUSINESS News ");
        assertSize(2, PodCastCategoryType.CAREERS,"CAREERS"," CAREERS ");
        assertSize(2, PodCastCategoryType.INVESTING,"INVESTING"," INVESTING");
        assertSize(2, PodCastCategoryType.MANAGEMENT_MARKETING,"Management & Marketing"," Management & Marketing ");
        assertSize(2, PodCastCategoryType.SHOPPING,"SHOPPING"," ShoppinG ");
        assertSize(2, PodCastCategoryType.DESIGN,"DESIGN"," DESIGN ");
        assertSize(2, PodCastCategoryType.FASHION_BEAUTY,"Fashion & Beauty"," Fashion & Beauty ");
        assertSize(2, PodCastCategoryType.FOOD,"FOOD"," FOOD");
        assertSize(2, PodCastCategoryType.LITERATURE,"LITERATURE"," LITERATURE ");
        assertSize(2, PodCastCategoryType.PERFORMING_ARTS,"Performing ARTS"," performing ARTS ");
        assertSize(2, PodCastCategoryType.VISUAL_ARTS,"Visual ARTS"," Visual ARTS ");
    }

    @Test
    public void categoriesLevel3() {
        assertSize(3, PodCastCategoryType.BUDDHISM,"BUDDHISM"," BUDDHISM ");
        assertSize(3, PodCastCategoryType.CHRISTIANITY,"CHRISTIANITY"," CHristianity ");
        assertSize(3, PodCastCategoryType.HINDUISM,"HINDUISM"," HINDUISM ");
        assertSize(3, PodCastCategoryType.ISLAM,"ISLAM"," ISLAM ");
        assertSize(3, PodCastCategoryType.JUDAISM,"JUDAISM"," JUDAISM ");
        assertSize(3, PodCastCategoryType.OTHER,"OTHER"," OTHER ");
        assertSize(3, PodCastCategoryType.SPIRITUALITY,"SPIRITUALITY"," SpiritualitY ");
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