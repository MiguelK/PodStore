package com.podcastcatalog.model.podcastcatalog;

import com.google.gson.Gson;
import com.podcastcatalog.TestUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

public class PodCastEpisodeTest {

    private static final Gson GSON = new Gson();

    @Test
    public void toGSON_duration() {
        TestUtil.assertToJSONContains(createValid().build(),"duration\":{\"totalSeconds\":5055,\"hours\":1,\"minutes\":24,\"seconds\":15}");
    }

    @Test
    public void serializable() {
        PodCastEpisode podCastEpisode = createValid().build();
        TestUtil.assertSerializable(podCastEpisode);
    }

    @Test
    public void testToString() {
        String s = createValid().build().toString();
        Assert.assertTrue(s.contains("duration"));
        Assert.assertTrue(s.contains("title"));
        Assert.assertTrue(s.contains("fileSize"));
        Assert.assertTrue(s.contains("description"));
        Assert.assertTrue(s.contains("podCastType"));
        Assert.assertTrue(s.contains("podCastCollectionId"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podCastCollectionId_empty() {
        createValid().podCastCollectionId(" ").build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podCastId_null() {
        createValid().podCastCollectionId(null).build();
    }

    @Test
    public void valid_podCastCollectionId() {
       Assert.assertEquals(createValid().podCastCollectionId("450").build().getPodCastCollectionId(),"450");
    }

    @Test
    public void getDuration() {
        Assert.assertTrue(createValid().duration(PodCastEpisodeDuration.parse("01:00:12")).build().getDuration().getHour()==1);
    }

    @Test
    public void toJSON() {
        TestUtil.assertToJSONNotNull(createValid());
    }

    @Test
    public void trim_title() {
        Assert.assertEquals(createValid().title(" some text ").build().getTitle(),"some text");
    }

    @Test
    public void no_duration_is_ok() {
        PodCastEpisode podCastEpisode = createValid().duration(null).build();
        String s = GSON.toJson(podCastEpisode);
        Assert.assertFalse(s.contains("duration"));
    }

    @Test
    public void with_duration() {
        PodCastEpisode.Builder valid = createValid();
        PodCastEpisode podCastEpisode = valid.duration(PodCastEpisodeDuration.parse("02:23:12")).build();
        TestUtil.assertToJSONContains(podCastEpisode, "duration");
    }

    @Test
    public void fileSizeInMegaByte() {
        Assert.assertEquals(createValid().fileSizeInMegaByte(PodCastEpisodeFileSize.parse("9155554")).build().getFileSize().getFileSizeInBytes(),9155554);
    }
    @Test
    public void trim_description() {
        Assert.assertEquals(createValid().description(" some text ").build().getDescription(),"some text");
    }
    @Test
    public void trim_targetURL() {
        Assert.assertEquals(createValid().targetURL(" some text ").build().getTargetURL(),"some text");
    }

    @Test
    public void podCastType() {
        Assert.assertEquals(createValid().podCastType(PodCastEpisodeType.Audio).build().getPodCastType(),PodCastEpisodeType.Audio);
    }

    @Test
    public void createdDate() {
        LocalDateTime createdDate = LocalDateTime.now().minusWeeks(12);
        Assert.assertEquals(createValid().createdDate(createdDate).build().getCreatedDate(),createdDate);
    }

    @Test
    public void testId() {
        Assert.assertEquals(createValid().build().getId(), "8788-titlename");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_id_null() {
        createValid().id(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_id_empty() {
        createValid().id(" ").build();
    }

    @Test
    public void create_valid_podcast() {
        Assert.assertNotNull(createValid());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void inavlid_description_null() {
        createValid().description(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void inavlid_description_emptyl() {
        createValid().description(" ").build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void inavlid_title_null() {
        createValid().title(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid_podCastType() {
        createValid().podCastType(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void inavlid_title_empty() {
        createValid().title(" ").build();
    }
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void inavlid_targetURL_null() {
        createValid().targetURL(null).build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void inavlid_targetURL_empty() {
        createValid().targetURL(" ").build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void inavlid_createdDate_null() {
        createValid().createdDate(null).build();
    }

    @Test
    public void title_and_targetURL_is_equal() {
        PodCastEpisode a = createValid().build();
        PodCastEpisode b = createValid().build();
        Assert.assertTrue(a.equals(b));
    }
    @Test
    public void not_equal_title() {
        PodCastEpisode a = createValid().title("Some other title").build();
        PodCastEpisode b = createValid().build();
        Assert.assertFalse(a.equals(b));
    }

    @Test
    public void podCastCollectionId_trim() {
        Assert.assertEquals(createValid().podCastCollectionId(" 55 ").build().getPodCastCollectionId(),"55");
    }

    @Test
    public void not_equal_targetURL() {
        PodCastEpisode a = createValid().targetURL("Some other target").build();
        PodCastEpisode b = createValid().build();
        Assert.assertFalse(a.equals(b));
    }

    public static PodCastEpisode.Builder createValid(){

        return PodCastEpisode.newBuilder().description("sdsd").title("TitleName").id("9999")
                .fileSizeInMegaByte(PodCastEpisodeFileSize.parse("9155554")).duration(PodCastEpisodeDuration.parse("01:24:15"))
                .createdDate(LocalDateTime.now()).targetURL("dfdsdf").podCastType(PodCastEpisodeType.Audio).podCastCollectionId("8788");

    }
}