package com.podcastcatalog.service.podcastcatalog;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.PodCastCatalogMetaData;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeTest;
import com.podcastcatalog.model.podcastsearch.PodCastEpisodeResultItem;
import com.podcastcatalog.model.podcastsearch.PodCastResultItem;
import com.podcastcatalog.model.podcastsearch.ResultItem;
import com.podcastcatalog.util.IOUtil;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class TextSearchIndexTest {

    private TextSearchIndex searchEngine;

    @BeforeMethod(alwaysRun = true)//(groups = TestUtil.SLOW_TEST)
    public void setUp() {
        searchEngine = new TextSearchIndex();
    }

    /*@Test(groups = TestUtil.SLOW_TEST)
    public void max_match() {
        for (int i = 0; i < 100; i++) {
            searchEngine.addText("Sommar i  dfhd dfhd uidf hfhhfhf ABBA " + i, new MyItem("PodCast" + i));
        }
        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("s").size() == 50, "Size=" + searchEngine.lookup("s").size());
    }

    @Test
    public void max_characters_to_index() {
        searchEngine = new TextSearchIndex();

        searchEngine.addText("Sommar i Mastering a few crucial Mac keyboard shortcuts will make using your" +
                " Apple computer easier and much more efficient. Cutting your reliance on your mouse will help you work more quickly," +
                " and you’ll undoubtedly impress your family, friends and co-workers to no end. " +
                "You might end up becoming the go-to Mac person in your office, and we all know how wonderful that will be", new MyItem("PodCast 1"));
        searchEngine.addText("Sommar i Pekinganka",  new MyItem("PodCast 3"));
        searchEngine.addText("Sommar i Peking",  new MyItem("PodCast Match"));

        searchEngine.buildIndex();

        System.out.println(searchEngine.getStatus());
        searchEngine.printIndex();

        Assert.assertTrue(searchEngine.lookup("Somm").size() == 3);
//Memory        Assert.assertTrue(searchEngine.lookup("Apple").size() == 1);

    }

    @Test
    public void lookup_word_match_max_word_1() {
        searchEngine = new TextSearchIndex<>();

        searchEngine.addText("Sommar i ",  new MyItem("PodCast 1"));
        searchEngine.addText("Sommar i Pekinganka",  new MyItem("PodCast 3"));

        MyItem podCast = new MyItem("PodCast Match");
        searchEngine.addText("Sommar i Peking",  podCast);
        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("Somm").size() == 3);
    }
    @Test
    public void lookup_word_match() {
        searchEngine.addText("Sommar i ",  new MyItem("PodCast 1"));
        searchEngine.addText("Sommar i x ",  new MyItem("PodCast 2"));
        searchEngine.addText("Sommar i Pekinganka",  new MyItem("PodCast 3"));
        searchEngine.addText("Pek i ",  new MyItem("PodCast 4"));

        MyItem podCast = new MyItem("PodCast Match");
        searchEngine.addText("Sommar i Peking",  podCast);
        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("Peking").size() == 2);
    }

    @Test
    public void no_result_found() {
        searchEngine.addText("Sommar i ",  new MyItem("PodCast"));
        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("xtra").isEmpty());
        Assert.assertTrue(searchEngine.lookup("").isEmpty());
        Assert.assertTrue(searchEngine.lookup(" ").isEmpty());
        Assert.assertTrue(searchEngine.lookup(null).isEmpty());
    }

    @Test
    public void max_result() {

        for (int i = 0; i < 100; i++) {
            MyItem podcast1 = new MyItem("PodCast" + i);
            searchEngine.addText("Sommar i " + i,  podcast1);
        }

        MyItem found = new MyItem("Found");
        searchEngine.addText("Sommar i Stockholm", found);

        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("st").size() == 1);
        Assert.assertEquals(searchEngine.lookup("st").get(0), found);
    }*/

   /* @Test
    public void lookup_hit() {
        MyItem podcast1 = new MyItem("PodCast1");
        searchEngine.addText("Sommar i Peking",  podcast1);
        searchEngine.addText("Sommar i Peking kommer alltid före den i Sverige",  podcast1);

        searchEngine.addText("Sommar i Sweden ",  podcast1);

        searchEngine.buildIndex();
        searchEngine.printIndex();


        Assert.assertTrue(searchEngine.lookup("s").size() == 1);
        Assert.assertTrue(searchEngine.lookup("so").size() == 1);
        Assert.assertTrue(searchEngine.lookup("sommar").size() == 1);
        Assert.assertTrue(searchEngine.lookup("Sommar i ").size() == 1);
    }

    @Test
    public void lookup_hit_1() {
        MyItem podcast1 = new MyItem("PodCast1");
        searchEngine.addText(" Fallet Peter Mangs i Sverige",  podcast1);

        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("Fallet P").size() == 1);
    }


    @Test
    public void full_word_match() {
        MyItem podcast1 = new MyItem("PodCast1");
        MyItem podcast2 = new MyItem("PodCast2");
        searchEngine.addText("AnnaStadling Amerika",  podcast1);
        searchEngine.addText("Anna Stadling Amerika",  podcast2);

        searchEngine.buildIndex();
        searchEngine.printIndex();

        Assert.assertTrue(searchEngine.lookup("Anna").size() == 2);
    }
*/

    @Test
    public void doNotIndexEpisodes() {

        searchEngine.addText("#123 Berlin",
                new PodCastEpisodeResultItem(PodCastEpisodeTest.createValid().title("UsedInId_1")
                        .targetURL("https://www.id_1.se").build()));
        searchEngine.addText("x sommar x ",
                new PodCastEpisodeResultItem(PodCastEpisodeTest.createValid().title("UsedInId_1")
                        .targetURL("https://www.id_2.se").build()));

        searchEngine.buildIndex();
        searchEngine.printPodCastEpisodeIndex();

        Assert.assertTrue(searchEngine.lookupPodCastEpisodes("#123", 100).isEmpty());
        Assert.assertTrue(searchEngine.lookupPodCastEpisodes("123", 100).isEmpty());
        Assert.assertTrue(searchEngine.lookupPodCastEpisodes("x", 100).isEmpty());
    }

        @Test
   public void index_4_podCasts() {

       searchEngine.addText("Sommar i Stockholm" ,
               new PodCastResultItem("collectionId_1", "Title...", "www.dn2.se"));

       searchEngine.addText("Sola i Stockholm" ,
               new PodCastResultItem("collectionId_2", "Title...", "www.dn3.se"));

            searchEngine.addText("x " ,
                    new PodCastResultItem("collectionId_3", "Title...", "www.dn3.se"));

            searchEngine.buildIndex();
       searchEngine.printPodCastIndex();

       Assert.assertTrue(searchEngine.lookupPodCast("Sommar", 100).size() == 1);
       Assert.assertTrue(searchEngine.lookupPodCast("s", 100).size() == 2);
       Assert.assertTrue(searchEngine.lookupPodCast("x", 100).size() == 1);
   }

    @Test
    public void index_4_episodes() {

        searchEngine.addText("Jättelång testx dhsjch sdhgsdhgsdhgsd sommare vinter i Berlin i Stockholm -,2" ,
                new PodCastEpisodeResultItem(PodCastEpisodeTest.createValid().title("UsedInId_1")
                        .targetURL("https://www.id_1.se").build()));

        searchEngine.addText("Sommar i Stockholm -,2" ,
                new PodCastEpisodeResultItem(PodCastEpisodeTest.createValid().title("UsedInId_1")
                        .targetURL("https://www.id_2.se").build()));

        searchEngine.addText("Solsken i Stockholm 987" ,
                new PodCastEpisodeResultItem(PodCastEpisodeTest.createValid().title("UsedInId_1")
                        .targetURL("https://www.id_3.se").build()));

        searchEngine.addText("2 Bananer i Pjamas" ,
                new PodCastEpisodeResultItem(PodCastEpisodeTest.createValid().title("UsedInId_1")
                        .targetURL("https://www.id_4.se").build()));

        searchEngine.addText("#.12 Roller i Stockholm" ,
                new PodCastEpisodeResultItem(PodCastEpisodeTest.createValid().title("UsedInId_1")
                        .targetURL("https://www.id_5.se").build()));

        searchEngine.addText("Maria" +
                        "2012 hittas en ung tjej mördad i Landskrona. Polisens blickar riktas omedelbart mot hennes bror och det pratas snart om ett hedersmotiv." ,
                new PodCastEpisodeResultItem(PodCastEpisodeTest.createValid().title("UsedInId_1")
                        .targetURL("https://www.id_6.se").build()));

        searchEngine.buildIndex();
        searchEngine.printPodCastEpisodeIndex();

        Assert.assertTrue(searchEngine.lookupPodCastEpisodes("Sommar", 100).size() == 1);
        Assert.assertTrue(searchEngine.lookupPodCastEpisodes("qq", 100).isEmpty());
        Assert.assertTrue(searchEngine.lookupPodCastEpisodes("roller", 100).size() == 1);
        Assert.assertTrue(searchEngine.lookupPodCastEpisodes("Berlin", 100).size() == 1);
        Assert.assertTrue(searchEngine.lookupPodCastEpisodes("Landskrona ", 100).size() == 1);
    }

    @Test
    public void status() throws IOException, InterruptedException {

        TextSearchIndex newTextSearchIndex = new TextSearchIndex();

        for (int i = 0; i < 1; i++) {
            newTextSearchIndex.addText(i + ".\uD83D\uDC4D\uD83D\uDE0A \uD83D\uDC6E\uD83C\uDFFF\u200D♀️, 1212-12.ÅÄÖ @-,+0289375*.,äåöööö Sommar i Stockholm ksdksdjqsd dqjhdsgds dghsa dhsd dgsahgs dshghsgdhsa d gh" + i ,
                    new PodCastEpisodeResultItem(PodCastEpisodeTest.createValid().title("UsedInId" + i)
                            .targetURL("https://www.test.se" + i).build()));
        }


        for (int i = 0; i < 900; i++) {
            newTextSearchIndex.addText("Sommar i Stockholm" + i,
                    new PodCastResultItem("collecvtionId" + i, "Title " + i, "www.dsd.se"));
        }


        newTextSearchIndex.buildIndex();
        newTextSearchIndex.printPodCastEpisodeIndex();

        List<ResultItem> s = newTextSearchIndex.lookupPodCastEpisodes("Som",100);
        PodCastCatalogMetaData m = new PodCastCatalogMetaData();
        m.textSearchIndex = newTextSearchIndex;


        File targetFile = new File(TestUtil.IO_TEMP_DATA_DIRECTORY, "SE_MetaData.dat");
        IOUtil.saveAsObject(m, targetFile);

        Thread.sleep(500);

        System.out.println("After load...");
        PodCastCatalogMetaData loadedPodCastCatalogMetaData = (PodCastCatalogMetaData) IOUtil.getObject(targetFile);

        loadedPodCastCatalogMetaData.textSearchIndex.printPodCastEpisodeIndex();
        // TestUtil.assertSerializable();
    }
}
