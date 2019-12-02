package com.podcastcatalog.modelbuilder.collector.itunes;

import com.podcastcatalog.TestUtil;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastcatalog.PodCastCategoryType;
import com.podcastcatalog.modelbuilder.collector.PodCastFeedParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * Created by miguelkrantz on 2018-07-05.
 */
public class PodCastIdCollectorTest {

    private static final String artworkUrl600 = "http://is4.mzstatic.com/image/thumb/Music62/v4/50/78/30/507830d2-568c-86e2-ecd8-ce61b6444770/source/100x100bb.jpg";

    @Test(groups = TestUtil.SLOW_TEST)
    public void testCategories() throws Exception {

        List<Long> podCastIds = new PodCastIdCollector(PodCastCatalogLanguage.SE, PodCastIdCollector.Category.ANIMATION_MANGA,
                "").getPodCastIds();
        System.out.println("Start fetch ids from ids=" + podCastIds.size());

        PodCast podCast = ItunesSearchAPI.lookupPodCast(String.valueOf(podCastIds.get(0))).get();
        Optional<PodCast> podCast1 = PodCastFeedParser.parse(new URL(podCast.getFeedURL()),
                artworkUrl600, "4444", 400);

        PodCast podCast2 = podCast1.get();
        List<PodCastCategoryType> podCastCategories = podCast2.getPodCastCategories();
        System.out.println(podCastCategories);

    }
        @Test(groups = TestUtil.SLOW_TEST)
    public void testParseId() throws Exception {

        for (PodCastCatalogLanguage language : PodCastCatalogLanguage.values()) {


            for (PodCastIdCollector.Category category : PodCastIdCollector.Category.values()) {
                List<Long> podCastIds = new PodCastIdCollector(language, category, "").getPodCastIds();
                System.out.println("Start fetch ids from " + category.name() + ",lang=" + language.name() + ", ids=" + podCastIds.size());

                PodCast podCast = ItunesSearchAPI.lookupPodCast(String.valueOf(podCastIds.get(0))).get();
                Optional<PodCast> podCast1 = PodCastFeedParser.parse(new URL(podCast.getFeedURL()),
                        artworkUrl600, "4444", 400);
                List<PodCastCategoryType> podCastCategories = podCast1.get().getPodCastCategories();

                System.out.println("podCastCategories=" + podCastCategories);

                Assert.assertFalse(podCastIds.isEmpty());
                Thread.sleep(3000);
            }


          //  Assert.assertFalse(new PodCastIdCollector(language, PodCastIdCollector.Category.HIGHER_EDUCATION, "").getPodCastIds().isEmpty());
        }

      //  Assert.assertFalse(new PodCastIdCollector(PodCastCatalogLanguage.SE, PodCastIdCollector.Category.VIDEO_GAMES, "").getPodCastIds().isEmpty());


        //      System.out.println(podCastIds.size() + " IDS==" + podCastIds);
    }

    @Test(groups = TestUtil.SLOW_TEST)
    public void topList() throws Exception {

        PodCastIdCollector podCastIdCollector = PodCastIdCollector.createPodCastIdCollector(PodCastCatalogLanguage.SE,
                PodCastIdCollector.Category.TOPLIST_COUNTRY);

        List<Long> podCastIds = podCastIdCollector.getPodCastIds();

        System.out.println(podCastIds.size() + " IDS==" + podCastIds);
    }

 /*   {
        "feed":{
        "title":"Top Audio Podcasts", "id":
        "https://rss.itunes.apple.com/api/v1/se/podcasts/top-podcasts/all/10/explicit.json", "author":{
            "name":"iTunes Store", "uri":"http://wwww.apple.com/se/itunes/"
        },"links":[{
            "self":"https://rss.itunes.apple.com/api/v1/se/podcasts/top-podcasts/all/10/explicit.json"
        },{
            "alternate":"https://itunes.apple.com/WebObjects/MZStore.woa/wa/viewTop?genreId=26\u0026popId=28"
        }],"copyright":"Copyright © 2018 Apple Inc. Все права защищены.", "country":"se", "icon":
        "http://itunes.apple.com/favicon.ico", "updated":"2018-07-06T03:17:56.000-07:00", "results":[{
            "artistName":"Sveriges Radio", "id":"284610981", "releaseDate":"2018-07-06", "contentAdvisoryRating":
            "Clean", "name":"Sommar \u0026 Vinter i P1", "kind":"podcast", "artistId":"211312173", "artistUrl":
            "https://itunes.apple.com/se/artist/sveriges-radio/211312173?mt=2", "artworkUrl100":
            "https://is4-ssl.mzstatic.com/image/thumb/Music118/v4/cc/2f/d5/cc2fd5dc-73a6-8f7a-9379-f03f2c69e7b6/source/200x200bb.png", "genres":[
            {
                "genreId":"1324", "name":"Samhälle och kultur", "url":"https://itunes.apple.com/se/genre/id1324"
            },{
                "genreId":"26", "name":"Podcaster", "url":"https://itunes.apple.com/se/genre/id26"
            }],"url":"https://itunes.apple.com/se/podcast/sommar-vinter-i-p1/id284610981?mt=2"
        },{
            "artistName":"Nils Bergman", "id":"897819815", "releaseDate":"2018-06-24", "contentAdvisoryRating":
            "Explicit", "name":"Rättegångspodden", "kind":"podcast", "artworkUrl100":
            "https://is1-ssl.mzstatic.com/image/thumb/Music125/v4/d9/1d/85/d91d858c-1ba8-990f-8e73-bbcccfddfa2f/source/200x200bb.png", "genres":[
            {
                "genreId":"1462", "name":"Historia", "url":"https://itunes.apple.com/se/genre/id1462"
            },{
                "genreId":"26", "name":"Podcaster", "url":"https://itunes.apple.com/se/genre/id26"
            },{
                "genreId":"1324", "name":"Samhälle och kultur", "url":"https://itunes.apple.com/se/genre/id1324"
            }],"url":"https://itunes.apple.com/se/podcast/r%C3%A4tteg%C3%A5ngspodden/id897819815?mt=2"
        },{
            "artistName":"Perfect Day Media", "id":"1271090133", "releaseDate":"2018-07-01", "contentAdvisoryRating":
            "Clean", "name":"Livet på läktaren", "kind":"podcast", "artworkUrl100":
            "https://is5-ssl.mzstatic.com/image/thumb/Music128/v4/6c/03/6c/6c036cea-fb69-ec30-d54e-e76d0d0ddf31/source/200x200bb.png", "genres":[
            {
                "genreId":"1303", "name":"Komedi", "url":"https://itunes.apple.com/se/genre/id1303"
            },{
                "genreId":"26", "name":"Podcaster", "url":"https://itunes.apple.com/se/genre/id26"
            },{
                "genreId":"1324", "name":"Samhälle och kultur", "url":"https://itunes.apple.com/se/genre/id1324"
            },{
                "genreId":"1302", "name":"Personliga dagböcker", "url":"https://itunes.apple.com/se/genre/id1302"
            }],"url":"https://itunes.apple.com/se/podcast/livet-p%C3%A5-l%C3%A4ktaren/id1271090133?mt=2"
        },{
            "artistName":"Sveriges Radio", "id":"308339623", "releaseDate":"2018-07-05", "contentAdvisoryRating":
            "Clean", "name":"P3 Dokumentär", "kind":"podcast", "artistId":"211312173", "artistUrl":
            "https://itunes.apple.com/se/artist/sveriges-radio/211312173?mt=2", "artworkUrl100":
            "https://is5-ssl.mzstatic.com/image/thumb/Music118/v4/e5/3d/49/e53d4972-48a4-ffa2-0c60-efd375fbf4d5/source/200x200bb.png", "genres":[
            {
                "genreId":"1311", "name":"Nyheter och politik", "url":"https://itunes.apple.com/se/genre/id1311"
            },{
                "genreId":"26", "name":"Podcaster", "url":"https://itunes.apple.com/se/genre/id26"
            }],"url":"https://itunes.apple.com/se/podcast/p3-dokument%C3%A4r/id308339623?mt=2"
        },{
            "artistName":"Alice Stenlöf och Bianca Ingrosso", "id":"1316796982", "releaseDate":
            "2018-07-02", "contentAdvisoryRating":"Explicit", "name":
            "Alice \u0026 Bianca - Har du sagt A får du säga B", "kind":"podcast", "artworkUrl100":
            "https://is4-ssl.mzstatic.com/image/thumb/Music118/v4/e0/70/83/e070839c-2498-1782-e2fe-b3d727fd1500/source/200x200bb.png", "genres":[
            {
                "genreId":"1459", "name":"Mode och skönhet", "url":"https://itunes.apple.com/se/genre/id1459"
            },{
                "genreId":"26", "name":"Podcaster", "url":"https://itunes.apple.com/se/genre/id26"
            },{
                "genreId":"1301", "name":"Konst", "url":"https://itunes.apple.com/se/genre/id1301"
            },{
                "genreId":"1303", "name":"Komedi", "url":"https://itunes.apple.com/se/genre/id1303"
            },{
                "genreId":"1309", "name":"TV och film", "url":"https://itunes.apple.com/se/genre/id1309"
            }],"url":
            "https://itunes.apple.com/se/podcast/alice-bianca-har-du-sagt-a-f%C3%A5r-du-s%C3%A4ga-b/id1316796982?mt=2"
        },{
            "artistName":"RadioPlay", "id":"1067686460", "releaseDate":"2018-07-03", "contentAdvisoryRating":
            "Clean", "name":"Svenska Mordhistorier", "kind":"podcast", "artistId":"407482243", "artistUrl":
            "https://itunes.apple.com/se/artist/radioplay/407482243?mt=2", "artworkUrl100":
            "https://is3-ssl.mzstatic.com/image/thumb/Music125/v4/4e/16/f9/4e16f969-a28b-b3f3-8fb9-5099337675e4/source/200x200bb.png", "genres":[
            {
                "genreId":"1405", "name":"Scenkonst", "url":"https://itunes.apple.com/se/genre/id1405"
            },{
                "genreId":"26", "name":"Podcaster", "url":"https://itunes.apple.com/se/genre/id26"
            },{
                "genreId":"1301", "name":"Konst", "url":"https://itunes.apple.com/se/genre/id1301"
            }],"url":"https://itunes.apple.com/se/podcast/svenska-mordhistorier/id1067686460?mt=2"
        },{
            "artistName":"Alexander Pärleros", "id":"985517492", "releaseDate":"2018-07-04", "contentAdvisoryRating":
            "Clean", "name":"Framgångspodden", "kind":"podcast", "artistId":"1209621121", "artistUrl":
            "https://itunes.apple.com/se/artist/alexander-p%C3%A4rleros/1209621121?mt=2", "artworkUrl100":
            "https://is5-ssl.mzstatic.com/image/thumb/Music118/v4/28/c8/56/28c856c2-6d6a-13a8-3898-7592bb324ff8/source/200x200bb.png", "genres":[
            {
                "genreId":"1410", "name":"Karriär", "url":"https://itunes.apple.com/se/genre/id1410"
            },{
                "genreId":"26", "name":"Podcaster", "url":"https://itunes.apple.com/se/genre/id26"
            },{
                "genreId":"1321", "name":"Näringsliv", "url":"https://itunes.apple.com/se/genre/id1321"
            }],"url":"https://itunes.apple.com/se/podcast/framg%C3%A5ngspodden/id985517492?mt=2"
        },{
            "artistName":"filipandfredrik.com", "id":"492695082", "releaseDate":"2018-07-05", "contentAdvisoryRating":
            "Clean", "name":"Filip \u0026 Fredrik podcast", "kind":"podcast", "artworkUrl100":
            "https://is1-ssl.mzstatic.com/image/thumb/Music115/v4/fc/0d/b2/fc0db2b4-8194-30a2-2ef9-a5ad784d0c31/source/200x200bb.png", "genres":[
            {
                "genreId":"1303", "name":"Komedi", "url":"https://itunes.apple.com/se/genre/id1303"
            },{
                "genreId":"26", "name":"Podcaster", "url":"https://itunes.apple.com/se/genre/id26"
            }],"url":"https://itunes.apple.com/se/podcast/filip-fredrik-podcast/id492695082?mt=2"
        },{
            "artistName":"RadioPlay", "id":"1152876845", "releaseDate":"2018-07-04", "contentAdvisoryRating":
            "Explicit", "name":"Alla Våra Ligg", "kind":"podcast", "artistId":"407482243", "artistUrl":
            "https://itunes.apple.com/se/artist/radioplay/407482243?mt=2", "artworkUrl100":
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/cd/77/be/cd77be73-e0f4-1eb4-e3ac-145bcaa09dac/source/200x200bb.png", "genres":[
            {
                "genreId":"1302", "name":"Personliga dagböcker", "url":"https://itunes.apple.com/se/genre/id1302"
            },{
                "genreId":"26", "name":"Podcaster", "url":"https://itunes.apple.com/se/genre/id26"
            },{
                "genreId":"1324", "name":"Samhälle och kultur", "url":"https://itunes.apple.com/se/genre/id1324"
            }],"url":"https://itunes.apple.com/se/podcast/alla-v%C3%A5ra-ligg/id1152876845?mt=2"
        },{
            "artistName":"Carl Fridsjö", "id":"1213330824", "releaseDate":"2018-06-10", "contentAdvisoryRating":
            "Explicit", "name":"En Mörk Historia", "kind":"podcast", "artworkUrl100":
            "https://is2-ssl.mzstatic.com/image/thumb/Music115/v4/94/a2/12/94a212b0-cec4-348a-7486-0433203ce37f/source/200x200bb.png", "genres":[
            {
                "genreId":"1311", "name":"Nyheter och politik", "url":"https://itunes.apple.com/se/genre/id1311"
            },{
                "genreId":"26", "name":"Podcaster", "url":"https://itunes.apple.com/se/genre/id26"
            },{
                "genreId":"1314", "name":"Religion och andlighet", "url":"https://itunes.apple.com/se/genre/id1314"
            },{
                "genreId":"1324", "name":"Samhälle och kultur", "url":"https://itunes.apple.com/se/genre/id1324"
            }],"url":"https://itunes.apple.com/se/podcast/en-m%C3%B6rk-historia/id1213330824?mt=2"
        }]}
    }*/

}