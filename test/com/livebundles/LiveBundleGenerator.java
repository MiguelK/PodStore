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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
        model.version = 1;
        model.add(new PodCastModel("https://podtail.com/content/images/podcast/artwork/600/a/l/alice-bianca-har-du-sagt-a-far-du-saga-b.jpg","https://www.expressen.se/noje/bianca-ingrossos-ursakt-efter-hatet/","1316796982"));

        saveAsJSON(model, "swe-featured.json");
    }

    @Test
    public void featuredPodCast_Eng() {

        FeaturedPodCastModel model = new FeaturedPodCastModel();
        model.version = 2;

        model.add(new PodCastModel("https://secureimg.stitcher.com/feedimagesplain328/54050.jpg","https://www.rollingstone.com/culture/lists/beyond-serial-10-true-crime-podcasts-you-need-to-follow-w429955","917918570"));

        saveAsJSON(model, "eng-featured.json");
    }


        @Test
    public void podCast() {

        LiveBundles liveBundlesSWE = new LiveBundles();
        liveBundlesSWE.addVirtualPodCast(make5_Kvinnliga_Idtrottare(sweIdPrefx + 1));
        liveBundlesSWE.addVirtualPodCast(make_musik_poddar(sweIdPrefx + 2));
        liveBundlesSWE.addVirtualPodCast(make_dokumentar_1(sweIdPrefx + 3));
        liveBundlesSWE.addVirtualPodCast(make_best_episodes_1(sweIdPrefx + 4));
        liveBundlesSWE.addVirtualPodCast(make_best_of_mordpodden(sweIdPrefx + 5));
        liveBundlesSWE.addVirtualPodCast(varvet(sweIdPrefx + 6));

        saveAsJSON(liveBundlesSWE, "swe-liveBundles.json");


        LiveBundles liveBundlesENG = new LiveBundles();
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

        return new VirtualPodCast(id,"Det bästa från Värvet.","Av Kristoffer Triumf",
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

        return new VirtualPodCast(id,"Det bästa från Mordpodden.",defaultPushBodyText,
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


        return new VirtualPodCast(id,"Bästa avsnitten.",defaultPushBodyText,imageHost + "stars.jpg", episodes);
    }
    private VirtualPodCast make_dokumentar_1(String id) {
        List<Episode> episodes = new ArrayList<>();
        episodes.add(new Episode("Vipeholmsexperimenten", "308339623"));
        episodes.add(new Episode("Fallet Linda", "308339623"));
        episodes.add(new Episode("Anders Svensson", "302534426"));
        episodes.add(new Episode("John Hron", "302534426"));
        episodes.add(new Episode("Elin Krantz", "1067686460"));
        episodes.add(new Episode("Fallet Melissa", "987229238"));

        return new VirtualPodCast(id,"Dokumentärtips.",defaultPushBodyText,imageHost + "murder.jpg", episodes);
    }

    private VirtualPodCast make5_Kvinnliga_Idtrottare(String id) {
        List<Episode> episodes = new ArrayList<>();
        episodes.add(new Episode("Carolina Klüft", "284610981"));
        episodes.add(new Episode("Sarah Sjöström", "284610981"));
        episodes.add(new Episode("Lisa Dahlkvist", "1157968567"));
        episodes.add(new Episode("Elisabet Höglund", "1236563704"));   //id
        episodes.add(new Episode("Linnéa Claeson", "284610981"));   //id

        return new VirtualPodCast(id,"Poddar om kvinnliga idrottare",defaultPushBodyText,imageHost + "idrott-1.jpg", episodes);
    }

    private VirtualPodCast make_musik_poddar(String id) {
        List<Episode> episodes = new ArrayList<>();
        episodes.add(new Episode("Uggla", "391088995"));
        episodes.add(new Episode("Thåström", "1073552565"));
        episodes.add(new Episode("Maggio", "1049255623"));
        episodes.add(new Episode("Dahlgren", "1049255623"));
        episodes.add(new Episode("Anna", "305632534"));   //id

        return new VirtualPodCast(id,"Musik Musik","Inspererande poddar om några svenska musiker",imageHost + "music-1.jpg", episodes);
    }

    //ENG
    private VirtualPodCast make_pupular_5(String id) {
        List<Episode> episodes = new ArrayList<>();
        episodes.add(new Episode("Have some crullers", "1192761536"));
        episodes.add(new Episode("Alec Baldwin", "1027264941"));
        episodes.add(new Episode("Harold", "201671138"));
        episodes.add(new Episode("Show 56", "173001861"));
        episodes.add(new Episode("John McEnroe", "342735925"));   //id

        return new VirtualPodCast(id,"Popular podcasts","Listen here...",imageHost + "tennis.jpg", episodes);
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


        List<VirtualPodCast> virtualPodCasts = new ArrayList<>(); //Samling episoder i en virtual pod "Veckans bästa avsnitt"

        //List<VirtualCategory> virtualCategories = new ArrayList<>(); //e.g Samling med poddar "True Crime poddar"

        void addVirtualPodCast(VirtualPodCast virtualPodCast) {
            virtualPodCasts.add(virtualPodCast);

            //List<String> al = virtualPodCast.episodes.stream().
            //      map(e-> e.podCastCollectionId).collect(Collectors.toList());

        }
    }


}
