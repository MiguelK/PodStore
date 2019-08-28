package com.podcastcatalog.tag;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import org.testng.annotations.Test;

import java.util.List;

public class TagIndexTest {

    @Test
    public void parseName() throws Exception {
        TagManager.getInstance().createTagIndex("SWE");
        TagManager.getInstance().indexContent("zlatan ibrahimovic");
    }

        @Test
    public void testName() throws Exception {

        TagManager.getInstance().createTagIndex("SWE");

        ItunesSearchAPI query = ItunesSearchAPI.createCollector("term=Spår&entity=podcast&limit=1&country=SE");
        List<PodCast> podCasts = query.collectPodCasts();
        PodCast podCast1 = podCasts.get(0);

        String title = podCast1.getTitle();
        String description = podCast1.getLatestPodCastEpisode().getDescription();

      //  TagManager.getInstance().indexContent(title);
        TagManager.getInstance().indexContent(description + "zlatan ibrahimovic" + " Jan Banan");

        /*for (PodCast podCast : podCasts) {
            System.out.println(podCast.getTitle());
            System.out.println(podCast.getDescription());
            for (PodCastEpisode podCastEpisode : podCast.getPodCastEpisodesInternal()) {
                System.out.println(podCastEpisode.getTitle());
                System.out.println(podCastEpisode.getDescription());
            }

        }*/


        //  File file = new File(TestUtil.IO_TEMP_DATA_DIRECTORY, "SE_MetaData.dat");

        //  PodCastCatalogMetaData metaData = (PodCastCatalogMetaData)IOUtil.getObject(file);
        System.out.println("metaData==" + podCast1);

      //  metaData.textSearchIndex.printIndex();

    }
}
