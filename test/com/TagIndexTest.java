package com;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.PodCastCatalogMetaData;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.util.IOUtil;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

/**
 * Created by miguelkrantz on 2019-04-24.
 */
public class TagIndexTest {


    @Test
    public void testName() throws Exception {


        ItunesSearchAPI query = ItunesSearchAPI.createCollector("term=P3&entity=podcast&limit=1&country=SE");
        List<PodCast> podCasts = query.collectPodCasts();

        for (PodCast podCast : podCasts) {
            System.out.println(podCast.getTitle());
            System.out.println(podCast.getDescription());
            for (PodCastEpisode podCastEpisode : podCast.getPodCastEpisodesInternal()) {
                System.out.println(podCastEpisode.getTitle());
                System.out.println(podCastEpisode.getDescription());
            }

        }


        //  File file = new File(TestUtil.IO_TEMP_DATA_DIRECTORY, "SE_MetaData.dat");

        //  PodCastCatalogMetaData metaData = (PodCastCatalogMetaData)IOUtil.getObject(file);
        System.out.println("metaData==" + podCasts);

      //  metaData.textSearchIndex.printIndex();

    }
}
