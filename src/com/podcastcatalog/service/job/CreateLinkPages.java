package com.podcastcatalog.service.job;


import com.google.gson.Gson;
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
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by miguelkrantz on 2018-06-09.
 */
public class CreateLinkPages implements Job {

    private final static Logger LOG = Logger.getLogger(CreateLinkPages.class.getName());
    public static final int MAX_PODCAST_EPISODE = 2;
    public static final int MAX_PODCAST = 2;

    private volatile boolean executedOnce = false;

    private final Gson GSON = new Gson();

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    private static final File LINK_PAGES_ROOT_DIR = new File(ServerInfo.localPath, "web-external");

    private static final File TEMPLATE_ROOT_DIR = new File(LINK_PAGES_ROOT_DIR , "link-page-template");

    private WebSitemapGenerator webSitemapGenerator;
    @Override
    public void doWork() {

        if(!ServerInfo.isLocalDevMode() || executedOnce ) {
            return;
        }

        executedOnce = true;

        File linkPagesDir = linkPageRootDir();

        try {
            File templateCss = new File(LINK_PAGES_ROOT_DIR, "LinkPages" + File.separator + "css" + File.separator + "style.css");
            FileUtils.copyFileToDirectory(templateCss, linkPagesDir);

            File templateJs = new File(LINK_PAGES_ROOT_DIR,  "LinkPages" + File.separator + "js" + File.separator + "index.js");
            FileUtils.copyFileToDirectory(templateJs, linkPagesDir);

            webSitemapGenerator = new WebSitemapGenerator("https://www.pods.one", linkPagesDir);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        for (PodCastCatalogLanguage podCastCatalogLanguage : PodCastCatalogLanguage.values()) {

            PodCastCatalog podCastCatalog = PodCastCatalogService.getInstance().getPodCastCatalog(podCastCatalogLanguage);

            if(podCastCatalog==null) {
                LOG.info("CreateLinkPages catalog not loaded yet? for lang=" + podCastCatalogLanguage);
                continue;
            }

            LOG.info("CreateLinkPages for lang=" + podCastCatalogLanguage);

            createPages(podCastCatalog);
        }

        webSitemapGenerator.write(); //Write sitemap
        webSitemapGenerator.writeSitemapsWithIndex();

    }

    private void createPages(PodCastCatalog podCastCatalog) {
        BundleItemVisitor bundleItemVisitor = new BundleItemVisitor();

        for (Bundle bundle : podCastCatalog.getBundles()) {
            if(bundle.getBundleType() == BundleType.Category) {
                for (BundleItem bundleItem : bundle.getBundleItems()) {
                    bundleItem.accept(bundleItemVisitor);
                }
            }
        }

        // LOG.info("CreateLinkPages bundleItemVisitor=" + bundleItemVisitor.getPodCastEpisodes().size() );


        List<PodCast> podCasts = bundleItemVisitor.getPodCasts();
        LOG.info("podCasts=" + podCasts.size());

        List<ForkJoinTask> forkJoinTasks = new ArrayList<>();

        podCasts = podCasts.subList(0, MAX_PODCAST); //FIXME


        File linkPageLangRootDir = linkPageLangRootDir(podCastCatalog.getPodCastCatalogLanguage());

        for (PodCast podCast : podCasts) {
            forkJoinTasks.add(forkJoinPool.submit(new PodCastAction(podCast, linkPageLangRootDir,podCastCatalog.getPodCastCatalogLanguage())));
        }

        for (ForkJoinTask joinTask : forkJoinTasks) {
            try {
                joinTask.get(3, TimeUnit.MINUTES);
            } catch (Exception e) {
                LOG.info("Took more then 2 min to process ");
                e.printStackTrace();
            }
        }
    }



    private File linkPageLangRootDir(PodCastCatalogLanguage podCastCatalogLanguage) {
        File linkPagesDir = new File(linkPageRootDir(), podCastCatalogLanguage.name());
        if(!linkPagesDir.exists()) {
            linkPagesDir.mkdirs();
        }
        return linkPagesDir;
    }

    private File linkPageRootDir() {
        File podDataHomeDir = ServiceDataStorage.useDefault().getPodDataHomeDir();
        File linkPagesDir = new File(podDataHomeDir, "LinkPages");
        if(!linkPagesDir.exists()) {
            linkPagesDir.mkdirs();
        }
        return linkPagesDir;
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
            System.out.println("Error creating shortLink" + shortLink);
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


    private void updateText(PodCast podCast, PodCastEpisode podCastEpisode,  File linkPageRoot) {
        File targetFile = new File(linkPageRoot, "index.html");
        File sourceFile = new File(linkPageRoot, "index.html");

        try {
            Path path = sourceFile.toPath();
            Stream<String> lines = Files.lines(path, Charset.forName("UTF-8")); //ISO-8859-1
            List <String> replaced = lines.map(line -> line.replaceAll("template_podcast_title",podCast.getTitle()).
                    replaceAll("template_meta_description",podCast.getDescription()).
                    replaceAll("template_podcast_image",podCast.getArtworkUrl600()).
                            replaceAll("template_podcast_episode_title",podCastEpisode.getTitle()).
                            replaceAll("template_podcast_episode_description",podCastEpisode.getDescription()

                            )).collect(Collectors.toList());
            Files.write(targetFile.toPath(), replaced);
            lines.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //createEpisodeNode
    private void replaceText(PodCast podCast, PodCastEpisode podCastEpisode,  File linkPageRoot) {
        File targetFile = new File(linkPageRoot, "index.html");
        File sourceFile = new File(TEMPLATE_ROOT_DIR, "index.html"); //If new

        try {
            String pid=  podCast.getCollectionId();
            String eid=  podCastEpisode.getId();

            String podCastTitle =  podCast.getTitle();
            String  podCastEpisodeTitle =  podCastEpisode.getTitle();
            String  podCastImage = podCast.getArtworkUrl600();

            String targetLink  = createShortLink(pid, eid, podCastTitle, podCastEpisodeTitle, podCastImage);

            if(targetLink==null){
                //Failed creating short link, do not create episode dir.
                return;
            }

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


    private class PodCastEpisodeAction extends RecursiveAction {

        private PodCastEpisode podCastEpisode;

        File linkPagesDir;
        PodCast podCast;
        PodCastCatalogLanguage lang;

        PodCastEpisodeAction(File linkPagesDir, PodCast podCast, PodCastEpisode podCastEpisode,PodCastCatalogLanguage lang) {
            this.podCastEpisode = podCastEpisode;
            this.linkPagesDir = linkPagesDir;
            this.podCast = podCast;
            this.lang = lang;
        }

        void computeDirectly() {
            try {

                String episodeName = podCastEpisode.getTitle().replaceAll("\\s", "-");
                String podCastName = podCast.getTitle().replaceAll("\\s", "-");
                episodeName = changeSwedishCharactersAndWhitespace(episodeName);// URLEncoder.encode( episodeName, "UTF-8" );
                podCastName = changeSwedishCharactersAndWhitespace(podCastName); // URLEncoder.encode( podCastName, "UTF-8" );

                File linkPageRoot = new File(linkPagesDir, podCastName + File.separator + episodeName);

               // String lang = PodCastCatalogLanguage.SE.name();
                String externalURL =  "https://www.pods.one/podcast/" + lang.name() + "/" + podCastName + File.separator + episodeName;
               // System.out.println("External=" + externalURL);

                webSitemapGenerator.addUrl(externalURL);
                boolean isUpdateTextRequest = false;
                if(linkPageRoot.exists()){
                    //FIXME Do update no new UniversalLink
                    LOG.info("Update " + episodeName);
                    isUpdateTextRequest = true;
                } else {
                    LOG.info("Create new link=" + linkPageRoot.getAbsolutePath());
                    linkPageRoot.mkdirs();
                }

                if(isUpdateTextRequest) {
                    updateText(podCast, podCastEpisode, linkPageRoot);
                } else {
                    File templateCss = new File(TEMPLATE_ROOT_DIR, "default.css");
                    FileUtils.copyFileToDirectory(templateCss, linkPageRoot);
                    replaceText(podCast, podCastEpisode, linkPageRoot);
                    String artworkUrl600 = podCastEpisode.getArtworkUrl600();
                    File targetImage = new File(linkPageRoot, "image.jpg");
                    try (InputStream in = new URL(artworkUrl600).openStream()) {
                        Files.copy(in, targetImage.toPath()); // Paths.get("C:/File/To/Save/To/image.jpg"));
                    }
                }

                //FIXME
                //Create QR code...
                LOG.info("Created LinkPage for PodCastEpisode podCast=" + podCast.getTitle() + ", " + podCastEpisode.getTitle());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            @Override
        protected void compute() {
            computeDirectly();
        }
    }

    class PodCastAction extends RecursiveAction {

           private PodCast podCast;
           File linkPagesDir;
        PodCastCatalogLanguage lang;

            PodCastAction(PodCast podCast, File linkPageLangRootDir, PodCastCatalogLanguage lang) {
                this.podCast = podCast;
                this.lang = lang;
                linkPagesDir = linkPageLangRootDir;
            }

            @Override
            protected void compute() {

                List<PodCastEpisodeAction> tasks = new ArrayList<>();
                List<PodCastEpisode> podCastEpisodes = podCast.getPodCastEpisodesInternal();
                podCastEpisodes = podCastEpisodes.size() > MAX_PODCAST_EPISODE ? podCastEpisodes.subList(0, MAX_PODCAST_EPISODE) : podCastEpisodes;

                LOG.info("PodCastAction: " + podCast.getTitle() + " Episodes=" + podCastEpisodes.size());

                File podCastTemplate = new File(LINK_PAGES_ROOT_DIR, "LinkPages" + File.separator + "podcast-template" + File.separator + "index.html");
                try {
                    FileUtils.copyFileToDirectory(podCastTemplate, linkPagesDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                for (PodCastEpisode podCastEpisode : podCastEpisodes) {
                    tasks.add(new PodCastEpisodeAction(linkPagesDir, podCast, podCastEpisode, lang));
                }

                invokeAll(tasks);

            }
        }
}
