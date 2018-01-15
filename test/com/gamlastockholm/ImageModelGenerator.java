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

    @Test
    public void createModel() {

        ImageCatalogModel model = new ImageCatalogModel();
        model.version = 1;

        model.add(new ImageModel("Vitabergsparken1_2176x1392.png", Arrays.asList(sodermalm) ));
        model.add(new ImageModel("Bragevagen_2176x1412.png", Arrays.asList(vasastan) ));


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

        public ImageModel(String imageName, List<Tag> tags) {
            this.imageName = imageName;
            this.tags = tags;
        }

        void add(Tag model){
            tags.add(model);
        }
    }
}
