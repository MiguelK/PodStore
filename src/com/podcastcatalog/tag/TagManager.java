package com.podcastcatalog.tag;

import com.podcastcatalog.model.podcastcatalog.PodCast;
import com.podcastcatalog.model.podcastcatalog.PodCastEpisode;
import com.podcastcatalog.tag.model.Tag;
import com.podcastcatalog.tag.swedish.SwedishTextAnalyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagManager {


    enum Lang {
        SWE
    }

    static final TagManager INSTANCE = new TagManager();

    public TagSearchResult search(String s) {

        return new TagSearchResult();
    }

    public Tag getTagForPodCast(String pid) {
        return null;
    }


    public Tag getTagForPodCastEpisode(String eid) {
        return null;
    }


    public static TagManager getInstance() {
        return INSTANCE;
    }

    private Map<Lang, TagIndexTree> tagIndex = new HashMap<>();
    private Map<Lang, TextAnayzier> textAnayziers = new HashMap<>();

    public void configure(Lang lang) {
        tagIndex.put(lang, new TagIndexTree());
        textAnayziers.put(lang, new SwedishTextAnalyzer());
    }

    public void index(PodCast podCast, Lang lang) {

        TagIndexTree tagIndexTree = this.tagIndex.get(lang);
        TextAnayzier textAnayzier = textAnayziers.get(lang);

        List<TextAnayzier.Word> words = textAnayzier.parseWords(podCast.getTitle());
        System.out.println("Words:" + words.size());

        for (TextAnayzier.Word word : words) {
            System.out.println(word);
            //tagIndexTree.addText();
        }

        for (PodCastEpisode podCastEpisode : podCast.getPodCastEpisodesInternal()) {
            List<TextAnayzier.Word> w = textAnayzier.parseWords(podCastEpisode.getTitle() + podCastEpisode.getDescription());
            System.out.println("Episode Words:" + w.size());

            for (TextAnayzier.Word word : w) {
                System.out.println(word);
                tagIndexTree.addText(word.toString(), podCastEpisode);
            }
            tagIndexTree.buildIndex();
        }


    }
}
