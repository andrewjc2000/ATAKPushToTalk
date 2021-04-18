package com.atakmap.android.pushToTalk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A collection of static maps, in addition to a static helper method used to replace
 * all instances of phrases with particular mapped replacements
 * @author achafos3, reshed3
 * @version 1.0
 */
public class TextSubstitution {
    /**
     * Static String arrays used to initialize the translation maps defined below
     */
    private static final String[] phoeneticAlphabet = {
        "alpha", "alfa", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel", "india",
        "juliett", "juliet", "kilo", "lima", "mike", "mic", "november", "oscar", "papa", "quebec",
        "romeo", "sierra", "tango", "uniform", "victor", "whiskey", "x ray", "x-ray", "x ray",
        "ex ray", "yankee", "zulu"
    };
    private static final String[] numbers = {
        "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
    };

    /**
     * Constant Mapping that maps all of the words in the phonetic alphabet to the letters
     * they represent. Deals with a few spelling variations on words in the phonetic alphabet
     * when appropriate.
     */
    public static final Map<String, String> PHONETIC_MAPPING = new HashMap<>();
    /**
     * Constant Mapping that maps all of the English words for the digits from 0 to 9
     * to the single-character digits they represent in the Arabic numeral system.
     */
    public static final Map<String, String> NUMBER_MAPPING = new HashMap<>();
    static {
        for (String phonetic: phoeneticAlphabet) {
            PHONETIC_MAPPING.put(phonetic, phonetic.substring(0, 1).toUpperCase());
        }
        for (int i = 0;i < numbers.length; i++) {
            NUMBER_MAPPING.put(numbers[i], String.valueOf(i));
        }
    }

    /**
     * For every mapping of phrases to their replacements, replaces each instance of each phrase
     * in the mapping with its replacement.
     * @param original the String to be modified
     * @param translations A List of mappings from phrases to their replacements
     * @return a String modified as described above
     */
    public static String convertWordsToShortcuts(String original,
                                                 List<Map<String, String>> translations) {
        for (Map<String, String> translationMap: translations) {
            for(String translationKey: translationMap.keySet()) {
                // this regex ensures case is ignored when replacing
                original = original.replaceAll(
                    "(?i)" + translationKey, translationMap.get(translationKey)
                );
            }
        }
        return original;
    }

}
