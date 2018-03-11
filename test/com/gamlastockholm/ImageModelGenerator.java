package com.gamlastockholm;

import com.google.gson.Gson;
import com.livebundles.LiveBundleGenerator;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ImageModelGenerator {

    private static String imageHost = "http://pods.one/liveBudles/images/";

    private final Gson GSON = new Gson();

    private Tag sodermalm = new Tag(1,"Södermalm");

    private Tag vasastan = new Tag(2,"Vasastan");
    private Tag normalm = new Tag(3,"Normalm");

    @Test
    public void createModel() {

        ImageCatalogModel model = new ImageCatalogModel();
        model.version = 2;

        model.add(new ImageModel("Vitabergsparken","59.307438,18.083352",
                "Vitabergsparken1_2176x1392.png", Arrays.asList(sodermalm)));

        model.add(new ImageModel("Hornsgatan","59.307438,18.083352",
                "Hornsgatan3_176x1469.png", Arrays.asList(sodermalm)));

        model.add(new ImageModel("Hornsgatan","59.307438,18.083352",
                "Hornsgatan2_2176x1397.png", Arrays.asList(sodermalm)));


        model.add(new ImageModel("Hammarbyleden","59.307438,18.083352",
                "Hammarbyleden1_2176x1398.png", Arrays.asList(sodermalm)));




        model.add(new ImageModel("Bragevägen","59.307438,18.083352",
                "Bragevagen_2176x1412.png", Arrays.asList(vasastan)));


        model.add(new ImageModel("Arsenalgatan","59.307438,18.083352",
                "Arsenalgatan1_2176x1372.png", Arrays.asList(normalm)));

        /*model.add(new ImageModel("Vitabergsparken1_2176x1392.png", Arrays.asList(sodermalm) ));

        model.add(new ImageModel("Hornsgatan3_176x1469.png", Arrays.asList(sodermalm) ));
        model.add(new ImageModel("Gotgatan2_2176x1389.png", Arrays.asList(sodermalm) ));


        model.add(new ImageModel("Bragevagen_2176x1412.png", Arrays.asList(vasastan) ));
        model.add(new ImageModel("Kungsgatan_2176x1373.png", Arrays.asList(vasastan) ));


        model.add(new ImageModel("Arsenalgatan1_2176x1372.png.png", Arrays.asList(normalm) ));
        */



        saveAsJSON(model, "ImageCatalogModel.json");
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

    public class ImageCatalogModel {
        private List<ImageModel> images = new ArrayList<>();
        List<Tag> allTags = new ArrayList<>();
        int version;

        void add(ImageModel model){
            images.add(model);
            allTags.addAll(model.tags);
        }
    }

    public class Tag {
        int id;
        String name;

        public Tag(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tag tag = (Tag) o;
            return id == tag.id;
        }

        @Override
        public int hashCode() {

            return Objects.hash(id);
        }
    }

    public class ImageModel {
        List<Tag> tags;
        String imageName;
        String streetName;
        String streetViewCenter; //59.307438,18.083352


        public ImageModel(String streetName, String streetViewCenter, String imageName, List<Tag> tags) {
            this.imageName = imageName;
            this.tags = tags;
            this.streetName =streetName;
            this.streetViewCenter = streetViewCenter;

        }

        void add(Tag model){
            tags.add(model);
        }
    }
}
