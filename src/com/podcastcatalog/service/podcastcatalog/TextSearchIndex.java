package com.podcastcatalog.service.podcastcatalog;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class TextSearchIndex<T> {

    private static final int MAX_RESULT_SIZE = 50;
    private final Map<String, Node<T>> index;

    private final List<InputData> inputDatas;
    private static final int MAX_WORDS_TO_INDEX = 4;
    private int maxWordsToIndex  = MAX_WORDS_TO_INDEX;
    private int maxCharactersLengthToIndex;

    public String getStatus() {
        return "TextSearchIndex: indexSize=" + index.size();
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

    public TextSearchIndex(int maxWordsToIndex) {
        inputDatas = new ArrayList<>();
        index = new HashMap<>();
        this.maxWordsToIndex = maxWordsToIndex;
    }

    public TextSearchIndex() {
        inputDatas = new ArrayList<>();
        index = new HashMap<>();
    }

    public void addText(String text, Prio prio, T targetObject) {
        inputDatas.add(new InputData(StringUtils.trimToEmpty(text).toLowerCase(), prio, targetObject));
    }

    public void buildIndex() {

        for (InputData inputData : inputDatas) {
            String text = inputData.getText();
            List<String> words = Arrays.asList(StringUtils.split(text));

            if (words.size() > maxWordsToIndex && maxWordsToIndex>1) {
                words = words.subList(0, maxWordsToIndex - 1);
                int totalWordsEndOffset = text.indexOf(words.get(maxWordsToIndex - 2)) + 1;
                text = text.substring(0, totalWordsEndOffset);
            }

            /*if (words.size() == 1) {
                words = words.subList(0, 1);
                int x = text.indexOf(words.get(0)) + 1;
                text = text.substring(0, x);
            }*/ //FIXME


            int rank = inputData.getPrio().getRank();
            Node<T> node = index.putIfAbsent(text, new Node<>(text, new TargetRelation<>(inputData.getTargetObject(), rank)));
            if (node != null) {
               // System.out.printf("node=" + text);
                node.addTargetRelation(new TargetRelation<>(inputData.getTargetObject(), rank));
            }

            String allWordTerms = ""; //e.g Fallet Pe

            for (String word : words) {
                String term = ""; //

                Node<T> node1;
                Node<T> node2;

                for (char c : word.toCharArray()) {
                    term += c;
                    allWordTerms += c;

                    if (term.equals(word)) {
                        rank += 600; //FIXME ?
                    }

                    node1 = index.putIfAbsent(term, new Node<>(term, new TargetRelation<>(inputData.getTargetObject(), rank)));
                    if (node1 != null) {
                        //        System.out.println("Term=" + term);
                        node1.addTargetRelation(new TargetRelation<>(inputData.getTargetObject(), rank));
                    }
                    node2 = index.putIfAbsent(allWordTerms, new Node<>(allWordTerms, new TargetRelation<>(inputData.getTargetObject(), rank)));
                    if (node2 != null) {
                   //         System.out.println("allWordTerms=" + allWordTerms);
                        node2.addTargetRelation(new TargetRelation<>(inputData.getTargetObject(), rank));
                    }
                }
            }
        }

        for (Node node : index.values()) {
            node.sortResult();
        }

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
