package com.podcastcatalog.builder;

import com.podcastcatalog.api.response.bundle.Bundle;
import com.podcastcatalog.api.response.PodCastCatalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PodCastCatalogBuilderService {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Callback callback;

    public void registerListener(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void handleNewCatalog(PodCastCatalog podCastCatalog);
    }

    public PodCastCatalog buildPodcastCatalog(PodCastCatalogBuilder podCastCatalogBuilder){

        Set<BundleBuilder> bundles = podCastCatalogBuilder.getBundleBuilders();
        List<Bundle> podCastBundle1s = invoke(bundles);

        return PodCastCatalog.create(podCastCatalogBuilder.getPodCastCatalogLang(), podCastBundle1s);
    }

    private List<Bundle> invoke(Set<BundleBuilder>  tasks){
        List<Bundle> podCastBundle1s = new ArrayList<>();
        try {
            List<Future<Bundle>> futures = executorService.invokeAll(tasks);
            for (Future<Bundle> future : futures) {
                Bundle podCastBundle1 = future.get();
                podCastBundle1s.add(podCastBundle1);
            }
        } catch (Exception e) {
            e.printStackTrace();//FIXME
        }
        return podCastBundle1s;
    }

   /* void startBuildCatalog() throws InterruptedException, ExecutionException {
        //FIXME Async
        Set<PodCastBundleTask> podCastBundleTasks = new HashSet<>();
        PodCastBundleTask podCastBundleTask = PodCastBundleTask.create("Toplistan Sverige","descr", Collections.emptyList());
        podCastBundleTasks.add(podCastBundleTask);

        List<Future<PodCastBundle1>> futures = executorService.invokeAll(podCastBundleTasks);
          List<PodCastBundle1> podCastCategories = new ArrayList<PodCastBundle1>();
          List<PodCastBundle1> getBundleBuilders = new ArrayList<PodCastBundle1>();
        for (Future<PodCastBundle1> future : futures) {
            PodCastBundle1 podCastBundle = future.get();
            System.out.println("future.get = " + podCastBundle);
            getBundleBuilders.add(podCastBundle);
        }

        PodCastCatalog podCastCatalog = PodCastCatalog.create(PodCastCatalogLanguage.Sweden, podCastCategories, getBundleBuilders);

        callback.handleNewCatalog(podCastCatalog);
    }*/
}
