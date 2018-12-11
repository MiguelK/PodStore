package com.podcastcatalog.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by miguelkrantz on 2018-12-11.
 */
public class IdGenerator {

    private static final String VALID_CHARACTERS = "abcdefghijklmnopqrstuvzyx";

    private static final List<Character> VALID_CHARACTER_LIST = new ArrayList<>();

    static {
        for (char c : VALID_CHARACTERS.toCharArray()) {
            VALID_CHARACTER_LIST.add(c);
        }
    }

    public static String generate(String podCastEpisodeTitle, String podCastCollectionId) {

        List<Character> title = podCastEpisodeTitle.toLowerCase().chars().mapToObj(e->(char)e).collect(Collectors.toList());
        String s1 = title.stream().filter(VALID_CHARACTER_LIST::contains)
                .collect(StringBuilder::new,StringBuilder::appendCodePoint,StringBuilder::append)
                .toString();


        return podCastCollectionId + "-" + s1;
    }
}
