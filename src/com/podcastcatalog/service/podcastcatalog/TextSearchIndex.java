package com.podcastcatalog.service.podcastcatalog;

import com.podcastcatalog.model.podcastsearch.PodCastEpisodeResultItem;
import com.podcastcatalog.model.podcastsearch.PodCastResultItem;
import com.podcastcatalog.model.podcastsearch.ResultItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class TextSearchIndex implements Serializable {

    static final long serialVersionUID=42L;


    private final static Logger LOG = Logger.getLogger(TextSearchIndex.class.getName());

    private final Map<String, Node> podCastEpisodeIndex = new ConcurrentSkipListMap<>();
    private final Map<String, Node> podCastIndex = new ConcurrentSkipListMap<>();

    private final int maxEpisodesToIndex;
    private final int maxPodCastsToIndex;

    public TextSearchIndex() {
        this.maxEpisodesToIndex = 50000;
        this.maxPodCastsToIndex = 1200;
    }

    public String getStatus() {
        return "podCastEpisodeIndex=" + podCastEpisodeIndex.size();
    }

    void printPodCastEpisodeIndex() {
        System.out.println("podCastEpisodeIndex{");
        System.out.println("");
        for (Node node : podCastEpisodeIndex.values()) {
            System.out.println(node);
        }
        System.out.printf(getStatus());
    }

    void printPodCastIndex() {
        for (Node node : podCastIndex.values()) {
            System.out.println(node);
        }
    }

    public void addText(String text, PodCastEpisodeResultItem targetObject) {
        if (podCastEpisodeIndex.size() >= maxEpisodesToIndex) {
            return;
        }

        String key = StringUtils.trimToEmpty(text).toLowerCase();
        List<String> strings = parseWordsEpisode(key);
        if (strings.isEmpty()) {
            return;
        }
        String firstChar = strings.get(0);// key.length() > 3 ? key.substring(0, 3) : key; //strings.get(0);

        podCastEpisodeIndex.put(firstChar, new Node(key, targetObject));
    }

    public void addText(String text, PodCastResultItem targetObject) {
        if (podCastIndex.size() >= maxPodCastsToIndex) {
            return;
        }
        String key = StringUtils.trimToEmpty(text).toLowerCase();
        podCastIndex.put(key, new Node(key, targetObject));
    }

    public void buildIndex() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        indexPodCastEpisodes();

        indexPodCasts();
        stopWatch.stop();

        LOG.info("Done building TextSearchIndex. podCastEpisodeIndex size=" + podCastEpisodeIndex.size()
                + ", podCastIndex size=" + podCastIndex.size() +
                " Time elapsed=" + stopWatch.toString());
    }

    private void indexPodCasts() {

        for (Node node : podCastIndex.values()) {
            ResultItem targetObject = node.getResultItem();

            //Index fully title
            podCastIndex.putIfAbsent(node.term, new Node(node.term, targetObject));

            List<String> allWordsInInputText = parseWords(node.term);

            //Index start of title
            int maxCharsToIndex = 0;
            String wordCharacters = "";
            String firstWord = allWordsInInputText.get(0);

            for (char c : firstWord.toCharArray()) {
                wordCharacters += c;
                if (podCastIndex.size() >= maxPodCastsToIndex) {
                    return;
                }
                Node node1 = podCastIndex.putIfAbsent(wordCharacters, new Node(wordCharacters, targetObject));
                if (node1 != null) {
                    node1.addTargetRelation(targetObject);
                }
                maxCharsToIndex += 1;
                if (maxCharsToIndex > 4) {
                    break;
                }
            }
            //Index all words
          /* allWordsInInputText.remove(0);
            for (String word : allWordsInInputText) {
                if(StringUtils.isEmpty(word) || word.length() <= 1) {
                    continue;
                }

                Node node2 = podCastIndex.putIfAbsent(word, new Node(word, targetObject));
                if (node2 != null) {
                    node2.addTargetRelation(targetObject);
                }
            }*/
        }
    }

    private void indexPodCastEpisodes() {

        for (Node node : podCastEpisodeIndex.values()) {
            ResultItem targetObject = node.getResultItem();

            List<String> allWordsInInputText = parseWordsEpisode(node.term);

            //Index all words
            Map<String, AtomicInteger> test = new ConcurrentSkipListMap<>();

            for (String word : allWordsInInputText) {
                Node node2 = podCastEpisodeIndex.putIfAbsent(word, new Node(word, targetObject));
                if (node2 != null) {
                  node2.addTargetRelation(targetObject);
                }

/*                AtomicInteger atomicInteger = test.putIfAbsent(word, new AtomicInteger(1));
                if (atomicInteger != null) {
                    atomicInteger.incrementAndGet();
                }*/
            }

           /* for (String s : test.keySet()) {

                AtomicInteger atomicInteger = test.get(s);
                if (podCastEpisodeIndex.size() >= maxEpisodesToIndex) {
                    return;
                }

                if (atomicInteger.get() > 0) { //FIXME
                    Node node2 = podCastEpisodeIndex.putIfAbsent(s, new Node(s, targetObject));
                    if (node2 != null) {
                        node2.addTargetRelation(targetObject);
                    }
                }
            }*/
        }

    }

    private List<String> parseWords(String word) {

        String[] split = StringUtils.split(word);
        List<String> validWords = new ArrayList<>();
        for (String s : split) {
            String replace = s.replace(",", "")
                    .replace(".", "");
            validWords.add(replace);
        }
//        System.out.println("Word=" + word + ": validWords=" + validWords);
        return validWords;
    }

    private List<String> parseWordsEpisode(String word) {

        String[] split = StringUtils.split(word);
        List<String> validWords = new ArrayList<>();
        for (String s : split) {
            String replace = StringUtils.trimToEmpty(s).replace(",", "")
                    .replace(".", "");

            if (StringUtils.isEmpty(replace) || replace.length() <= 2) {
                continue;
            }
            //FIXME add clean up filters
            // List<String> av = Arrays.asList("av", "på","er","ös","er","vi","de");
            // if(av.contains(replace)) {
            //  continue;
            //}

            boolean contains = StringUtils.containsAny(replace, "”_*!,.#0123456789''-,&%#+?");

            if (contains) {
                continue;
            }


            try {
                int i = Integer.parseInt(replace);
                // System.out.println("No add Integer " + i);
            } catch (Exception e) {
                //if(skipWords.get(replace) == null) {
                //  System.out.println("Adding word=" + replace);
                validWords.add(replace);
                //}
            }

            //boolean contains = StringUtils.containsAny(replace, "!,.#0123456789");
            // if(!contains) {
            //  System.out.println("Valid word: " + replace);
            //validWords.add(replace);
            //}
        }

        if (validWords.size() > 10) {
            //  System.out.println("CUT " + validWords.size());
            validWords = validWords.subList(0, validWords.size() - 1);
        }
        return validWords;
    }

    public List<ResultItem> lookupPodCastEpisodes(String queryParam, int maxResultSize) {
        return lookupFromIndex(podCastEpisodeIndex, queryParam, maxResultSize);
    }

    public List<ResultItem> lookupPodCast(String queryParam, int maxResultSize) {
        return lookupFromIndex(podCastIndex, queryParam, maxResultSize);
    }

    private List<ResultItem> lookupFromIndex(Map<String, Node> index, String queryParam, int maxResultSize) {
        if (StringUtils.trimToNull(queryParam) == null) {
            return Collections.emptyList();
        }

        String term = StringUtils.deleteWhitespace(queryParam).toLowerCase();
        Node node = index.get(term);


        if(node == null && term.length() > 5) {
            node = index.get(term.substring(0, 3));
        }

        Set<ResultItem> uniqueResultItems = new HashSet<>();
        if(node != null) {
            uniqueResultItems.addAll(node.getValues());
        }
            for (String key : index.keySet()) {
                if(key.startsWith(term)) {
                    uniqueResultItems.addAll(index.get(key).getValues());
                    //resultItems.addAll(index.get(key).getValues());
                }
            }

        List<ResultItem> resultItems = new ArrayList<>();
        resultItems.addAll(uniqueResultItems);

        if (resultItems.isEmpty()) {
            return Collections.emptyList();
        }

        if (resultItems.size() > maxResultSize) {
            return resultItems.subList(0, maxResultSize);
        }

        return resultItems;
    }

    private class Node implements Serializable {
     //   static final long serialVersionUID=42L;

        private final String term;
        private final Set<ResultItem> relatedPodCastEpisodes = new HashSet<>();

        Node(String term, ResultItem targetRelation) {
            this.term = term;
            relatedPodCastEpisodes.add(targetRelation);
        }

        void addTargetRelation(ResultItem targetRelation) {
            if (relatedPodCastEpisodes.size() <= 6) {
                relatedPodCastEpisodes.add(targetRelation);
            }
        }

        ResultItem getResultItem() {
            return getValues().get(0);
        }

        List<ResultItem> getValues() {
            return new ArrayList<>(relatedPodCastEpisodes);
        }

        @Override
        public String toString() {
            return "Node{" +
                    ", term='" + term + '\'' +
                    ", relatedPodCastEpisodes=" + relatedPodCastEpisodes.size() +
                    '}';
        }
    }
}
