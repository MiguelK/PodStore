package com.livebundles;

import com.google.gson.Gson;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalog;
import com.podcastcatalog.model.podcastsearch.PodCastResultItem;
import com.podcastcatalog.model.podcastsearch.ResultType;
import com.podcastcatalog.service.datastore.PodCastCatalogVersion;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.podcastcatalog.TestUtil.GSON;

public class LiveBundleGenerator {


    private static String imageHost = "http://pods.one/liveBudles/images/";

    private final Gson GSON = new Gson();

    private static String defaultPushBodyText = "Lyssna här...";

    private String sweIdPrefx = "swe_";

    private String engIdPrefx = "eng_";


    @Test
    public void featuredPodCast_Swe() {

        FeaturedPodCastModel model = new FeaturedPodCastModel();
        model.version = 5;


        model.add(new PodCastModel("https://static-cdn.sr.se/sida/images/909/77f00286-6cbd-4407-b95b-202ebc736e27.jpg?preset=socialmedia-share-image",
                "http://sverigesradio.se/sida/artikel.aspx?programid=909&artikel=2462939","302534426"));


        model.add(new PodCastModel("http://is5.mzstatic.com/image/thumb/Music71/v4/c4/f8/91/c4f891d5-9df5-03a4-55af-1f1687d8c327/source/1200x630bb.jpg",
                "https://www.breakit.se/podcast/163/aer-det-en-bra-affar-for-skattebetalarna-att-satsa-pa-startups","962159150"));

        model.add(new PodCastModel("https://imgs.aftonbladet-cdn.se/v2/images/1a48f0e9-08e7-4158-ad95-b21dd14a0fbc?fit=crop&h=732&w=1100&s=b5104d3f210dbe3ede95f7e909a62e5d29f0d647",
                "https://www.aftonbladet.se/nojesbladet/a/4p4rR/mia-skaringer-blev-sjukskriven--under-foraldraledigheten","1229724132"));
        model.add(new PodCastModel("http://podcasts.nu/images/podcasts/5/sr-p3-dokumentar-standard.jpg","http://sverigesradio.se/sida/avsnitt/1012742?programid=2519","308339623"));

        model.add(new PodCastModel("https://podtail.com/content/images/podcast/artwork/600/a/l/alice-bianca-har-du-sagt-a-far-du-saga-b.jpg","https://www.expressen.se/noje/bianca-ingrossos-ursakt-efter-hatet/","1316796982"));
        model.add(new PodCastModel("http://is5.mzstatic.com/image/thumb/Music118/v4/22/ca/68/22ca6812-2fec-f4c8-b6ab-a2b16f3f108f/source/170x170bb.jpg",
                "http://fallet.aftonbladet.se","979901114"));

        model.add(new PodCastModel("http://is3.mzstatic.com/image/thumb/Music128/v4/09/29/8f/09298f1e-331b-4a17-0c58-63a72736d559/source/1200x630bb.jpg",
                "https://www.expressen.se/noje/podcast/livshjulet/","645681893"));

        model.add(new PodCastModel("https://brapodcast.se/cache/img/1305009993.jpg",
                "http://iamready.se/livsstil/kan-sjalv-en-podcast-om-manskligt-beteende-negativa-spiraler-och-positiv-tankeprogrammering-med-marten-nylen/",
                "1305009993"));

        model.add(makePodCastModel("http://is5.mzstatic.com/image/thumb/Music128/v4/a4/ac/38/a4ac3836-feb5-90ba-bb27-73c2b847a306/source/170x170bb.jpg",
                "http://www.lofsan.se/traningspodden-med-jessica-och-lofsan/","1032687266"));

        model.add(makePodCastModel("http://is5.mzstatic.com/image/thumb/Music62/v4/c9/86/3b/c9863bdc-3f2f-c008-30da-ecaf3b2d53c6/source/170x170bb.jpg",
                "https://www.breakit.se/artikel/3262/sa-mycket-omsatter-borspodden","702777888"));

        model.add(makePodCastModel("http://is4.mzstatic.com/image/thumb/Music118/v4/3c/59/8c/3c598c80-205c-67d8-95e8-59dd1480250b/source/170x170bb.jpg",
                "https://alexandra.elle.se/allt-ar-bra-med-anja-och-alex/","1330922969"));

        model.add(makePodCastModel("http://is5.mzstatic.com/image/thumb/Music71/v4/9d/05/1f/9d051fe5-59f2-723f-3485-a1e03a021b0c/source/170x170bb.jpg",
                "http://www.su.se/om-oss/evenemang/bildningspodden-en-podcast-för-vetgiriga-1.254430","1055815193"));
        model.add(makePodCastModel("http://is3.mzstatic.com/image/thumb/Music118/v4/8c/82/16/8c8216c2-3615-ecae-47b1-a7002833c881/source/170x170bb.jpg",
                "https://www.expressen.se/noje/william-spetz-aktuell-i-humorprogram-pa-p3/","1073050778"));
        model.add(makePodCastModel("http://is5.mzstatic.com/image/thumb/Music118/v4/bc/94/ce/bc94cec1-b0df-e924-107c-7034fa3b9136/source/170x170bb.jpg",
                "https://www.expressen.se/omtalat/sex-och-relationer/7-tips-sa-kommer-du-over-personen-som-dumpat-dig/","874008884"));
        model.add(makePodCastModel("http://is4.mzstatic.com/image/thumb/Music128/v4/32/b7/4d/32b74dd5-9524-0356-d59c-699dc756644f/source/170x170bb.jpg",
                "https://www.svt.se/kultur/medier/popular-podd-om-usa-valet-fortsatter","1080924446"));
        model.add(makePodCastModel("http://is4.mzstatic.com/image/thumb/Music118/v4/48/f9/ad/48f9ad29-089a-4261-0389-ee006eb7a562/source/170x170bb.jpg",
                "http://poddtoppen.fi/2016/12/veckans-poddtips-david-batras-podcast/","1151906794"));
        model.add(makePodCastModel("http://is3.mzstatic.com/image/thumb/Music71/v4/42/7b/67/427b67be-aa0a-a65e-add9-4615eaf06778/source/170x170bb.jpg",
                "http://kodsnack.se","561631498"));
        model.add(makePodCastModel("http://is1.mzstatic.com/image/thumb/Music111/v4/02/cc/15/02cc1579-6f12-ec74-2c33-ddf6b8ced2aa/source/170x170bb.jpg",
                "https://www.nyteknik.se/iot-podden/ny-teknik-startar-podd-om-internet-of-things-6832229","1215903822"));

        //Collections.reverse(model.podCastModels); //Latest will alway be first

        /*  model.add(makePodCastModel("","",""));
        model.add(makePodCastModel("","",""));
        model.add(makePodCastModel("","",""));
        model.add(makePodCastModel("","",""));
        model.add(makePodCastModel("","",""));
        model.add(makePodCastModel("","",""));
        model.add(makePodCastModel("","",""));
        model.add(makePodCastModel("","",""));
        model.add(makePodCastModel("","",""));
        model.add(makePodCastModel("","",""));
*/

        validate(model);
        saveAsJSON(model, "swe-featured.json");
    }

    @Test
    public void featuredPodCast_Eng() {

        FeaturedPodCastModel model = new FeaturedPodCastModel();
        model.version = 2;

        model.add(new PodCastModel(
                "https://pbs.twimg.com/profile_images/841467361119096833/HyCyoKBd.jpg",
                "https://stownpodcast.org","1212558767"));
        model.add(new PodCastModel("http://www.indiewire.com/wp-content/uploads/2017/07/levar-burton-reads.jpeg?w=780",
                "http://www.indiewire.com/2017/07/neil-gaiman-levar-burton-reads-podcast-1201860114/","1244649384"));
        model.add(new PodCastModel("https://secureimg.stitcher.com/feedimagesplain328/54050.jpg","https://www.rollingstone.com/culture/lists/beyond-serial-10-true-crime-podcasts-you-need-to-follow-w429955","917918570"));
        model.add(makePodCastModel("http://is4.mzstatic.com/image/thumb/Music127/v4/d0/e6/5f/d0e65f81-c2cf-7f59-38e4-6abcfab7e38a/source/170x170bb.jpg",
                "https://en.wikipedia.org/wiki/The_Joe_Rogan_Experience","360084272"));
        model.add(makePodCastModel("http://is4.mzstatic.com/image/thumb/Music128/v4/d9/ba/3a/d9ba3a5c-b57e-e767-0923-06f08ad0b401/source/170x170bb.jpg",
                "https://www.indystar.com/story/sports/nfl/colts/2017/08/23/pat-mcafee-opens-up-life-after-football/588204001/","1134713453"));
        model.add(makePodCastModel("http://is4.mzstatic.com/image/thumb/Music128/v4/3f/77/53/3f77530f-9c97-214e-141f-28366deb715d/source/170x170bb.jpg",
                "https://www.eatingforfree.com","1332978675"));
        model.add(makePodCastModel("http://is4.mzstatic.com/image/thumb/Music118/v4/ac/26/71/ac267185-232d-c64e-c57d-bbdc25f7ab83/source/170x170bb.jpg",
                "http://allyssaarens.oucreate.com/music/podcast-recommendation-by-turned-up-by-jake-jones-and-robert-venable/","1273576074"));
        model.add(makePodCastModel("http://is1.mzstatic.com/image/thumb/Music71/v4/a6/2d/e5/a62de5e5-947a-dfa5-433f-82f3a53eaa89/source/170x170bb.jpg",
                "https://www.nytimes.com/column/popcast-pop-music-podcast","120315823"));
        model.add(makePodCastModel("http://is5.mzstatic.com/image/thumb/Music62/v4/b7/e4/67/b7e46778-c171-9c44-ba31-cf42705467c7/source/170x170bb.jpg",
                "http://awfulannouncing.com/online-outlets/pardon-take-took-sports-podcasting.html","1089022756"));
        model.add(makePodCastModel("http://is2.mzstatic.com/image/thumb/Music71/v4/ec/3f/92/ec3f924d-e918-086c-777e-cc64e1237984/source/170x170bb.jpg",
                "https://en.wikipedia.org/wiki/Monday_Morning_Podcast","480486345"));
        model.add(makePodCastModel("http://is4.mzstatic.com/image/thumb/Music71/v4/2b/fd/92/2bfd9226-b330-491a-86cc-02624d253901/source/170x170bb.jpg",
                "https://en.wikipedia.org/wiki/WTF_with_Marc_Maron","329875043"));
        model.add(makePodCastModel("http://is4.mzstatic.com/image/thumb/Music128/v4/7e/c9/12/7ec9120c-4fdd-c0a7-eb8c-787bfb6e21df/source/170x170bb.jpg",
                "https://en.wikipedia.org/wiki/Juliet_Litman","1330224581"));
        model.add(makePodCastModel("http://is3.mzstatic.com/image/thumb/Music111/v4/15/09/d9/1509d9f7-9ad2-3f02-41e6-effa3dcc81f8/source/170x170bb.jpg",
                "https://lifehacker.com/tag/the-upgrade","508117781"));
        model.add(makePodCastModel("http://is5.mzstatic.com/image/thumb/Music19/v4/b5/f3/1e/b5f31eaa-a3d3-2c30-377f-cc74713cc07b/source/170x170bb.jpg",
                "https://www.theverge.com/podcasts","430333725"));
        model.add(makePodCastModel("http://is2.mzstatic.com/image/thumb/Music118/v4/6c/b6/ab/6cb6ab65-91d0-5a25-5199-9bae5bf2e89b/source/170x170bb.jpg",
                "https://www.npr.org/templates/story/story.php?storyId=5013","214089682"));
        model.add(makePodCastModel("http://is1.mzstatic.com/image/thumb/Music128/v4/c7/86/60/c7866006-f92c-3249-c406-8e7c3a7db877/source/170x170bb.jpg",
                "https://twitter.com/hashtag/tumanbay?lang=sv","1278345733"));
        model.add(makePodCastModel("http://is2.mzstatic.com/image/thumb/Music128/v4/fb/a0/a4/fba0a426-7293-ef1a-de0b-9c42b7a56c5a/source/170x170bb.jpg",
                "https://en.wikipedia.org/wiki/Critical_Role","1243705452"));

        validate(model);

        //model.add(makePodCastModel("","",""));
       // Collections.reverse(model.podCastModels); //Latest will alway be first


        saveAsJSON(model, "eng-featured.json");
    }

    private PodCastModel makePodCastModel(String imageUrl,String targetUrl,String pid) {
        return new PodCastModel(imageUrl,
                targetUrl,pid);
    }

    private void validate(FeaturedPodCastModel model)  {


        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<Future<?>> features = new ArrayList<>();

        for (PodCastModel podCastModel : model.podCastModels) {

            Future<?> submit = executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL imageUrl = new URL(podCastModel.imageUrl.replace("https","http"));
                        URLConnection conn = imageUrl.openConnection();
                        conn.connect();

                        URL targetUrl = new URL(podCastModel.targetUrl.replace("https","http"));
                        URLConnection targetUrlConn = targetUrl.openConnection();
                        targetUrlConn.connect();

                    } catch (Exception e) {
                        // the URL is not in a valid form
                        throw new RuntimeException(e);
                    }

                }
            });
            features.add(submit);
        }


        for (Future<?> feature : features) {
            try {
                feature.get(3, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
                throw  new RuntimeException(e);
            }
        }

        System.out.println("Done validating model " + model.podCastModels.size());
    }

    @Test
    public void podCast() {

        LiveBundles liveBundlesSWE = new LiveBundles();
        liveBundlesSWE.version = 2;
        liveBundlesSWE.addVirtualPodCast(make5_Kvinnliga_Idtrottare(sweIdPrefx + 1));
        liveBundlesSWE.addVirtualPodCast(make_musik_poddar(sweIdPrefx + 2));
        liveBundlesSWE.addVirtualPodCast(make_dokumentar_1(sweIdPrefx + 3));
        liveBundlesSWE.addVirtualPodCast(make_best_episodes_1(sweIdPrefx + 4)); //First episode on DiscoverVC2
        liveBundlesSWE.addVirtualPodCast(make_best_of_mordpodden(sweIdPrefx + 5));
        liveBundlesSWE.addVirtualPodCast(varvet(sweIdPrefx + 6));

        saveAsJSON(liveBundlesSWE, "swe-liveBundles.json");


        LiveBundles liveBundlesENG = new LiveBundles();
        liveBundlesENG.version = 1;
        liveBundlesENG.addVirtualPodCast(make_pupular_5(engIdPrefx + 1));
        saveAsJSON(liveBundlesENG, "eng-liveBundles.json");

        System.out.println("Done...");

        //PodCastResultItem item = new PodCastResultItem("1234","Title","image.jpg");

        //Assert.assertTrue(item.getResultType()== ResultType.PODCAST);
    }

    //SWE
    private VirtualPodCast varvet(String id) {
        List<Episode> episodes = new ArrayList<>();
        episodes.add(new Episode("Alexander Bard", "508310996"));
        episodes.add(new Episode("kjellman", "508310996"));
        episodes.add(new Episode("Joel Alme", "508310996"));
        episodes.add(new Episode("Love Antell", "508310996"));
        episodes.add(new Episode("Amanda Ooms Bard", "508310996"));
        episodes.add(new Episode("charlotte kalla", "508310996"));

        return new VirtualPodCast(id,"Värvet","Av Kristoffer Triumf",
                imageHost + "varvet.jpg", episodes);
    }
    private VirtualPodCast make_best_of_mordpodden(String id) {
        List<Episode> episodes = new ArrayList<>();
        episodes.add(new Episode("fotbollsmordet", "1107993389"));
        episodes.add(new Episode("styckm", "1107993389"));
        episodes.add(new Episode("elevmordet", "1107993389"));
        episodes.add(new Episode("orrefors", "1107993389"));
        episodes.add(new Episode("taggtrådsmordet", "1107993389"));
        episodes.add(new Episode("giftmordet", "1107993389"));

        return new VirtualPodCast(id,"Mordpodden",defaultPushBodyText,
                imageHost +"mordpodden.jpg", episodes);
    }


    private VirtualPodCast make_best_episodes_1(String id) {
        List<Episode> episodes = new ArrayList<>();
        episodes.add(new Episode("288-förbjuden", "534137041"));
        episodes.add(new Episode("fotbollsmordet", "1107993389"));
        episodes.add(new Episode("knarkkungarna", "308339623"));
        episodes.add(new Episode("11-att-fortsätta-leva", "1288509918"));
        episodes.add(new Episode("293-all-exclusive", "665802774"));
        episodes.add(new Episode("32-slicka-eller", "1229724132"));
        episodes.add(new Episode("143-jakten", "973949360"));
        episodes.add(new Episode("5-förlösamordet", "989645925"));
        episodes.add(new Episode("191-glassresan", "492695082"));


        return new VirtualPodCast(id,"Trending",defaultPushBodyText,imageHost + "stars.jpg", episodes);
    }
    private VirtualPodCast make_dokumentar_1(String id) {
        List<Episode> episodes = new ArrayList<>();
        episodes.add(new Episode("Vipeholmsexperimenten", "308339623"));
        episodes.add(new Episode("Fallet Linda", "308339623"));
        episodes.add(new Episode("Anders Svensson", "302534426"));
        episodes.add(new Episode("John Hron", "302534426"));
        episodes.add(new Episode("Elin Krantz", "1067686460"));
        episodes.add(new Episode("Fallet Melissa", "987229238"));

        return new VirtualPodCast(id,"Dokumentär",defaultPushBodyText,imageHost + "murder.jpg", episodes);
    }

    private VirtualPodCast make5_Kvinnliga_Idtrottare(String id) {
        List<Episode> episodes = new ArrayList<>();
        episodes.add(new Episode("Carolina Klüft", "284610981"));
        episodes.add(new Episode("Sarah Sjöström", "284610981"));
        episodes.add(new Episode("Lisa Dahlkvist", "1157968567"));
        episodes.add(new Episode("Elisabet Höglund", "1236563704"));   //id
        episodes.add(new Episode("Linnéa Claeson", "284610981"));   //id

        return new VirtualPodCast(id,"Idrottare",defaultPushBodyText,imageHost + "idrott-1.jpg", episodes);
    }

    private VirtualPodCast make_musik_poddar(String id) {
        List<Episode> episodes = new ArrayList<>();
        episodes.add(new Episode("Uggla", "391088995"));
        episodes.add(new Episode("Thåström", "1073552565"));
        episodes.add(new Episode("Maggio", "1049255623"));
        episodes.add(new Episode("Dahlgren", "1049255623"));
        episodes.add(new Episode("Anna", "305632534"));   //id

        return new VirtualPodCast(id,"Musik","Inspererande poddar om några svenska musiker",imageHost + "music-1.jpg", episodes);
    }

    //ENG
    private VirtualPodCast make_pupular_5(String id) {
        List<Episode> episodes = new ArrayList<>();
        episodes.add(new Episode("Have some crullers", "1192761536"));
        episodes.add(new Episode("Alec Baldwin", "1027264941"));
        episodes.add(new Episode("Harold", "201671138"));
        episodes.add(new Episode("Show 56", "173001861"));
        episodes.add(new Episode("John McEnroe", "342735925"));   //id

        return new VirtualPodCast(id,"Popular","Listen here...",imageHost + "tennis.jpg", episodes);
    }


    private void saveAsJSON(Object object, String fileName) {

        File file = new File("/Users/miguelkrantz/Documents/intellij-projects/PodStore/web-external/pods.nu/liveBundles/" + fileName);

        try {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file))) {
                GSON.toJson(object, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    class VirtualPodCast {

        String id;
        String title; //Grymma Sport poddar
        String pushBodyText; //5 poddar om kvinliga idrottare
        String imageUrl; //Optional

        List<Episode> episodes = new ArrayList<>();

        public VirtualPodCast(String id, String title, String pushBodyText, String imageUrl, List<Episode> episodes) {
            this.title = title;
            this.pushBodyText = pushBodyText;
            this.imageUrl = imageUrl;
            this.episodes = episodes;
            this.id = id;
        }
    }

    class Episode {
        String podCastEpisodeId;
        String podCastCollectionId;

        public Episode(String podCastEpisodeId, String podCastCollectionId) {
            this.podCastEpisodeId = podCastEpisodeId;
            this.podCastCollectionId = podCastCollectionId;
        }
    }


    public class PodCastModel {
        String imageUrl;
        String targetUrl;
        String podCastCollectionId;
        String podacstEpisodeTargetURL;

        public PodCastModel(String imageUrl, String targetUrl, String podCastCollectionId) {
            this.imageUrl = imageUrl;
            this.targetUrl = targetUrl;
            this.podCastCollectionId = podCastCollectionId;
        }

        public PodCastModel(String imageUrl, String targetUrl, String podCastCollectionId, String podacstEpisodeTargetURL) {
            this.imageUrl = imageUrl;
            this.targetUrl = targetUrl;
            this.podCastCollectionId = podCastCollectionId;
            this.podacstEpisodeTargetURL = podacstEpisodeTargetURL;
        }
    }

    public class FeaturedPodCastModel {
        List<PodCastModel> podCastModels = new ArrayList<>();
        int version;

        void add(PodCastModel model){
            podCastModels.add(model);
        }
    }

    public class LiveBundles {

        int version;

        List<VirtualPodCast> virtualPodCasts = new ArrayList<>(); //Samling episoder i en virtual pod "Veckans bästa avsnitt"

        //List<VirtualCategory> virtualCategories = new ArrayList<>(); //e.g Samling med poddar "True Crime poddar"

        void addVirtualPodCast(VirtualPodCast virtualPodCast) {
            virtualPodCasts.add(virtualPodCast);

            //List<String> al = virtualPodCast.episodes.stream().
            //      map(e-> e.podCastCollectionId).collect(Collectors.toList());

        }
    }


}
