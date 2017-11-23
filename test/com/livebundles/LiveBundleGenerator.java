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


    private final Gson GSON = new Gson();

    @Test
    public void podCast() {

        LiveBundles liveBundles = new LiveBundles();

        List<Episode> episodes = new ArrayList<>();
        episodes.add(new Episode("Carolina Klüft", "284610981"));   //i och id
        episodes.add(new Episode("Sarah Sjöström", "284610981"));   //i och id
        episodes.add(new Episode("Lisa Dahlkvist", "1157968567"));   //i och id



        String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Olympic_pictogram_Athletics_-_colored.svg/2000px-Olympic_pictogram_Athletics_-_colored.svg.png";
        VirtualPodCast v1 = new VirtualPodCast("Poddar om kvinnliga idrottare","5 poddar om svenska idrottare",imageUrl, episodes);


        liveBundles.addVirtualPodCast(v1);


        saveAsJSON(liveBundles);



        System.out.println("gdhsdghsg");

        //PodCastResultItem item = new PodCastResultItem("1234","Title","image.jpg");

        //Assert.assertTrue(item.getResultType()== ResultType.PODCAST);
    }


    private void saveAsJSON(LiveBundles liveBundles) {

        File file = new File("/Users/miguelkrantz/Documents/intellij-projects/PodStore/web-external/pods.nu/liveBundles/liveBundles.json");

        try {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file))) {
                GSON.toJson(liveBundles, writer);
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

        public VirtualPodCast(String title, String pushBodyText, String imageUrl, List<Episode> episodes) {
            this.title = title;
            this.pushBodyText = pushBodyText;
            this.imageUrl = imageUrl;
            this.episodes = episodes;
            this.id = UUID.randomUUID().toString();
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


    public class LiveBundles {

        List<VirtualPodCast> virtualPodCasts = new ArrayList<>(); //Samling episoder i en virtual pod "Veckans bästa avsnitt"

        //List<VirtualCategory> virtualCategories = new ArrayList<>(); //e.g Samling med poddar "True Crime poddar"

        void addVirtualPodCast(VirtualPodCast virtualPodCast) {
            virtualPodCasts.add(virtualPodCast);

            List<String> al = virtualPodCast.episodes.stream().
                    map(e-> e.podCastCollectionId).collect(Collectors.toList());

        }
    }


}
