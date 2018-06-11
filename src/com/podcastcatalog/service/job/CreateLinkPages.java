package com.podcastcatalog.service.job;

import com.podcastcatalog.model.podcastcatalog.Bundle;
import com.podcastcatalog.model.podcastcatalog.BundleItem;
import com.podcastcatalog.model.podcastcatalog.BundleItemVisitor;
import com.podcastcatalog.model.podcastcatalog.BundleType;
import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalog;
import com.podcastcatalog.model.podcastcatalog.PodCastCatalogLanguage;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.service.datastore.ServiceDataStorage;
import com.podcastcatalog.service.podcastcatalog.PodCastCatalogService;
import com.podcastcatalog.util.ServerInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by miguelkrantz on 2018-06-09.
 */
public class CreateLinkPages implements Job {

    private final static Logger LOG = Logger.getLogger(CreateLinkPages.class.getName());

    private volatile boolean executedOnce = false;

    @Override
    public void doWork() {

        if(!ServerInfo.isLocalDevMode() || executedOnce ) {
            return;
        }

        String name =  "json.zip";
        File templateRoot = new File(ServerInfo.localPath, "web-external" + File.separator + "link-page-template");

        PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(PodCastCatalogLanguage.SE);

        if(podCastCatalog==null) {
            LOG.info("CreateLinkPages catalog not loaded yet" + templateRoot.getAbsolutePath());
            return;
        }

        executedOnce = true;

        LOG.info("CreateLinkPages " + templateRoot.getAbsolutePath());
        BundleItemVisitor bundleItemVisitor = new BundleItemVisitor();

        for (Bundle bundle : podCastCatalog.getBundles()) {
            if(bundle.getBundleType() == BundleType.Category) {
                for (BundleItem bundleItem : bundle.getBundleItems()) {
                    bundleItem.accept(bundleItemVisitor);
                }
            }
        }

       // LOG.info("CreateLinkPages bundleItemVisitor=" + bundleItemVisitor.getPodCastEpisodes().size() );


        File podDataHomeDir = ServiceDataStorage.useDefault().getPodDataHomeDir();
        File linkPagesDir = new File(podDataHomeDir, "LinkPages");
        if(!linkPagesDir.exists()) {
            linkPagesDir.mkdirs();
        }

        List<PodCast> podCasts = bundleItemVisitor.getPodCasts();
        LOG.info("podCasts=" + podCasts.size());

        int maxPodCasts = podCasts.size();
        int maxEpisodes = 50;
        int podCastCounter = 0;

        for(PodCast podCast : podCasts) {
            podCastCounter++;
            int episodeCounter = 0;
            for(PodCastEpisode podCastEpisode: podCast.getPodCastEpisodes()){
                createLinkPage(templateRoot, linkPagesDir, podCast, podCastEpisode);
                episodeCounter++;
                if (episodeCounter>=maxEpisodes) {
                    break;
                }
            }

            if(podCastCounter >= maxPodCasts){
                break;
            }

        }

     //   createLinkPage(templateRoot, linkPagesDir, podCast, podCastEpisode);
    }

    private String changeSwedishCharactersAndWhitespace(String string) {
        String newString = string.
                replaceAll("å", "a").
                replaceAll("ä", "a").
                replaceAll("ö", "o").
                replaceAll("Å", "A").
                replaceAll("Ä", "A").
                replaceAll("Ö", "O").
                replaceAll("\\s", "-");
        return newString;
    }
    private void createLinkPage(File templateRoot, File linkPagesDir, PodCast podCast, PodCastEpisode podCastEpisode) {
        try {

            String episodeName = podCastEpisode.getTitle().replaceAll("\\s", "-");
            String podCastName = podCast.getTitle().replaceAll("\\s", "-");
            episodeName = changeSwedishCharactersAndWhitespace(episodeName);// URLEncoder.encode( episodeName, "UTF-8" );
            podCastName = changeSwedishCharactersAndWhitespace(podCastName); // URLEncoder.encode( podCastName, "UTF-8" );

            File linkPageRoot = new File(linkPagesDir, podCastName + File.separator + episodeName);
            if(linkPageRoot.exists()){
                return;
            }
            linkPageRoot.mkdirs();


        File templateCss = new File(templateRoot, "default.css");
       // LOG.info("CreateLinkPages episodeRoot=" + linkPageRoot.getAbsolutePath() + "templateCss=" + templateCss );

        File indexFile = new File(templateRoot, "index.html");
        File targetIndexFile = new File(linkPageRoot, "index.html");
        File targetImage = new File(linkPageRoot, "image.jpg");

            FileUtils.copyFileToDirectory(templateCss, linkPageRoot);
            replaceText(indexFile, targetIndexFile, podCast, podCastEpisode);

            String artworkUrl600 = podCastEpisode.getArtworkUrl600();
           // LOG.info("download image artworkUrl600=" + artworkUrl600);

            try(InputStream in = new URL(artworkUrl600).openStream()){
                Files.copy(in, targetImage.toPath()); // Paths.get("C:/File/To/Save/To/image.jpg"));
            }
            //FIXME
            //Create QR code...
            LOG.info("Created LinkPage " + podCastEpisode.getTitle());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void replaceText(File sourceFile, File targetFile, PodCast podCast, PodCastEpisode podCastEpisode) {
        try {
            String pid= URLEncoder.encode( podCast.getCollectionId(), "UTF-8" );
            String eid= URLEncoder.encode( podCastEpisode.getId(), "UTF-8" );

            String podCastTitle = URLEncoder.encode( podCast.getTitle(), "UTF-8" );
            String podCastEpisodeTitle = URLEncoder.encode( podCastEpisode.getTitle(), "UTF-8" );
            String podCastImage = podCast.getArtworkUrl600();
            String targetLink = "https://qw7xh.app.goo.gl?link%3Dhttp://www.podsapp.se?pid%3D" +
                    pid + "&eid%3D" + eid + "&isi%3D1209200428&ibi%3Dcom.app.Pods&st%3D" +
                    podCastTitle + "&sd%3D" + podCastEpisodeTitle + "&si%3D" + podCastImage;


            Path path = sourceFile.toPath();
            Stream<String> lines = Files.lines(path, Charset.forName("UTF-8")); //ISO-8859-1
            List <String> replaced = lines.map(line -> line.replaceAll("template_podcast_title",podCast.getTitle()).
                    replaceAll("template_target_link",targetLink).
                           replaceAll("template_meta_description",podCast.getDescription()).
                            replaceAll("template_podcast_image",podCast.getArtworkUrl600()).
                    //replaceAll("template_podcast_title",podCast.getTitle()).
                    replaceAll("template_podcast_episode_title",podCastEpisode.getTitle()).
                    replaceAll("template_podcast_episode_description",podCastEpisode.getDescription()

                            )).collect(Collectors.toList());
            Files.write(targetFile.toPath(), replaced);
            lines.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
