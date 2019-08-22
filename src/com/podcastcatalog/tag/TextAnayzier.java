package com.podcastcatalog.tag;

import com.podcastcatalog.tag.human_name_parser.HumanNameParserParser;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public abstract class TextAnayzier {

    private final List<Word> words;

    protected TextAnayzier(String text) {
        this.words = parseWords(text);
    }

    /*public int getSimilarTextCount(TextAnayzier textAnayzier) {
        List<Word> parts1 = textAnayzier.getParts();

        List<String> intersection = new ArrayList<String>();

        //FIXME
        if (parts1.size() > words.size()) {
            for (Word word : parts1) {
                if (words.contains(word)) {
                    intersection.add(word.word);
                }
            }

        } else {
            for (Word word : words) {
                if (parts1.contains(word)) {
                    intersection.add(word.word);
                }
            }
        }

        return intersection.size();     //FIXME
    }

    public boolean isSimilarText(TextAnayzier textAnayzier) {
        return  getSimilarTextCount(textAnayzier) > 0;     //FIXME
    }*/

    public List<Word> getWords() {
        return words;
    }

    private static final int NOT_FOUND = -1;

    private int lookupSynonymId(String word) {

        //Map<String word,Integer> s
        Map<String, Integer> synonymWords = getLowerCaseSynonymWords();

        Integer w = synonymWords.get(word);
        if (w == null) {
            //   System.out.println("Lookup not found " + w + " " + word + " " + synonymWords);
            return NOT_FOUND;
        }

        return w;
    }

    class Word {
        private final String word;

        private Word(String word) {
            this.word = word;
        }

        public int getSynonymId() {
            return lookupSynonymId(word);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Word otherWord = (Word) o;

            int lookupSynonymId = lookupSynonymId(word);
            if (lookupSynonymId != NOT_FOUND &&
                    otherWord.getSynonymId() != NOT_FOUND &&
                    otherWord.getSynonymId() == lookupSynonymId) {
                return true;
            }

            if (word != null ? !word.equals(otherWord.word) : otherWord.word != null) {
                //   System.out.println("Not equal " + lookupSynonymId + " " + word);
                return false;   //FIXME hashcode
            }
            return true;
        }

        @Override
        public String toString() {
            return "Word: " + word;
        }

        @Override
        public int hashCode() {
            return word != null ? word.hashCode() : 0;
        }
    }

    private List<Word> getParts() {
        return words;
    }

    private List<Word> parseWords(String text) {
        String trimmedLowerCaseText = StringUtils.trimToEmpty(text).toLowerCase();
        if (StringUtils.isEmpty(trimmedLowerCaseText)) {
            return Collections.emptyList();
        }

       // HumanNameParserParser humanNameParserParser = new HumanNameParserParser(trimmedLowerCaseText);
        // System.out.println("humanNameParserParser= " + humanNameParserParser.getFirst()
        //      + " " + humanNameParserParser.getLast());

        Scanner scanner = new Scanner(trimmedLowerCaseText);

        List<Word> words = new ArrayList<Word>();
        while (scanner.hasNext()) {
            String next = scanner.next();



            if(isValidWord(next)) {
                words.add(new Word(next));
            }
        }

        return words;
    }

    protected abstract Map<String, Integer> getLowerCaseSynonymWords();
    protected abstract boolean isValidWord(String word);

}
