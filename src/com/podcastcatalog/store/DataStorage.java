package com.podcastcatalog.store;

import com.google.gson.Gson;
import com.podcastcatalog.api.response.PodCastCatalog;
import com.podcastcatalog.api.response.PodCastCatalogLanguage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang.math.NumberUtils;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class DataStorage {
    private final static Logger LOG = Logger.getLogger(DataStorage.class.getName());

    private static final Gson GSON = new Gson();

    private final File rootDir;

    public DataStorage(File dataDirectory) {
        if (dataDirectory == null) {
            throw new IllegalArgumentException("dataDirectory is null");
        }
        if (!dataDirectory.isDirectory()) {
            throw new IllegalArgumentException("dataDirectory is not a dir " + dataDirectory.getAbsolutePath());
        }

        this.rootDir = new File(dataDirectory, "PodCastCatalogVersions");

        initRoot();
    }

    public DataStorage() {
        this(new HomeDirectoryLocator().locateDataDir());
    }

    private void initRoot() {
        if (!this.rootDir.exists()) {
            this.rootDir.mkdirs();
        }
    }

    private List<File> getVersionDirectories() {
        List<File> files = new ArrayList<>();

        File[] subdirs = rootDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
        if (subdirs == null) {
            return Collections.emptyList();
        }

        IntStream intStream = Arrays.stream(subdirs).filter(this::isVersionDirectory).mapToInt(m ->
                NumberUtils.toInt(m.getName()));//.sorted().toArray();

        List<Integer> v = intStream
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        Collections.reverse(v);

        for (Integer version : v) {
            File file = new File(rootDir, String.valueOf(version));
            files.add(file);
        }


        return files;
    }

    private File getLatestVersionDirectory() {
        List<File> versionDirectories = getVersionDirectories();

        if (versionDirectories.isEmpty()) {
            return null;
        }

        return versionDirectories.get(0);
    }

    private PodCastCatalogVersion createNewVersionDirectory() {
        File[] subdirs = rootDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);

        int nextVersionNumber = 1;
        if (subdirs != null) {

            OptionalInt max = Arrays.stream(subdirs).filter(this::isVersionDirectory).mapToInt(m ->
                    NumberUtils.toInt(m.getName())).max();

            if (max.isPresent()) {
                nextVersionNumber = max.getAsInt() + 1;
            }
        }

        File file = new File(rootDir, String.valueOf(nextVersionNumber));
        file.mkdirs();//FIXME

        PodCastCatalogVersion podCastCatalogVersion = PodCastCatalogVersion.create(file);

        return podCastCatalogVersion;
    }

    private boolean isVersionDirectory(File e) {
        int i = NumberUtils.toInt(e.getName(), -1);
        return i != -1;
    }

    private File getFileName(PodCastCatalogLanguage podCastCatalogLanguage) {
        String fileName = podCastCatalogLanguage.name() + ".dat";
        return new File(rootDir, fileName);
    }

  /*  public Optional<PodCastCatalog> load(PodCastCatalogLanguage podCastCatalogLanguage) {

        File file = getFileName(podCastCatalogLanguage);

        if (!file.exists() || !file.canRead()) {
            return Optional.empty();
        }

        ObjectInputStream in = null;
        FileInputStream fileIn = null;
        try {
            try {
                fileIn = new FileInputStream(file);
                in = new ObjectInputStream(fileIn);
                return Optional.of((PodCastCatalog) in.readObject());
            } catch (IOException | ClassNotFoundException e) {
                LOG.log(Level.SEVERE, "Unable to load PodCastCatalog=" + file.getAbsolutePath(), e);
            }

        } finally {
            if (in != null) {
                IOUtils.closeQuietly(in);
            }
            if (fileIn != null) {
                IOUtils.closeQuietly(fileIn);
            }
        }

        return Optional.empty();
    }
*/
    public void deleteAll() {
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            return;
        }

        try {
            FileUtils.deleteDirectory(rootDir);
        } catch (IOException e) {
            e.printStackTrace();//FIXME
        }
    }

    public void save(PodCastCatalog podCastCatalog) {
        PodCastCatalogVersion versionDirectory = createNewVersionDirectory();

        saveAsObject(podCastCatalog, versionDirectory);
        saveAsJSON(podCastCatalog, versionDirectory);
        zipJSONFile();
    }

    private void zipJSONFile() {
        LOG.warning("FIXME zip json file... TODO");//FIXME

    }

   /* private static File createFile(File root, String fileName) {
        File file = new File(root, fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;

    }*/

    private void saveAsObject(PodCastCatalog podCastCatalog, PodCastCatalogVersion versionDirectory) {
//        File file = createFile(versionDirectory, podCastCatalog.getPodCastCatalogLanguage() + ".dat");//FIXME

        FileOutputStream fileOut = null;
        ObjectOutputStream out = null;
        try {
            fileOut =
                    new FileOutputStream(versionDirectory.getSweDat());
            out = new ObjectOutputStream(fileOut);
            out.writeObject(podCastCatalog);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.log(Level.SEVERE, "Unable to save PodCastCatalog " + versionDirectory.getSweDat().getAbsolutePath(), e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(fileOut);
        }
    }

    private void saveAsJSON(PodCastCatalog podCastCatalog, PodCastCatalogVersion versionDirectory) {

        try {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(versionDirectory.getSweJSON()), "UTF-8")) {
                GSON.toJson(podCastCatalog, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File get() {
        File zipFile = new File(rootDir, "Sweden.json.zip");
        return zipFile;
    }

    public Optional<PodCastCatalogVersion> getCurrentVersion() {
        List<PodCastCatalogVersion> allVersions = getAllVersions();

        if(allVersions.isEmpty()){
            return Optional.empty();
        }

        return Optional.ofNullable(allVersions.get(0));
    }

    public List<PodCastCatalogVersion> getAllVersions() {

        List<PodCastCatalogVersion> allVersions = new ArrayList<>();
        List<File> latestVersionDirectories = getVersionDirectories();

        for (File versionRoot : latestVersionDirectories) {
            allVersions.add(PodCastCatalogVersion.load(versionRoot));
        }

        return allVersions;
    }


    public static class PodCastCatalogVersion {
        private int version;
        private File sweJSON;
        private File sweJSONZipped;
        private File sweDat;

        private PodCastCatalogVersion(File versionRoot) {
            sweDat = new File(versionRoot, PodCastCatalogLanguage.Sweden.name() + ".dat");
            sweJSON = new File(versionRoot, PodCastCatalogLanguage.Sweden.name() + ".json");
            sweJSONZipped = new File(versionRoot, PodCastCatalogLanguage.Sweden.name() + "_json.zip");

            make(versionRoot);
        }

        private void make(File versionRoot) {
            try {
                version = Integer.parseInt(versionRoot.getName());
                sweDat.createNewFile();
                sweJSON.createNewFile();
                sweJSONZipped.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static PodCastCatalogVersion create(File versionRoot){
            PodCastCatalogVersion podCastCatalogVersion = new PodCastCatalogVersion(versionRoot);
            podCastCatalogVersion.make(versionRoot);
            return podCastCatalogVersion;
        }

        public static PodCastCatalogVersion load(File versionRoot){


            PodCastCatalogVersion podCastCatalogVersion = new PodCastCatalogVersion(versionRoot);

            podCastCatalogVersion.loadV();

            return podCastCatalogVersion;
        }

        private PodCastCatalog podCastCatalog;

        private void loadV() {
//            public Optional<PodCastCatalog> load(PodCastCatalogLanguage podCastCatalogLanguage) {

//                File file = getFileName(podCastCatalogLanguage);

               /* if (!file.exists() || !file.canRead()) {
                    return Optional.empty();
                }*/

                ObjectInputStream in = null;
                FileInputStream fileIn = null;
                try {
                    try {
                        fileIn = new FileInputStream(sweDat);
                        in = new ObjectInputStream(fileIn);
                        podCastCatalog = ((PodCastCatalog) in.readObject());
                    } catch (IOException | ClassNotFoundException e) {
                        LOG.log(Level.SEVERE, "Unable to load PodCastCatalog=" + sweDat.getAbsolutePath(), e);
                    }

                } finally {
                    if (in != null) {
                        IOUtils.closeQuietly(in);
                    }
                    if (fileIn != null) {
                        IOUtils.closeQuietly(fileIn);
                    }
                }

//                return Optional.empty();
//            }

        }

        public File getSweJSON() {
            return sweJSON;
        }

        public File getSweJSONZipped() {
            return sweJSONZipped;
        }

        public File getSweDat() {
            return sweDat;
        }

        public int getVersion() {
            return version;
        }

        public PodCastCatalog getPodCastCatalogSwedish() {
            return podCastCatalog;
        }
    }

}
