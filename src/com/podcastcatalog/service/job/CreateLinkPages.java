package com.podcastcatalog.service.job;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.glassfish.jersey.server.Uri;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by miguelkrantz on 2018-06-09.
 */
public class CreateLinkPages implements Job {

    private final static Logger LOG = Logger.getLogger(CreateLinkPages.class.getName());

    private volatile boolean executedOnce = false;

    private final Gson GSON = new Gson();

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

        int maxPodCasts = 1 ; //podCasts.size();
        int maxEpisodes = 2;
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

     String createShortLink(String pid, String eid, String podCastTitle,
                                   String podCastEpisodeTitle, String podCastImage) {

         String linkValue = "http://www.podsapp.se?eid=" +
                 eid + "&pid=" + pid + "&isi=1209200428&ibi=com.app.Pods&st=" +
                 podCastTitle + "&sd=" + podCastEpisodeTitle + "&si=" + podCastImage;

         String longLink = null;
         HttpPost request = null;
        try {
            String linkValueEncoded = URLEncoder.encode(linkValue, "UTF-8");
            longLink = "https://qw7xh.app.goo.gl?link=" + linkValueEncoded;

            String webApiKey = "AIzaSyBbpNKapYpB4LtkPTI9Xbrd0TkG7wtw1mY";
            String shortLinksURL = "https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=" + webApiKey;


            HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
            request = new HttpPost(shortLinksURL);
            StringEntity params =new StringEntity("{\"longDynamicLink\":\" + " + longLink + " + \"} ");
            request.addHeader("content-type", "application/json; charset=utf-8");
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            String output;
            StringBuilder content1 = new StringBuilder();

            while ((output = br.readLine()) != null) {
                content1.append(output);
                content1.append(System.lineSeparator());
            }

                Map map = GSON.fromJson(content1.toString(), Map.class);

                Object shortLink = map.get("shortLink");
                if(shortLink != null && shortLink instanceof String){
                    return (String) shortLink;
                }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(request!= null){
                request.releaseConnection();
            }
        }

         System.out.println("Failed creating shortLink using longLink=" + longLink);
         return  longLink;
    }


    private void replaceText(File sourceFile, File targetFile, PodCast podCast, PodCastEpisode podCastEpisode) {
        try {
            String pid=  podCast.getCollectionId();
            String eid=  podCastEpisode.getId();

            String podCastTitle =  podCast.getTitle();
            String  podCastEpisodeTitle =  podCastEpisode.getTitle();
            String  podCastImage = podCast.getArtworkUrl600();

            String targetLink  = createShortLink(pid, eid, podCastTitle, podCastEpisodeTitle, podCastImage);

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
