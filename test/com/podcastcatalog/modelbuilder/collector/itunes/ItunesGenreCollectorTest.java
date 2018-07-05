package com.podcastcatalog.modelbuilder.collector.itunes;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by miguelkrantz on 2018-07-05.
 */
public class ItunesGenreCollectorTest {


    @Test
    public void testParseID() throws Exception {

        ItunesGenreCollector itunesGenreCollector = new ItunesGenreCollector(PodCastCatalogLanguage.SE,
                100, "https://itunes.apple.com/se/genre/podcaster-tv-och-film/id1309?mt=2");

        List<Long> podCastIds = itunesGenreCollector.getPodCastIds();

        System.out.println(podCastIds.size() + " IDS==" + podCastIds);
    }

}