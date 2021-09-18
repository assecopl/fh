package pl.fhframework.core.util;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Piotr on 2017-02-01.
 */
public class StringUtils {

    public static boolean containsIgnoreCase(String text, String fragment) {
        if (text == null || fragment == null) {
            return false;
        }
        return text.toLowerCase().contains(fragment.toLowerCase());
    }

    public static boolean startsWithIgnoreCase(String text, String fragment) {
        if (text == null || fragment == null) {
            return false;
        }
        return text.toLowerCase().startsWith(fragment.toLowerCase());
    }

    public static boolean endsWithIgnoreCase(String text, String fragment) {
        if (text == null || fragment == null) {
            return false;
        }
        return text.toLowerCase().endsWith(fragment.toLowerCase());
    }

    public static boolean equalsIgnoreCase(String text, String fragment) {
        if (text == null && fragment == null) {
            return true;
        }
        if (text == null || fragment == null) {
            return false;
        }
        return text.equalsIgnoreCase(fragment.toLowerCase());
    }

    public static boolean contains(String text, String fragment) {
        if (text == null || fragment == null) {
            return false;
        }
        return text.contains(fragment);
    }

    public static boolean startsWith(String text, String fragment) {
        if (text == null || fragment == null) {
            return false;
        }
        return text.startsWith(fragment);
    }

    public static boolean endsWith(String text, String fragment) {
        if (text == null || fragment == null) {
            return false;
        }
        return text.endsWith(fragment);
    }

    public static boolean equals(String text, String fragment) {
        return Objects.equals(text, fragment);
    }

    public static String repeat(String text, int times) {
        if (isNullOrEmpty(text)) {
            return text;
        }

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < times; i++) {
            output.append(text);
        }
        return output.toString();
    }

    public static boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public static boolean hasText(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static String joinWithEmpty(Collection<String> texts, String separator) {
        return join(texts, separator, true);
    }

    public static String joinWithoutEmpty(Collection<String> texts, String separator) {
        return join(texts, separator, false);
    }

    public static String join(Collection<String> texts, String separator, boolean keepEmpty) {
        if (texts == null) {
            return null;
        }
        return texts.stream()
                .filter(s -> keepEmpty || !isNullOrEmpty(s)) // maybe ommit empty strings
                .collect(Collectors.joining(separator)); // join using separator
    }

    public static List<String> splitToList(String text, String separator) {
        if (isNullOrEmpty(text)) {
            return Collections.emptyList();
        }
        return Arrays.asList(text.split(Pattern.quote(separator)));
    }

    public static Set<String> splitToSet(String text, String separator) {
        if (isNullOrEmpty(text)) {
            return Collections.emptySet();
        }
        return Arrays.stream(text.split(separator)).collect(Collectors.toSet());
    }

    public static String nullToEmpty(String text) {
        if (isNullOrEmpty(text)) {
            return "";
        } else {
            return text;
        }
    }

    public static String firstLetterToUpper(String text) {
        if (isNullOrEmpty(text)) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static String firstLetterToLower(String text) {
        if (isNullOrEmpty(text)) {
            return text;
        }
        return text.substring(0, 1).toLowerCase() + text.substring(1);
    }

    public static boolean hasSurroundingBraces(String text) {
        return text != null && text.startsWith("{") && text.endsWith("}");
    }

    public static String removeSurroundingBraces(String text) {
        if (hasSurroundingBraces(text)) {
            return text.substring(1, text.length() - 1);
        } else {
            return text;
        }
    }

    public static String removeSurroundingCharacters(String text) {
        if (text != null && text.length() > 1) {
            return text.substring(1, text.length() - 1);
        } else {
            return text;
        }
    }

    public static List<Character> explode(String charsInString) {
        List<Character> exploded = new ArrayList<>(charsInString.length());
        for (char singleChar : charsInString.toCharArray()) {
            exploded.add(singleChar);
        }
        return exploded;
    }

    public static String emptyToNull(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        } else {
            return text;
        }
    }

    /**
     * Null safe equality check
     * @param aS1 first string
     * @param aS2 second string
     * @return TRUE iif aS1 is equal to aS2
     */
    public static boolean equal(String aS1, String aS2) {
        if ( aS1 == null && aS2 != null ) return false;
        if ( aS1 != null && aS2 == null ) return false;

        if ( aS1 == null && aS2 == null ) return true; // aS2 == null is satified here, left for code clarity

        return aS1.equals(aS2);
    }

    /**
     * Remove Accents and Diacritics from String
     * @param str string
     * @return latinized string
     */
    public static String latinize(String str) {
        return str == null ? null : Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").replaceAll("ł","l").replaceAll("Ł","L");
    }

    /**
     * Utility method to take a string and convert it to normal Java variable
     * name capitalization.  This normally means converting the first
     * character from upper case to lower case, but in the (unusual) special
     * case when there is more than one character and both the first and
     * second characters are upper case, we leave it alone.
     * <p>
     * Thus "FooBah" becomes "fooBah" and "X" becomes "x", but "URL" stays
     * as "URL".
     *
     * @param  name The string to be decapitalized.
     * @return  The decapitalized version of the string.
     */
    public static String decapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
                Character.isUpperCase(name.charAt(0))){
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public static int countLines(CharSequence str) {
        return countChars(str, '\n') + 1;
    }

    public static int countChars(CharSequence str, Character character) {
        if(str == null)
        {
            return 0;
        }
        return (int) str.chars().filter(x -> x == character).count();
    }

    public static int countSequence(String str, String sequence) {
        if(str == null)
        {
            return 0;
        }

        int startIdx = 0;
        int count = 0;
        while (startIdx < str.length()) {
            int currIdx = str.indexOf(sequence, startIdx);
            if (currIdx > -1) {
                count++;
            }
            else {
                break;
            }
            startIdx = currIdx + sequence.length();
        }

        return count;
    }

    public static String limit(String input, int length) {
        if (input.length() < length) {
            return input;
        }
        return input.substring(0, length - 3) + "...";
    }

    public static String nvl(String... values) {
        for (String value : values) {
            if (!isNullOrEmpty(value)) {
                return value;
            }
        }
        return null;
    }

    public static String evl(String... values) {
        for (String value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
