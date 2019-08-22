package com.podcastcatalog.tag;

import com.podcastcatalog.tag.model.TagIndex;
import com.podcastcatalog.tag.swedish.SwedishTextAnalyzer;

import java.util.List;

public class TagManager {

    static final TagManager INSTANCE = new TagManager();

    public static TagManager getInstance() {
        return INSTANCE;
    }

    private TagIndex tagIndex = new TagIndex();

    public void createTagIndex(String lang) {
        tagIndex = new TagIndex();
    }

    public void indexContent(String content) {

        TextAnayzier textAnayzier = new SwedishTextAnalyzer(content);
        List<TextAnayzier.Word> words = textAnayzier.getWords();

        System.out.println("Words:" + words.size());
        for (TextAnayzier.Word word : words) {
            System.out.println(word);
        }
    }
}
