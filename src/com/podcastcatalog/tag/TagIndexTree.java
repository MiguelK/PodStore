package com.podcastcatalog.tag;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

public class TagIndexTree<T> implements Serializable {

    private final static Logger LOG = Logger.getLogger(TagIndexTree.class.getName());

    private static final int MAX_RESULT_SIZE = 50;
    private final Map<String, Node<T>> index;

    private final List<InputData> inputDatas;
    // private static final int MAX_WORDS_TO_INDEX = 4;
    // private int maxWordsToIndex  = MAX_WORDS_TO_INDEX;

    public String getStatus() {
        return "textSearchIndex=" + index.size();
    }

    void printIndex() {
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

    public TagIndexTree() {
        inputDatas = new ArrayList<>();
        index = new TreeMap<>();
    }

    public void addText(String text, T targetObject) {
        inputDatas.add(new InputData(StringUtils.trimToEmpty(text).toLowerCase(), targetObject));
    }

    private static final int MAX_CHARACTERS_LENGTH_TO_INDEX = 7;

    public void buildIndex() {

        LOG.info("Start building TextSearchIndex... size=" + inputDatas.size());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (InputData inputData : inputDatas) {
            String text = inputData.getText();
            String inputTextPrefix = text.length() > MAX_CHARACTERS_LENGTH_TO_INDEX ? text.substring(0, MAX_CHARACTERS_LENGTH_TO_INDEX - 1) : text;

            List<String> allWordsInInputText = Arrays.asList(StringUtils.split(text));

            TargetRelation<T> targetRelation = new TargetRelation<>(inputData.getTargetObject(), 3000); //FIXME max

            Node<T> node = index.putIfAbsent(inputTextPrefix, new Node<>(inputTextPrefix, targetRelation));
            if (node != null) {
                //Ok 2 or more inputTextPrefix are the same pointing to different targets.?
                //   System.out.println("node=" + inputTextPrefix);
                node.addTargetRelation(targetRelation);
            }

            // String totalWordParts = ""; //e.g Fallet Pe
            //TargetRelation ->

            //sommar,i,peking //maxCharacterLengthPart
           // int maxCharacterLengthPart = 7; //e.g sommaripeki
            //Fallet Peter Mangs i Sverige  abc

            int size = allWordsInInputText.size();
            for (int i = 0; i < size; i++) {
                String currentWord = allWordsInInputText.get(i);

                int next = i + 1;
                String nextWord = null;
                if (next < size) {
                    nextWord = allWordsInInputText.get(next);
                }

                if (nextWord != null) {
                    String mergedWords = currentWord;

                    char[] chars = nextWord.toCharArray();
                    for (int j = 0; j < chars.length; j++) {

                        if (j >= 3) { //FIXME max merge to right
                            break;
                        }

                        char aChar = chars[j];

                        mergedWords += aChar;

                        Node<T> node2 = index.putIfAbsent(mergedWords, new Node<>(mergedWords, targetRelation));

                        if (node2 != null) {
                            //         System.out.println("totalWordParts=" + totalWordParts);
                            node2.addTargetRelation(targetRelation);
                        }
                    }
                }

                String wordCharacters = "";

                for (char c : currentWord.toCharArray()) {
                    wordCharacters += c;
                    Node<T> node1;

                    if (wordCharacters.equals(currentWord)) {
                     //   rank += 600; //FIXME ?
                        node1 = index.putIfAbsent(wordCharacters, new Node<>(wordCharacters, new TargetRelation<>(inputData.getTargetObject(), 2000)));
                    } else {
                        node1 = index.putIfAbsent(wordCharacters, new Node<>(wordCharacters, new TargetRelation<>(inputData.getTargetObject(), 500)));
                    }

                    if (node1 != null) {
                        //        System.out.println("Term=" + term);
                        node1.addTargetRelation(targetRelation);
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

    private static class Node<T> implements  Serializable {
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

    private class InputData implements  Serializable {
        private final String text;

        private final T targetObject;

        InputData(String text,  T targetObject) {
            this.text = text;
            this.targetObject = targetObject;
        }

        public String getText() {
            return text;
        }

        T getTargetObject() {
            return targetObject;
        }
    }

    private static class TargetRelation<T> implements Comparable<TargetRelation<T>>, Serializable {
        private final T targetObject;
        private  int rank;

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
