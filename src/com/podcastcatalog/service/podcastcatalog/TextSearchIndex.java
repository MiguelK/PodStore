package com.podcastcatalog.service.podcastcatalog;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.util.*;
import java.util.logging.Logger;

public class TextSearchIndex<T> {

    private final static Logger LOG = Logger.getLogger(TextSearchIndex.class.getName());

    private static final int MAX_RESULT_SIZE = 50;
    private final Map<String, Node<T>> index;

    private final List<InputData> inputDatas;
    // private static final int MAX_WORDS_TO_INDEX = 4;
   // private int maxWordsToIndex  = MAX_WORDS_TO_INDEX;

    public String getStatus() {
        return "TextSearchIndex: indexSize=" + index.size();
    }
    void printIndex(){
        for (Map.Entry<String, Node<T>> entry : index.entrySet()) {
            System.out.println("Key= " + entry.getKey() + ", length=" + entry.getKey().length());
        }

        System.out.printf(getStatus());

    }

    public enum Prio {
        HIGH(100), HIGHEST(300), LOW(10);

        final int rank;

        Prio(int i) {
            this.rank = i;
        }

        public int getRank() {
            return rank;
        }
    }

    /*public TextSearchIndex(int maxWordsToIndex) {
        inputDatas = new ArrayList<>();
        index = new TreeMap<>();
        this.maxWordsToIndex = maxWordsToIndex;
        this.maxCharactersLengthToIndex = 10;
    }*/

    public TextSearchIndex() {
        inputDatas = new ArrayList<>();
        index = new TreeMap<>();
    }

    public void addText(String text, Prio prio, T targetObject) {
        if(text.length()>20){
            text = text.substring(0,13);

        }
        //Sommar i p5 dsdhs

        inputDatas.add(new InputData(StringUtils.trimToEmpty(text).toLowerCase(), prio, targetObject));
    }

    private static final int maxcharacterslengthtoindex = 7;

    public void buildIndex() {

        LOG.info("Start building TextSearchIndex... size=" + inputDatas.size());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (InputData inputData : inputDatas) {
            String text = inputData.getText();
            String inputTextPrefix = text.length()>maxcharacterslengthtoindex ? text.substring(0,maxcharacterslengthtoindex-1) : text;

            List<String> allWordsInInputText = Arrays.asList(StringUtils.split(text));

           /* int size = allWordsInInputText.size();
            if(size>100){
                LOG.info("allWordsInInputText size=" + size);
              //  continue;
                allWordsInInputText = allWordsInInputText.subList(0,99);
            }*/


            int rank = inputData.getPrio().getRank();
            TargetRelation<T> targetRelation = new TargetRelation<>(inputData.getTargetObject(), rank);

            Node<T> node = index.putIfAbsent(inputTextPrefix, new Node<>(inputTextPrefix, targetRelation));
            if (node != null) {
                //Ok 2 or more inputTextPrefix are the same pointing to different targets.?
             //   System.out.println("node=" + inputTextPrefix);
                node.addTargetRelation(targetRelation);
            }

            String totaWordParts = ""; //e.g Fallet Pe

            //sommar,i,peking //maxCharacterLengthPart
            int maxCharacterLengthPart = 7; //e.g sommaripeki
            //Fallet Peter Mangs i Sverige  abc
            for (String word : allWordsInInputText) {
                String term = "";

                Node<T> node1;
                Node<T> node2;

                if(totaWordParts.length()>maxCharacterLengthPart){
                    totaWordParts = ""; //Reset
                }

                for (char c : word.toCharArray()) {
                    term += c;
                    totaWordParts += c;

                    if (term.equals(word)) {
                        rank += 600; //FIXME ?
                    }

                    node1 = index.putIfAbsent(term, new Node<>(term, targetRelation));
                    if (node1 != null) {
                        //        System.out.println("Term=" + term);
                        node1.addTargetRelation(targetRelation);
                    }

                    node2 = index.putIfAbsent(totaWordParts, new Node<>(totaWordParts, targetRelation));
                    if (node2 != null) {
                   //         System.out.println("totaWordParts=" + totaWordParts);
                        node2.addTargetRelation(targetRelation);
                    }
                }
            }
        }

        for (Node node : index.values()) {
            node.sortResult();
        }

        stopWatch.stop();
        LOG.info("Done building TextSearchIndex... size=" + inputDatas.size() + " index size=" + index.size() +
                " Time elapsed=" + stopWatch.toString());


    }

    public List<T> lookup(String queryParam) {
        String q = StringUtils.trimToNull(queryParam);
        if (q == null) {
            return Collections.emptyList();
        }


        String term = StringUtils.deleteWhitespace(queryParam).toLowerCase();
        Node<T> node = index.get(term);

        if (node == null) {
            return Collections.emptyList();
        }

        List<T> values = node.getValues();

        if (values.size() > MAX_RESULT_SIZE) {
            return values.subList(0, MAX_RESULT_SIZE);
        }

        return values;
    }

    private static class Node<T> {
        final boolean rootNode;
        final String term;
        private List<T> targets;

        private final List<TargetRelation<T>> targetRelations = new ArrayList<>();

        Node(String term, TargetRelation<T> targetRelation) {
            this.rootNode = false;
            this.term = term;
            targetRelations.add(targetRelation);
        }

        void addTargetRelation(TargetRelation<T> targetRelation) {
            targetRelations.add(targetRelation);
        }

        List<T> getValues() {
            return new ArrayList<>(targets);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "rootNode=" + rootNode +
                    ", term='" + term + '\'' +
                    ", targets=" + getValues() +
                    '}';
        }

        void sortResult() {
            targets = new ArrayList<>();

            Collections.sort(targetRelations);

            for (TargetRelation<T> targetRelation : targetRelations) {
                T targetObject = (T) targetRelation.getTargetObject();

                if (targets.contains(targetObject)) {
                    continue;
                }

                targets.add(targetObject);
            }
        }
    }

    private class InputData {
        private final String text;
        private final Prio prio;
        private final T targetObject;

        InputData(String text, Prio prio, T targetObject) {
            this.text = text;
            this.prio = prio;
            this.targetObject = targetObject;
        }

        public String getText() {
            return text;
        }

        Prio getPrio() {
            return prio;
        }

        T getTargetObject() {
            return targetObject;
        }
    }

    private static class TargetRelation<T> implements Comparable<TargetRelation<T>> {
        private final T targetObject;
        private final int rank;

        @Override
        public int compareTo(TargetRelation<T> targetRelation) {
            return targetRelation.rank - rank;
        }

        TargetRelation(T targetObject, int rank) {
            this.targetObject = targetObject;
            this.rank = rank;
        }

        T getTargetObject() {
            return targetObject;
        }
    }
}
