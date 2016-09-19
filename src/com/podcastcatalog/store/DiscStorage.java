package com.podcastcatalog.store;

import com.podcastcatalog.api.response.PodCastCatalog;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;
import java.util.logging.Logger;

public class DiscStorage implements Storage {

    private static final String TEMP_FILE_PATH = System.getProperty("java.io.tmpdir");
    private static File DATA_DIRECTORY = new File(TEMP_FILE_PATH);
    private final static Logger LOG = Logger.getLogger(DiscStorage.class.getName());


    @Override
    public Optional<PodCastCatalog> load(String fileName) throws IOException, ClassNotFoundException {
        File file = getFile(fileName);

        ObjectInputStream in = null;
        FileInputStream fileIn=null;
        try {
             fileIn = new FileInputStream(file);
             in = new ObjectInputStream(fileIn);
            return Optional.of((PodCastCatalog) in.readObject());

        }finally {
            if(in!=null){
                in.close();
            }
            if(fileIn!=null){
                fileIn.close();
            }
        }
    }

    private File getFile(String fileName) {
        File file = new File(DATA_DIRECTORY, fileName);

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
    public void delete(String fileName) {
        File file = getFile(fileName);
        file.delete(); //FIXME
    }


    @Override
    public void save(String fileName, PodCastCatalog podCastCatalog) throws IOException {

        File file = getFile(fileName);

        FileOutputStream fileOut = null;
        ObjectOutputStream out = null;
        try{
            fileOut =
                    new FileOutputStream(file);
            out = new ObjectOutputStream(fileOut);
            out.writeObject(podCastCatalog);
        }finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(fileOut);
        }

        LOG.info("Saved " + podCastCatalog.getPodCastCatalogLanguage() + " to " + file.getAbsolutePath());
    }

    public static void setDataDirectory(File dataDirectory) {
        if(dataDirectory==null){
            throw new IllegalArgumentException("dataDirectory is null");
        }
        if(!dataDirectory.isDirectory()){
            throw new IllegalArgumentException("dataDirectory is not a dir " + dataDirectory.getAbsolutePath());
        }

        DATA_DIRECTORY = dataDirectory;
    }
}
