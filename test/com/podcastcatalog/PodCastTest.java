package com.podcastcatalog;

import com.podcastcatalog.api.response.PodCast;
import com.podcastcatalog.api.response.PodCastCategoryType;
import com.podcastcatalog.api.response.PodCastEpisode;
import com.podcastcatalog.api.response.PodCastEpisodeTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PodCastTest {

    @Test
    public void serializable() {
        PodCast podCast = createValid().build();
        TestUtil.assertSerializable(podCast);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNewBuilder() {
        PodCast.newBuilder().build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_id_negative() {
        createValid().id(-12).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_id_zero() {
        createValid().id(0).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_title_null() {
        createValid().title(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_title_empty() {
        createValid().title(" ").build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_description_null() {
        createValid().description(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_description_empty() {
        createValid().description(" ").build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_feedURL_null() {
        createValid().feedURL(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_feedURL_empty() {
        createValid().feedURL(" ").build();
    }
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_publisher_null() {
        createValid().publisher(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_publisher_empty() {
        createValid().publisher(" ").build();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void invalid_podCastEpisodes_null() {
        createValid().addPodCastEpisodes(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podCastEpisodes_empty() {
         PodCast.newBuilder().title("sdsds").description("dsdsdsd").createdDate(LocalDateTime.now()).
                feedURL("sdsdsd").id(22).publisher("sdsdsd").setPodCastCategories(PodCastCategoryType.fromString("ARTS")).
                addPodCastEpisodes(Collections.emptyList()).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_createdDate_null() {
        createValid().createdDate(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podCastCategories_null() {
        createValid().setPodCastCategories(null).build();
    }
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podCastCategories_empty() {
        createValid().setPodCastCategories(Collections.emptyList()).build();
    }

    @Test
    public void podCastCategories() {
        List<PodCastCategoryType> podCastCategories = PodCastCategoryType.fromString("College & High School");
        Assert.assertEquals(createValid().setPodCastCategories(podCastCategories).build().getPodCastCategories(),
                podCastCategories);
    }

    @Test
    public void testToString() {
        Assert.assertTrue(createValid().build().toString().contains("PodCast{"));
    }

    @Test
    public void podCastEpisodes() {
        List<PodCastEpisode> podCastEpisodes = Collections.singletonList(PodCastEpisodeTest.createValid().build());
        Assert.assertTrue(createValid().addPodCastEpisodes(podCastEpisodes).build().getPodCastEpisodes().size()==2);
    }
    @Test
    public void trim_description() {
        Assert.assertEquals(createValid().description(" some text ").build().getDescription(),"some text");
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void podCastCategories_unmodifiable() {
        createValid().build().getPodCastCategories().add(PodCastCategoryType.AMATEUR);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void podCastEpisodes_unmodifiable() {
        createValid().build().getPodCastEpisodes().add(PodCastEpisodeTest.createValid().build());
    }
    @Test
    public void createdDate() {
        LocalDateTime createdDate = LocalDateTime.now().minusHours(22);
        Assert.assertEquals(createValid().createdDate(createdDate).build().getCreatedDate(),createdDate);
    }

    @Test
    public void trim_title() {
        Assert.assertEquals(createValid().title(" some text ").build().getTitle(),"some text");
    }

    @Test
    public void trim_feedURL() {
        Assert.assertEquals(createValid().feedURL(" some text ").build().getFeedURL(),"some text");
    }

    @Test
    public void trim_publisher() {
        Assert.assertEquals(createValid().publisher(" some text ").build().getPublisher(),"some text");
    }

    @Test
    public void id() {
        Assert.assertEquals(createValid().id(343).build().getId(),343);
    }

    @Test
    public void addPodCastEpisode() {

        PodCastEpisode podCastEpisode = PodCastEpisodeTest.createValid().build();
        PodCast podCast = createValid().addPodCastEpisode(podCastEpisode).build();

        Assert.assertTrue(podCast.getPodCastEpisodes().contains(podCastEpisode));
    }

    @Test
    public void isValid_true() {
        Assert.assertTrue(createValid().isValid());
    }

    @Test
    public void isValid_false() {
        Assert.assertFalse(createValid().title(null).isValid());
    }

    @Test
    public void create_valid_PodCast() {
        Assert.assertNotNull(createValid().build());
    }

    public static  PodCast.Builder createValid(){
        List<PodCastEpisode> podCastEpisode = new ArrayList<>();
        podCastEpisode.add(PodCastEpisodeTest.createValid().build());

        return PodCast.newBuilder().title("sdsds").description("dsdsdsd").createdDate(LocalDateTime.now()).
                feedURL("sdsdsd").id(22).publisher("sdsdsd").setPodCastCategories(PodCastCategoryType.fromString("ARTS")).
                addPodCastEpisodes(podCastEpisode);

    }


}