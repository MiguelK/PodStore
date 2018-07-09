package com.podcastcatalog.util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.podcastcatalog.service.job.CreateLinkPages;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by miguelkrantz on 2018-07-08.
 */
public class DynamicLinkIndex {

    private final Gson GSON = new Gson();

    private final static Logger LOG = Logger.getLogger(DynamicLinkIndex.class.getName());

    private Index index;
    public DynamicLinkIndex() {
        index = new Index();
    }

    public void addLink(String key, String url){
        if(StringUtils.isEmpty(key) || StringUtils.isEmpty(url)){
            throw new IllegalArgumentException();
        }
        index.put(key, url);
    }

    public void saveTo(File file){

        LOG.info("Saving dynamicLink index from file " + file.getAbsolutePath() + "links=" + index.keyValues.values().size());

        try {
            try (Writer writer = new FileWriter(file)) {
                System.out.println("Writing JSON=" + file.getAbsolutePath());
              //  Gson gson = new GsonBuilder().create();
                //  gson.toJson("Hello", writer);
                // gson.toJson(123, writer);
                GSON.toJson(index,writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFrom(File file){

        if(!file.exists() || !file.canRead()){
            return;
        }
       // GSON.fromJson(new InputStreamReader(new ))
        // Map map = GSON.fromJson(content1.toString(), Map.class);

        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
            this.index = GSON.fromJson(reader, Index.class);
            LOG.info("Loaded dynamicLink index from file " + file.getAbsolutePath() + ", size=" + index.keyValues.values().size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class Index {
        private Map<String,String> keyValues = new HashMap<>();

        void put(String key, String value){
            keyValues.put(key,value);
        }
    }
}
