package com.podcastcatalog.tag.swedish;

import com.podcastcatalog.tag.TextAnayzier;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class SwedishTextAnalyzer extends TextAnayzier {
    private static final String SWEDISH_DICTIONARY_FILE = "Swedish.txt";
    private static final String SWEDISH_SYNONYM_FILE = "Synonymer.txt";

    private static Map<String, Integer> LOWER_CASE_SYNONYM_WORDS;

    private static HashMap<String, Boolean> IGNORE_WORDS;

    static {
        InputStream resourceAsStream =
                SwedishTextAnalyzer.class.getResourceAsStream(SWEDISH_DICTIONARY_FILE);


        try {
           //IGNORE_WORDS = IOUtils.readLines(resourceAsStream, StandardCharsets.UTF_8);

            Scanner scanner = new Scanner(resourceAsStream, StandardCharsets.UTF_16.name());

            IGNORE_WORDS = new HashMap<String, Boolean>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                IGNORE_WORDS.put(line, true);
            }

            System.out.println("SWEDISH_DICTIONARY_FILE: Words " + IGNORE_WORDS.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            List<String> rows = IOUtils.readLines(SwedishTextAnalyzer.class.getResourceAsStream(SWEDISH_SYNONYM_FILE));

            LOWER_CASE_SYNONYM_WORDS = new HashMap<String, Integer>();
            int i = 0;
            for (String synonymWordRow : rows) {
                String[] split = StringUtils.split(synonymWordRow);

                for (String synonymWord : split) {
                    LOWER_CASE_SYNONYM_WORDS.put(synonymWord, i);
                }
                i++;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public static SwedishTextAnalyzer create(String text) {
        return new SwedishTextAnalyzer(text);
    }*/

    protected Map<String, Integer> getLowerCaseSynonymWords() {
        return LOWER_CASE_SYNONYM_WORDS;
    }

    @Override
    protected boolean isValidWord(String word) {
        boolean contains = IGNORE_WORDS.containsKey(word);
        if(contains) {
           // System.out.println("contains " + word);
        }
        return !contains;
    }

}
