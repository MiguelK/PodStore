package com.podcastcatalog.modelbuilder.collector.itunes;


import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisodeType;
import com.podcastcatalog.modelbuilder.collector.PodCastFeedParser;
import it.sauronsoftware.feed4j.bean.FeedItem;

import java.util.List;
import java.util.concurrent.RecursiveTask;

public class PodCastEpisodeProcessor extends RecursiveTask<PodCastEpisode> {
    private static final long serialVersionUID = 1L;
    //This attribute will store the full path of the folder this task is going to process.
//    private final String path;
    //This attribute will store the name of the extension of the files this task is going to look for.
    FeedItem feedItem;
    String collectionId;

    //Implement the constructor of the class to initialize its attributes
    public PodCastEpisodeProcessor(FeedItem feedItem, String collectionId) {
        this.feedItem = feedItem;
        this.collectionId = collectionId;
//        this.artworkUrl600 = artworkUrl600;
//        this.collectionId = collectionId;
//        this.extension = extension;
    }

    //Implement the compute() method. As you parameterized the RecursiveTask class with the List<String> type,
    //this method has to return an object of that type.
    @Override
    protected PodCastEpisode compute() {
        //List to store the names of the files stored in the folder.
//        List<PodCast> list =new ArrayList<>();
        //FolderProcessor tasks to store the subtasks that are going to process the subfolders stored in the folder
//        List<FolderProcessor> tasks = new ArrayList<FolderProcessor>();
//

//        System.out.println("Episode: " + Thread.currentThread().getName());
      PodCastFeedParser.PodCastFeedItem podCastFeedItem = new PodCastFeedParser.PodCastFeedItem(feedItem);


        String guid = feedItem.getGUID();
        //FIXME createdDate
        PodCastEpisode.Builder episodeBuilder = PodCastEpisode.newBuilder();
        episodeBuilder.title(podCastFeedItem.getTitle()).podCastCollectionId(collectionId).
                createdDate(podCastFeedItem.getCreatedDate()).description(podCastFeedItem.getDescription()).id(guid).
                duration(podCastFeedItem.getDuration()).fileSizeInMegaByte(podCastFeedItem.getFileSizeInMegaByte()).
                targetURL(podCastFeedItem.getTargetURL()).podCastType(podCastFeedItem.getPodCastType()); //FIXME type?

        if (episodeBuilder.isValid() && podCastFeedItem.getPodCastType() == PodCastEpisodeType.Audio) { //FIXME anly audio?
            return  episodeBuilder.build();
        }

        return null;

//        System.out.println("Parse: " + Thread.currentThread().getName());
//        Optional<PodCast> podCast = PodCastFeedParser.parse(feedURL, artworkUrl600, collectionId);
//                    podCasts.add(e);
//        return podCast.orElse(null);
//
        //Get the content of the folder.
//        File file = new File(path);
//        File content[] = file.listFiles();
        //For each element in the folder, if there is a subfolder, create a new FolderProcessor object
        //and execute it asynchronously using the fork() method.
        /*if (content != null) {
            for (int i = 0; i < content.length; i++) {
                if (content[i].isDirectory()) {
                    FolderProcessor task = new FolderProcessor(content[i].getAbsolutePath(), extension);
                    task.fork();
                    tasks.add(task);
                }
                //Otherwise, compare the extension of the file with the extension you are looking for using the checkFile() method
                //and, if they are equal, store the full path of the file in the list of strings declared earlier.
                else {
                    if (checkFile(content[i].getName())) {
                        list.add(content[i].getAbsolutePath());
                    }
                }
            }
        }*/
        //If the list of the FolderProcessor subtasks has more than 50 elements,
        //write a message to the console to indicate this circumstance.
//        if (tasks.size() > 50) {
//            System.out.printf("%s: %d tasks ran.\n", file.getAbsolutePath(), tasks.size());
//        }
        //add to the list of files the results returned by the subtasks launched by this task.
//        addResultsFromTasks(list, tasks);
        //Return the list of strings
    }

    //For each task stored in the list of tasks, call the join() method that will wait for its finalization and then will return the result of the task.
    //Add that result to the list of strings using the addAll() method.
    private void addResultsFromTasks(List<String> list, List<PodCastEpisodeProcessor> tasks) {
    /*    for (FolderProcessor item : tasks) {
            list.addAll(item.join());
        }*/
    }

    //This method compares if the name of a file passed as a parameter ends with the extension you are looking for.
//    private boolean checkFile(String name) {
//        return name.endsWith(extension);
//    }
}