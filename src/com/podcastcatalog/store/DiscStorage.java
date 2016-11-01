package com.podcastcatalog.store;

import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Optional;
import java.util.logging.Logger;

public class DiscStorage implements Storage {

    private final static Logger LOG = Logger.getLogger(DiscStorage.class.getName());

    private final File dataDirectory;

    public  DiscStorage(File dataDirectory) {
        if(dataDirectory==null){
            throw new IllegalArgumentException("dataDirectory is null");
        }
        if(!dataDirectory.isDirectory()){
            throw new IllegalArgumentException("dataDirectory is not a dir " + dataDirectory.getAbsolutePath());
        }

        this.dataDirectory = dataDirectory;
    }

    private String getFileName(PodCastCatalogLanguage podCastCatalogLanguage) {
        return podCastCatalogLanguage.name() + ".dat";
    }

    @Override
    public Optional<PodCastCatalog> load(PodCastCatalogLanguage podCastCatalogLanguage) {

        String fileName = getFileName(podCastCatalogLanguage);

        File file = getFile(fileName);

        ObjectInputStream in = null;
        FileInputStream fileIn=null;
        try {
            try {
                fileIn = new FileInputStream(file);
                in = new ObjectInputStream(fileIn);
                return Optional.of((PodCastCatalog) in.readObject());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }finally {
            if(in!=null){
              IOUtils.closeQuietly(in);
            }
            if(fileIn!=null){
                IOUtils.closeQuietly(fileIn);
            }
        }

        return Optional.empty();
    }

    private File getFile(String fileName) {
        File file = new File(dataDirectory, fileName);

        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();//FIXME
            }
        }

        return file;
    }

    @Override
    public void delete(PodCastCatalogLanguage podCastCatalogLanguage) {
        String fileName = getFileName(podCastCatalogLanguage);

        File file = getFile(fileName);

        file.delete(); //FIXME
    }

  /*  @Override
    public void delete(String fileName) {
        File file = getFile(fileName);*/

   /*     {
            String assetName = getFileName(podCastCatalogLanguage);

            try {
                storageStrategy.delete(assetName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }*/

   /*     file.delete(); //FIXME
    }*/


    @Override
    public void save(PodCastCatalog podCastCatalog)  {
        String fileName = getFileName(podCastCatalog.getPodCastCatalogLanguage());

        File file = getFile(fileName);

//        File file = getFile(fileName);

        FileOutputStream fileOut = null;
        ObjectOutputStream out = null;
        try{
            fileOut =
                    new FileOutputStream(file);
            out = new ObjectOutputStream(fileOut);
            out.writeObject(podCastCatalog);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(fileOut);
        }

        LOG.info("Saved " + podCastCatalog.getPodCastCatalogLanguage() + " to " + file.getAbsolutePath());
    }


}
