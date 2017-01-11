package com.podcastcatalog.service.podcastcatalog;

import com.podcastcatalog.TestUtil;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;


public class TextSearchIndexTest {

    private TextSearchIndex<MyItem> searchEngine;

    @BeforeMethod(alwaysRun = true)//(groups = TestUtil.SLOW_TEST)
    public void setUp() {
        searchEngine = new TextSearchIndex<>();
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void max_match() {
        for (int i = 0; i < 100; i++) {
            searchEngine.addText("Sommar i  dfhd dfhd uidf hfhhfhf ABBA " + i, TextSearchIndex.Prio.HIGHEST, new MyItem("PodCast" + i));
        }
        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("s").size() == 50, "Size=" + searchEngine.lookup("s").size());
    }

    @Test
    public void max_characters_to_index() {
        searchEngine = new TextSearchIndex<>();

        searchEngine.addText("Sommar i Mastering a few crucial Mac keyboard shortcuts will make using your" +
                " Apple computer easier and much more efficient. Cutting your reliance on your mouse will help you work more quickly," +
                " and you’ll undoubtedly impress your family, friends and co-workers to no end. " +
                "You might end up becoming the go-to Mac person in your office, and we all know how wonderful that will be", TextSearchIndex.Prio.HIGHEST, new MyItem("PodCast 1"));
        searchEngine.addText("Sommar i Pekinganka", TextSearchIndex.Prio.HIGHEST, new MyItem("PodCast 3"));
        searchEngine.addText("Sommar i Peking", TextSearchIndex.Prio.HIGH, new MyItem("PodCast Match"));

        searchEngine.buildIndex();

        System.out.println(searchEngine.getStatus());
        searchEngine.printIndex();

        Assert.assertTrue(searchEngine.lookup("Somm").size() == 3);
//Memory        Assert.assertTrue(searchEngine.lookup("Apple").size() == 1);

    }

    @Test
    public void lookup_word_match_max_word_1() {
        searchEngine = new TextSearchIndex<>();

        searchEngine.addText("Sommar i ", TextSearchIndex.Prio.HIGHEST, new MyItem("PodCast 1"));
        searchEngine.addText("Sommar i Pekinganka", TextSearchIndex.Prio.HIGHEST, new MyItem("PodCast 3"));

        MyItem podCast = new MyItem("PodCast Match");
        searchEngine.addText("Sommar i Peking", TextSearchIndex.Prio.HIGH, podCast);
        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("Somm").size() == 3);
    }
    @Test
    public void lookup_word_match() {
        searchEngine.addText("Sommar i ", TextSearchIndex.Prio.HIGHEST, new MyItem("PodCast 1"));
        searchEngine.addText("Sommar i x ", TextSearchIndex.Prio.HIGHEST, new MyItem("PodCast 2"));
        searchEngine.addText("Sommar i Pekinganka", TextSearchIndex.Prio.HIGHEST, new MyItem("PodCast 3"));
        searchEngine.addText("Pek i ", TextSearchIndex.Prio.HIGHEST, new MyItem("PodCast 4"));

        MyItem podCast = new MyItem("PodCast Match");
        searchEngine.addText("Sommar i Peking", TextSearchIndex.Prio.HIGH, podCast);
        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("Peking").size() == 2);
        Assert.assertEquals(searchEngine.lookup("Peking").get(0), podCast);
    }

    @Test
    public void no_result_found() {
        searchEngine.addText("Sommar i ", TextSearchIndex.Prio.HIGHEST, new MyItem("PodCast"));
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
            searchEngine.addText("Sommar i " + i, TextSearchIndex.Prio.HIGHEST, podcast1);
        }

        MyItem found = new MyItem("Found");
        searchEngine.addText("Sommar i Stockholm", TextSearchIndex.Prio.LOW, found);

        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("st").size() == 1);
        Assert.assertEquals(searchEngine.lookup("st").get(0), found);
    }

    @Test
    public void lookup_hit() {
        MyItem podcast1 = new MyItem("PodCast1");
        searchEngine.addText("Sommar i Peking", TextSearchIndex.Prio.HIGHEST, podcast1);
        searchEngine.addText("Sommar i Peking kommer alltid före den i Sverige", TextSearchIndex.Prio.HIGHEST, podcast1);

        searchEngine.buildIndex();
        searchEngine.printIndex();


        Assert.assertTrue(searchEngine.lookup("s").size() == 1);
        Assert.assertTrue(searchEngine.lookup("so").size() == 1);
        Assert.assertTrue(searchEngine.lookup("sommar").size() == 1);
//To long match        Assert.assertTrue(searchEngine.lookup("Sommar i Peking").size() == 1);
//        Assert.assertTrue(searchEngine.lookup("Sommar i Peking kommer alltid före den i Sverige").size() == 1);
        Assert.assertTrue(searchEngine.lookup("Sommar i ").size() == 1);
    }

    @Test
    public void lookup_hit_1() {
        MyItem podcast1 = new MyItem("PodCast1");
        searchEngine.addText(" Fallet Peter Mangs i Sverige", TextSearchIndex.Prio.HIGHEST, podcast1);

        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("Fallet P").size() == 1);
    }


    @Test
    public void full_word_match() {
        MyItem podcast1 = new MyItem("PodCast1");
        MyItem podcast2 = new MyItem("PodCast2");
        searchEngine.addText("AnnaStadling Amerika", TextSearchIndex.Prio.HIGH, podcast1);
        searchEngine.addText("Anna Stadling Amerika", TextSearchIndex.Prio.HIGH, podcast2);

        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("Anna").size() == 2);
        Assert.assertEquals(searchEngine.lookup("Anna").get(0), podcast2);
    }

    @Test
    public void simple_api_test() {
        MyItem podcast1 = new MyItem("PodCast1");
        MyItem podcast2 = new MyItem("PodCast2");
        MyItem episode2 = new MyItem("Episode2");

        //Add everything to index...
        searchEngine.addText("Anna Stadling", TextSearchIndex.Prio.HIGHEST, podcast1);
        searchEngine.addText("Sommar i Paris", TextSearchIndex.Prio.HIGHEST, podcast2);
        searchEngine.addText("Paris i Paris centrum.", TextSearchIndex.Prio.HIGH, episode2);

        searchEngine.buildIndex();

        List<MyItem> search1 = searchEngine.lookup("Pa");

        Assert.assertTrue(search1.size() == 2, "Size=" + search1.size());
        Assert.assertEquals(search1.get(0), podcast2);//Pod higer prio
        Assert.assertEquals(search1.get(1), episode2);
    }

    @Test
    public void status() {

        for (int i = 0; i < 100; i++) {
            MyItem podcast1 = new MyItem("PodCast" + i);
            searchEngine.addText("Sommar i " + i, TextSearchIndex.Prio.HIGHEST, podcast1);
        }

        MyItem found = new MyItem("Found");
        searchEngine.addText("Sommar i Stockholm", TextSearchIndex.Prio.LOW, found);

        searchEngine.buildIndex();

        Assert.assertNotNull(searchEngine.getStatus());
    }
}
