package com.podcastcatalog.modelbuilder.collector.okihika;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastCategory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class PodCastCategoryCollectorOkihikaTest {

    @Test(groups = TestUtil.SLOW_TEST)
    public void testName() {
        PodCastCollectorOkihika podCastCollectorOkihika =  PodCastCollectorOkihika.parseSWE(PodCastCollectorOkihika.TopList.NEWS_POLITICS, 3);
        List<PodCast> podCasts = podCastCollectorOkihika.collectPodCasts();
        Assert.assertTrue(podCasts.size()==3," was=" + podCasts.size());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void parse() {
        PodCastCollectorOkihika podCastCollectorOkihika =  PodCastCollectorOkihika.parseSWE(PodCastCollectorOkihika.TopList.ARTS, 3);
        List<PodCast> podCasts = podCastCollectorOkihika.collectPodCasts();
        Assert.assertFalse(podCasts.isEmpty());
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void testCollectPodCastCategory_3() {

        //FIXME MUSIC error parsing id
        PodCastCategoryCollectorOkihika collector = new PodCastCategoryCollectorOkihika(PodCastCollectorOkihika.TopList.ARTS, 3, "descr", "image");

        PodCastCategory podCastCategory = collector.collectCategories();

        Assert.assertFalse(podCastCategory.getPodCasts().isEmpty());
    }

    @Test
    public void parseId_valid() {
        PodCastCollectorOkihika podCastCollectorOkihika =  PodCastCollectorOkihika.parseSWE(PodCastCollectorOkihika.TopList.ARTS, 3);

        Long integer = podCastCollectorOkihika.parseID("<a href=\"https://itunes.apple.com/se/podcast/svenska-mordhistorier/id1067686460?mt=2&amp;uo=2&amp;at=10l4Yx&amp;app=itunes\" rel=\"nofollow\"><img class=\"img-polaroid\" src=\"http://is4.mzstatic.com/image/thumb/Podcasts62/v4/3f/e9/d4/3fe9d4cf-634c-713e-4a7f-7f35c1ca4f2f/mza_3905801940985720885.jpg/100x100bb-85.jpg\">\n" +
                " <div class=\"spacer\"></div><span class=\"spacer10 visible-xs\"></span><img class=\"itunes\" height=\"30px\" src=\"//okihika.com/itunes.png\"></a>");

        Assert.assertNotNull(integer);
        Assert.assertTrue(podCastCollectorOkihika.parseID("entity/id425654569") == 425654569L);
        Assert.assertTrue(podCastCollectorOkihika.parseID("entity/id4654569x") == 4654569L);
        Assert.assertTrue(podCastCollectorOkihika.parseID("entity/id4654569") == 4654569L);
        Assert.assertTrue(podCastCollectorOkihika.parseID("entity/id4555555654569?") == 4555555654569L);
        Assert.assertTrue(podCastCollectorOkihika.parseID(" entity/id4555555654569 ") == 4555555654569L);
    }

}