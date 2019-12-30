package com.podcastcatalog.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by miguelkrantz on 2018-12-11.
 */
public class IdGenerator {

    private static final String DO_NOT_CONVERT_CHARACTERS = "0123456789abcdefghijklmnopqrstuvzyx";

    private static final String VALID_CHARACTERS = "0123456789";
    private static final String INVALID_CHARACTERS = "åÅäÄöÖ";

    private static final List<Character> VALID_CHARACTER_LIST = new ArrayList<>();

    static {

        for (int i = 0; i <= 65535; i++) {
            char unicode = (char) i;


            String character = "" + unicode;
            if(StringUtils.isAlphanumeric(character) && !INVALID_CHARACTERS.contains(character)) {
                VALID_CHARACTER_LIST.add(unicode);
            }
        }

        for (char c : VALID_CHARACTERS.toCharArray()) {
            VALID_CHARACTER_LIST.add(c);
        }
    }

    public static String generate(String podCastEpisodeTitle, String podCastCollectionId) {

        List<Character> title = podCastEpisodeTitle.toLowerCase().chars().mapToObj(e->(char)e).collect(Collectors.toList());

        String s1 = title.stream().filter(VALID_CHARACTER_LIST::contains)
                .collect(StringBuilder::new,StringBuilder::appendCodePoint,StringBuilder::append)
                .toString();

        StringBuilder encodedString = new StringBuilder();
        for (char c : s1.toCharArray()) {

            if(DO_NOT_CONVERT_CHARACTERS.contains("" + c)){
                encodedString.append(c);
                continue;
            }
            encodedString.append((int)c);
        }


        return (podCastCollectionId + "-" + encodedString.toString()).trim();
    }
}
