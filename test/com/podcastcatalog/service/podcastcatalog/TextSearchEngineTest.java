package com.podcastcatalog.service.podcastcatalog;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;


public class TextSearchEngineTest {

    private TextSearchEngine<MyItem> searchEngine;

    @BeforeMethod
    public void setUp() {
        searchEngine = new TextSearchEngine<>();
    }

    @Test
    public void max_match() {
        for (int i = 0; i < 100; i++) {
            searchEngine.addText("Sommar i  dfhd dfhd uidf hfhhfhf ABBA " + i, TextSearchEngine.Prio.HIGHEST, new MyItem("PodCast" + i));
        }
        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("s").size() == 50, "Size=" + searchEngine.lookup("s").size());
    }

    @Test
    public void lookup_word_match() {
        searchEngine.addText("Sommar i ", TextSearchEngine.Prio.HIGHEST, new MyItem("PodCast 1"));
        searchEngine.addText("Sommar i x ", TextSearchEngine.Prio.HIGHEST, new MyItem("PodCast 2"));
        searchEngine.addText("Sommar i Pekinganka", TextSearchEngine.Prio.HIGHEST, new MyItem("PodCast 3"));
        searchEngine.addText("Pek i ", TextSearchEngine.Prio.HIGHEST, new MyItem("PodCast 4"));

        MyItem podCast = new MyItem("PodCast Match");
        searchEngine.addText("Sommar i Peking", TextSearchEngine.Prio.HIGH, podCast);
        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("Peking").size() == 2);
        Assert.assertEquals(searchEngine.lookup("Peking").get(0), podCast);
    }

    @Test
    public void no_result_found() {
        searchEngine.addText("Sommar i ", TextSearchEngine.Prio.HIGHEST, new MyItem("PodCast"));
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
            searchEngine.addText("Sommar i " + i, TextSearchEngine.Prio.HIGHEST, podcast1);
        }

        MyItem found = new MyItem("Found");
        searchEngine.addText("Sommar i Stockholm", TextSearchEngine.Prio.LOW, found);

        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("st").size() == 1);
        Assert.assertEquals(searchEngine.lookup("st").get(0), found);
    }

    @Test
    public void lookup_hit() {
        MyItem podcast1 = new MyItem("PodCast1");
        searchEngine.addText("Sommar i Peking", TextSearchEngine.Prio.HIGHEST, podcast1);
        searchEngine.buildIndex();

        Assert.assertTrue(searchEngine.lookup("s").size() == 1);
        Assert.assertTrue(searchEngine.lookup("so").size() == 1);
        Assert.assertTrue(searchEngine.lookup("sommar").size() == 1);
        Assert.assertTrue(searchEngine.lookup("Sommar i Peking").size() == 1);
    }

    @Test
    public void full_word_match() {
        MyItem podcast1 = new MyItem("PodCast1");
        MyItem podcast2 = new MyItem("PodCast2");
        searchEngine.addText("AnnaStadling Amerika", TextSearchEngine.Prio.HIGH, podcast1);
        searchEngine.addText("Anna Stadling Amerika", TextSearchEngine.Prio.HIGH, podcast2);

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
        searchEngine.addText("Anna Stadling", TextSearchEngine.Prio.HIGHEST, podcast1);
        searchEngine.addText("Sommar i Paris", TextSearchEngine.Prio.HIGHEST, podcast2);
        searchEngine.addText("Paris i Paris centrum.", TextSearchEngine.Prio.HIGH, episode2);

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
            searchEngine.addText("Sommar i " + i, TextSearchEngine.Prio.HIGHEST, podcast1);
        }

        MyItem found = new MyItem("Found");
        searchEngine.addText("Sommar i Stockholm", TextSearchEngine.Prio.LOW, found);

        searchEngine.buildIndex();

        Assert.assertNotNull(searchEngine.getStatus());
    }
}
