package com.podcastcatalog.modelbuilder.collector.itunes;

import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by miguelkrantz on 2018-07-05.
 */
public class PodCastIdCollectorTest {


    @Test
    public void testParseId() throws Exception {

        PodCastIdCollector podCastIdCollector = new PodCastIdCollector(PodCastCatalogLanguage.US, PodCastIdCollector.Category.ARTS);

        List<Long> podCastIds = podCastIdCollector.getPodCastIds();

        System.out.println(podCastIds.size() + " IDS==" + podCastIds);
    }

}