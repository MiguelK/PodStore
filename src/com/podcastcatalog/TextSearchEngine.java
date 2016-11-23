package com.podcastcatalog;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class TextSearchEngine<T> {

    private static final int MAX_RESULT_SIZE = 50;
    private final Map<String, Node> index;

    private final List<InputData> inputDatas;

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

    public TextSearchEngine() {
        inputDatas = new ArrayList<>();
        index = new HashMap<>();
    }

    public void addText(String text, Prio prio, T targetObject) {
        //FIXME validate
        String s = StringUtils.trimToEmpty(text).toLowerCase();
        inputDatas.add(new InputData(s, prio, targetObject));
    }

    public void buildIndex() {
        //FIXME validate...

        for (InputData inputData : inputDatas) {
            String text = inputData.getText();
            List<String> words = Arrays.asList(StringUtils.split(text));

            int rank = inputData.getPrio().getRank();
            Node node11 = index.putIfAbsent(text, new Node<T>(false, text, new TargetRelation<T>(inputData.getTargetObject(), rank)));
            if (node11 != null) {
                node11.addTargetRelation(new TargetRelation<T>(inputData.getTargetObject(), rank));
            }

            for (String word : words) {
                String term = "";
                Node node1;
                for (char c : word.toCharArray()) {
                    term += c;

                    if (term.equals(word)) {
                        rank += 600;
                    }

                    node1 = index.putIfAbsent(term, new Node<T>(false, term, new TargetRelation<T>(inputData.getTargetObject(), rank)));
                    if (node1 != null) {
                        node1.addTargetRelation(new TargetRelation<T>(inputData.getTargetObject(), rank));
                    }
                }
            }
        }

        for (Node node : index.values()) {
            node.sortResult();
        }

//        System.out.println("Index size= " + index.size());
    }

    public List<T> lookup(String q) {
        String s = StringUtils.trimToNull(q);
        if (s == null) {
            return Collections.emptyList();
        }

//        int levenshteinDistance = StringUtils.getLevenshteinDistance(q, "Terrorn i Paris centrum.");
//        System.out.println("A=" + levenshteinDistance);

        String term = s.toLowerCase();
        Node node = index.get(term);

        if (node == null) {
            return Collections.emptyList();
        }

        System.out.println(term + " Node == " + node);

        //pac -> Paris centrum
        //Node node = Nodes.lookup(w)

        List values = node.getValues();

        if (values.size() > MAX_RESULT_SIZE) {
            return values.subList(0, MAX_RESULT_SIZE);


        }

        return values;
    }

    //Terrorn i Paris centrum
    //Parkeringsplatsen i Malmö
    // SearchTernm= "Par"       //Node: term="par" partOffset=
    // SearchTernm= "Par"       //Node: term="par" partOffset=
    private static class Node<T> {
        final boolean rootNode; //rootTextPart, rootTextFull
        final String term; //"a"
        private boolean wordMatch;

        //        int partOffset; //Max =20 antal hop till fullt ord, offset into chidlrens?
//        List<Node> word = new ArrayList<Node>();
//        List<Node> childrens = new ArrayList<Node>();
//        List<Node> partsAfter = new ArrayList<Node>(); //Jagad av staten //All full word after
//        List<Node> partsBefore = new ArrayList<Node>(); //Jagad av staten //All full word before
        //        List<T> ts
//        private Set<T> targets = new HashSet<T>();
        private List<T> targets;

        private final List<TargetRelation<T>> targetRelations = new ArrayList<>();

        Node(boolean rootNode, String term, TargetRelation<T> targetRelation) {
            this.rootNode = rootNode;
            this.term = term;
            targetRelations.add(targetRelation);
        }

        void addTargetRelation(TargetRelation<T> targetRelation) {
            targetRelations.add(targetRelation);
        }

        List<T> getValues() {
            return new ArrayList<T>(targets);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "rootNode=" + rootNode +
                    ", term='" + term + '\'' +
                    ", wordMatch='" + wordMatch + '\'' +
                    ", targets=" + getValues() +
                    '}';
        }

        public void sortResult() {
            targets = new ArrayList<T>();

            Collections.sort(targetRelations);

            for (TargetRelation targetRelation : targetRelations) {
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

        public InputData(String text, Prio prio, T targetObject) {
            this.text = text;
            this.prio = prio;
            this.targetObject = targetObject;
        }

        public String getText() {
            return text;
        }

        public Prio getPrio() {
            return prio;
        }

        public T getTargetObject() {
            return targetObject;
        }
    }

    private static class TargetRelation<T> implements Comparable<TargetRelation<T>> {
        private final T targetObject;
        private int rank;

        @Override
        public int compareTo(TargetRelation<T> targetRelation) {
            return targetRelation.rank - rank;
        }

        public TargetRelation(T targetObject, int rank) {
            this.targetObject = targetObject;
            this.rank = rank;
        }

        public T getTargetObject() {
            return targetObject;
        }
    }
}
