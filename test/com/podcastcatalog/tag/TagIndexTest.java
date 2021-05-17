package com.podcastcatalog.tag;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.modelbuilder.collector.itunes.ItunesSearchAPI;
import com.podcastcatalog.tag.model.Tag;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class TagIndexTest {


    //@Test(groups = TestUtil.SLOW_TEST)
    public void basicAPI_Test_trueCrime() throws Exception {

        TagManager.getInstance().configure(TagManager.Lang.SWE);

        ItunesSearchAPI query = ItunesSearchAPI.createCollector("term=Spår&entity=podcast&limit=1&country=SE");
        List<PodCast> podCasts = query.collectPodCasts();
        PodCast podCast = podCasts.get(0);

        TagManager.getInstance().index(podCast, TagManager.Lang.SWE);

        TagSearchResult searchResult = TagManager.getInstance().search("Kala"); //kalamarksmordet

        System.out.println(searchResult.getPodCasts().size());

        Tag tagForPodCast = TagManager.getInstance().getTagForPodCast(podCast.getCollectionId());

        Tag tagForPodCastEpisode = TagManager.getInstance().getTagForPodCastEpisode(podCast.getPodCastEpisodes().get(0).getId());

       Assert.assertTrue(searchResult.getPodCasts().size() == 1);


        //Verify  created index file(s)
            //  File file = new File(TestUtil.IO_TEMP_DATA_DIRECTORY, "SE_MetaData.dat");
        //  PodCastCatalogMetaData metaData = (PodCastCatalogMetaData)IOUtil.getObject(file);
      //  metaData.textSearchIndex.printIndex();

    }
}
