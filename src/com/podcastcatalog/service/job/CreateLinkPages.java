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
import com.podcastcatalog.util.DynamicLinkIndex;
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
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
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

    private static final int MAX_PODCAST_EPISODE = 2;
    private static final int MAX_PODCAST = 3;
    private static final File LINK_PAGES_ROOT_DIR = new File(ServerInfo.localPath, "web-external");
    private static final File TEMPLATE_ROOT_DIR = new File(LINK_PAGES_ROOT_DIR , "link-page-template");

    private volatile boolean executedOnce = false;

    private final Gson GSON = new Gson();

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    private WebSitemapGenerator webSitemapGenerator;
    private DynamicLinkIndex linkIndex;

    @Override
    public void doWork() {

        if(!ServerInfo.isLocalDevMode() || executedOnce ) {
            return;
        }

        executedOnce = true;

        File linkPagesDir = linkPageRootDir();

        linkIndex = new DynamicLinkIndex();
        File dynamicLinksIndex = new File(linkPagesDir.getParentFile(), "dynamicLinks-index.json");
        linkIndex.loadFrom(dynamicLinksIndex);

        try {
            File templateDefaultCSS = new File(TEMPLATE_ROOT_DIR, "default.css");
            FileUtils.copyFileToDirectory(templateDefaultCSS, linkPagesDir);

            File templateCss = new File(LINK_PAGES_ROOT_DIR, "LinkPages" + File.separator + "css" + File.separator + "style.css");
            FileUtils.copyFileToDirectory(templateCss, linkPagesDir);

            File templateJs = new File(LINK_PAGES_ROOT_DIR,  "LinkPages" + File.separator + "js" + File.separator + "index.js");
            FileUtils.copyFileToDirectory(templateJs, linkPagesDir);

            //Episode template stuff
            File templateCss2 = new File(LINK_PAGES_ROOT_DIR, "LinkPages" + File.separator + "css" + File.separator + "style-episode.css");
            FileUtils.copyFileToDirectory(templateCss2, linkPagesDir);

            File templateJs2 = new File(LINK_PAGES_ROOT_DIR,  "LinkPages" + File.separator + "js" + File.separator + "index-episode.js");
            FileUtils.copyFileToDirectory(templateJs2, linkPagesDir);

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

            List<PodCast> podCasts = getPodCasts(podCastCatalog);

            createLanguageRootDirectory(podCasts, podCastCatalogLanguage);
        }

        linkIndex.saveTo(dynamicLinksIndex);
        webSitemapGenerator.write(); //Write sitemap
        webSitemapGenerator.writeSitemapsWithIndex();
    }

    private void createLanguageRootDirectory(List<PodCast> podCasts, PodCastCatalogLanguage podCastCatalogLanguage) {

        StringBuilder allPodCastsHtml = new StringBuilder();

        List<ForkJoinTask> forkJoinTasks = new ArrayList<>();

        for (PodCast podCast : podCasts) {
            forkJoinTasks.add(forkJoinPool.submit(new PodCastDirectoryAction(podCast, podCastCatalogLanguage)));

            String podCastName = podCast.getTitle().replaceAll("\\s", "-");
            podCastName = changeSwedishCharactersAndWhitespace(podCastName); // URLEncoder.encode( podCastName, "UTF-8" );

            String podCastImage = podCast.getArtworkUrl600();
            String podCastTitle = podCast.getTitle();
            int episodeCount = podCast.getPodCastEpisodesInternal().size();
            String podCastOverviewURL =  podCastName + "/index.html";
            String x = "<figure class=\"snip1584\"><img src=\"" + podCastImage + "\" alt=\"sample87\"/>\n" +
                    "  <figcaption>\n" +
                    "    <h3>" + podCastTitle + "</h3>\n" +
                    "    <h5>Show all (" + episodeCount + ") episodes</h5>\n" +
                    "  </figcaption><a href=\"" + podCastOverviewURL + "\"></a>\n" +
                    "</figure>";
            allPodCastsHtml.append(x);
        }

        try {

            File podCastTemplate = new File(LINK_PAGES_ROOT_DIR, "LinkPages" + File.separator + "podcast-template" + File.separator + "index.html");
            Path path = podCastTemplate.toPath();
            Stream<String> lines = Files.lines(path, Charset.forName("UTF-8")); //ISO-8859-1

            List <String> replaced = lines.map(line -> line.replaceAll("template_all_podcasts_fragments",
                    allPodCastsHtml.toString())).collect(Collectors.toList());

            File podCastTemplateTarget = new File(linkPageLangRootDir(podCastCatalogLanguage), "index.html");
            Files.write(podCastTemplateTarget.toPath(), replaced);
            lines.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (ForkJoinTask joinTask : forkJoinTasks) {
            try {
                joinTask.get(15, TimeUnit.MINUTES);
            } catch (Exception e) {
                LOG.info("Took more then 15 min to process " + e.getMessage());
                //e.printStackTrace();
            }
        }
    }

    private List<PodCast> getPodCasts(PodCastCatalog podCastCatalog) {
        BundleItemVisitor bundleItemVisitor = new BundleItemVisitor();

        for (Bundle bundle : podCastCatalog.getBundles()) {
            if(bundle.getBundleType() == BundleType.Category) {
                for (BundleItem bundleItem : bundle.getBundleItems()) {
                    bundleItem.accept(bundleItemVisitor);
                }
            }
        }

        List<PodCast> podCasts = bundleItemVisitor.getPodCasts();
        LOG.info("podCasts=" + podCasts.size());

        podCasts = podCasts.subList(0, MAX_PODCAST); //FIXME
        return podCasts;
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
                replaceAll("/", "-").
                replaceAll(",", "-").
                replaceAll(";", "-").
                replaceAll("''", "-").
                replaceAll(":", "-").
                replaceAll("\\s", "-");

        String s = newString.replaceAll("[^A-Za-z0-9]", "");

      /*  try {
            return URLEncoder.encode(newString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.info("Failed encode string " + newString + ", " + e.getMessage());
            return newString;
        }*/
        return s;
    }

    String createShortLink(String pid, String eid, String podCastTitle,
                                   String podCastEpisodeTitle, String podCastImage) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DynamicLinkIndex.Key key = DynamicLinkIndex.Key.createKey(pid, eid);
        String targetLink = linkIndex.getValue(key);

        if(targetLink!=null){
            return targetLink;
        }

        String linkValue = "http://www.podsapp.se?eid=" +
                 eid + "&pid=" + pid + "&isi=1209200428&ibi=com.app.Pods&st=" +
                 podCastTitle + "&sd=" + podCastEpisodeTitle + "&si=" + podCastImage;

         if(eid==null){
              linkValue = "http://www.podsapp.se?pid=" + pid + "&isi=1209200428&ibi=com.app.Pods&st=" +
                     podCastTitle + "&si=" + podCastImage;
         }

         String longLink = null;
         HttpPost request = null;
        try {
            String quotaUser = podCastTitle.length() >10 ? podCastTitle.substring(0, 10) : "A123";

            //quotaUser Testing FIXME
            String linkValueEncoded = URLEncoder.encode(linkValue + "&quotaUser="  + quotaUser, "UTF-8");
            longLink = "https://qw7xh.app.goo.gl?link=" + linkValueEncoded;

            String webApiKey = "AIzaSyBbpNKapYpB4LtkPTI9Xbrd0TkG7wtw1mY";
            String shortLinksURL = "https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=" + webApiKey;

            //=quotaUser" + quotaUser + "

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
                    String shortLink1 = (String) shortLink;
                    linkIndex.addLink(key, shortLink1);
                    return shortLink1;

                }
            System.out.println("Error creating shortLink " + shortLink + ", Result Map=" + map + ", linkValueEncoded=" + linkValueEncoded);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(request!= null){
                request.releaseConnection();
            }
        }

       //  System.out.println("Failed creating shortLink using longLink=" + longLink);
         return  null; //longLink;
    }


    private void updateText(PodCast podCast, PodCastEpisode podCastEpisode,  File linkPageRoot) {
        File targetFile = new File(linkPageRoot, "index.html");
        File sourceFile = new File(linkPageRoot, "index.html");

        if(!sourceFile.exists()){
            replaceText(podCast, podCastEpisode, linkPageRoot);
            return;
        }

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

    private void handleEpisodeTargetIndexFile(File indexFile){
        //FIXME Read dynamic link add
        try {
            Path path = indexFile.toPath();
            Stream<String> lines = Files.lines(path, Charset.forName("UTF-8"));

            String line = lines.filter(s -> s.contains("https://qw7xh.app.goo.gl")).findFirst().orElseGet(null);
            System.out.println("Line to search in " + line);

            //lines.filter(s -> s.)

          /*  List <String> replaced = lines.map(line -> line.replaceAll("template_podcast_title",podCast.getTitle()).
                    replaceAll("template_meta_description",podCast.getDescription()).
                    replaceAll("template_podcast_image",podCast.getArtworkUrl600()).
                    replaceAll("template_podcast_episode_title",podCastEpisode.getTitle()).
                    replaceAll("template_podcast_episode_description",podCastEpisode.getDescription()

                    )).collect(Collectors.toList());
            Files.write(targetFile.toPath(), replaced);
            lines.close();*/
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
            LOG.info("replaceText target=" + linkPageRoot.getAbsolutePath());
            e.printStackTrace();
        }
    }

    private class PodCastEpisodeDirectoryAction extends RecursiveAction {

        private PodCastEpisode podCastEpisode;

        PodCast podCast;
        PodCastCatalogLanguage lang;

        PodCastEpisodeDirectoryAction(PodCast podCast, PodCastEpisode podCastEpisode, PodCastCatalogLanguage lang) {
            this.podCastEpisode = podCastEpisode;
            this.podCast = podCast;
            this.lang = lang;
        }

        void computeDirectly() {
            try {

                String episodeName = podCastEpisode.getTitle().replaceAll("\\s", "-");
                String podCastName = podCast.getTitle().replaceAll("\\s", "-");
                episodeName = changeSwedishCharactersAndWhitespace(episodeName);// URLEncoder.encode( episodeName, "UTF-8" );
                podCastName = changeSwedishCharactersAndWhitespace(podCastName); // URLEncoder.encode( podCastName, "UTF-8" );

                File podCastEpisodeDirectoryRoot = new File(linkPageLangRootDir(lang), podCastName + File.separator + episodeName);

                String externalURL =  "https://www.pods.one/podcast/" + lang.name() + "/" + podCastName + File.separator + episodeName;
               // System.out.println("External=" + externalURL);

                webSitemapGenerator.addUrl(externalURL);

                if(podCastEpisodeDirectoryRoot.exists()) {
                    LOG.info("Update PodCastEpisode " + episodeName);
                    updateText(podCast, podCastEpisode, podCastEpisodeDirectoryRoot);
                } else {
                    LOG.info("Create PodCastEpisodeRoot =" + podCastEpisodeDirectoryRoot.getAbsolutePath());
                    podCastEpisodeDirectoryRoot.mkdirs();
                    replaceText(podCast, podCastEpisode, podCastEpisodeDirectoryRoot);
                    String artworkUrl600 = podCastEpisode.getArtworkUrl600();
                    File targetImage = new File(podCastEpisodeDirectoryRoot, "image.jpg");
                    try (InputStream in = new URL(artworkUrl600).openStream()) {
                        Files.copy(in, targetImage.toPath());
                    }
                }

                //FIXME
                //Create QR code...
                LOG.info("Created LinkPage for PodCastEpisode podCast=" + podCast.getTitle() + ", " + podCastEpisode.getTitle());
            } catch (IOException e) {
                LOG.info("Error Created LinkPage");
                e.printStackTrace();
            }
        }
            @Override
        protected void compute() {
            computeDirectly();
        }
    }

    class PodCastDirectoryAction extends RecursiveAction {

        private PodCast podCast;
        private PodCastCatalogLanguage lang;

            PodCastDirectoryAction(PodCast podCast, PodCastCatalogLanguage lang) {
                this.podCast = podCast;
                this.lang = lang;
            }

            @Override
            protected void compute() {

                List<PodCastEpisodeDirectoryAction> tasks = new ArrayList<>();
                List<PodCastEpisode> podCastEpisodes = podCast.getPodCastEpisodesInternal();
                podCastEpisodes = podCastEpisodes.size() > MAX_PODCAST_EPISODE ? podCastEpisodes.subList(0, MAX_PODCAST_EPISODE) : podCastEpisodes;

                LOG.info("PodCastDirectoryAction: " + podCast.getTitle() + " Episodes=" + podCastEpisodes.size());

                StringBuilder allEpisodesHtml = new StringBuilder();

                for (PodCastEpisode podCastEpisode : podCastEpisodes) {
                    tasks.add(new PodCastEpisodeDirectoryAction(podCast, podCastEpisode, lang));

                    String episodeName = podCastEpisode.getTitle().replaceAll("\\s", "-");
                    episodeName = changeSwedishCharactersAndWhitespace(episodeName);

                    String episodeLinkTitle = podCastEpisode.getTitle() ;
                    String episodeLink =  episodeName + "/index.html";
                    String x = "<button onclick=\"window.location.href='" + episodeLink + "'\" class=\"snip0076 hover blue\"><span>" + episodeLinkTitle + " </span><i class=\"ion-android-arrow-forward\"></i></button><br/>\n";

                    allEpisodesHtml.append(x);
                }

                Collection<PodCastEpisodeDirectoryAction> podCastEpisodeDirectoryActions = invokeAll(tasks);

                for (PodCastEpisodeDirectoryAction action : podCastEpisodeDirectoryActions) {
                    try {
                        action.get(30,TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    String podCastTitle =  podCast.getTitle();
                    String  podCastImage = podCast.getArtworkUrl600();

                    String podCastDynamicLink  = createShortLink(podCast.getCollectionId(), null, podCastTitle, null, podCastImage);

                    File episodeTemplate = new File(LINK_PAGES_ROOT_DIR, "LinkPages" + File.separator + "episode-template" + File.separator + "index.html");

                    String podCastName = podCast.getTitle().replaceAll("\\s", "-");
                    podCastName = changeSwedishCharactersAndWhitespace(podCastName);

                    File podCastLinkPageRoot = new File(linkPageLangRootDir(lang), podCastName);

                    File episodeTemplateTarget = new File(podCastLinkPageRoot, "index.html");

                    Path path = episodeTemplate.toPath();
                    Stream<String> lines = Files.lines(path, Charset.forName("UTF-8")); //ISO-8859-1
                    List <String> replaced = lines.map(line -> line.replaceAll("template_all_episodes_fragments",
                            allEpisodesHtml.toString()).replaceAll("template_podcast_title", podCast.getTitle()).
                            replaceAll("template_podcast_link",podCastDynamicLink).
                            replaceAll("template_podcast_image",podCast.getArtworkUrl600())).collect(Collectors.toList());

                    Files.write(episodeTemplateTarget.toPath(), replaced);
                    lines.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
}
