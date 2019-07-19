package pl.fhframework.core.rules.builtin;

import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.core.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Created by pawel.ruta on 2017-11-20.
 */
@BusinessRule(categories = "string")
public class StringRuleUtils {
    public boolean stringIsEmpty(String str) {
        return StringUtils.isNullOrEmpty(str);
    }

    public boolean stringIsBlank(String str) {
        return !StringUtils.hasText(str);
    }

    public String stringTrim(String str) {
        return str == null ? null : str.trim();
    }

    public boolean stringEquals(String str1, String str2) {
        return StringUtils.equals(str1, str2);
    }

    public boolean stringEqualsIgnoreCase(String str1, String str2) {
        return StringUtils.equalsIgnoreCase(str1, str2);
    }

    public int stringIndexOf(String str, String searchStr) {
        return str != null && searchStr != null ? str.indexOf(searchStr) : -1;
    }

    public int stringIndexOf(String str, String searchChar, int startPos) {
        return stringIsEmpty(str) ? -1 : str.indexOf(searchChar, startPos);
    }

    public int stringLastIndexOf(String str, String searchStr) {
        return str != null && searchStr != null ? str.lastIndexOf(searchStr) : -1;
    }

    public int stringLastIndexOf(String str, String searchStr, int startPos) {
        return str != null && searchStr != null ? str.lastIndexOf(searchStr, startPos) : -1;
    }

    public boolean stringContains(String str, String searchStr) {
        return StringUtils.contains(str, searchStr);
    }

    public boolean stringContainsIgnoreCase(String str, String searchStr) {
        return StringUtils.containsIgnoreCase(str, searchStr);
    }

    public String stringSubstring(String str, int start) {
        if (str == null) {
            return null;
        } else {
            if (start < 0) {
                start += str.length();
            }

            if (start < 0) {
                start = 0;
            }

            return start > str.length() ? "" : str.substring(start);
        }
    }

    public String stringSubstring(String str, int start, int end) {
        if (str == null) {
            return null;
        } else {
            if (end < 0) {
                end += str.length();
            }

            if (start < 0) {
                start += str.length();
            }

            if (end > str.length()) {
                end = str.length();
            }

            if (start > end) {
                return "";
            } else {
                if (start < 0) {
                    start = 0;
                }

                if (end < 0) {
                    end = 0;
                }

                return str.substring(start, end);
            }
        }
    }

    public List<String> stringSplit(String str, String separator) {
        return StringUtils.splitToList(str, separator);
    }

    public String stringJoin(Collection<String> texts, String separator) {
        return StringUtils.join(texts, separator, true);
    }

    public String stringReplaceRegex(String str, String regex, String replacement) {
        return str == null ? null : str.replaceAll(regex, replacement);
    }

    public String stringReplace(String str, String target, String replacement) {
        return str == null ? null : str.replace(target, replacement);
    }

    public boolean stringMatches(String str, String regex) {
        return str != null && str.matches(regex);
    }

    public int stringLength(String str) {
        return str == null ? 0 : str.length();
    }

    public String stringToUpperCase(String str) {
        return str == null ? null : str.toUpperCase();
    }

    public String stringToLowerCase(String str) {
        return str == null ? null : str.toLowerCase();
    }

    public String stringCapitalize(String str) {
        return StringUtils.firstLetterToUpper(str);
    }

    public String stringUncapitalize(String str) {
        return StringUtils.firstLetterToLower(str);
    }

    public String stringSwapCase(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            StringBuilder buffer = new StringBuilder(strLen);
            for (int i = 0; i < strLen; ++i) {
                char ch = str.charAt(i);
                if (Character.isUpperCase(ch)) {
                    ch = Character.toLowerCase(ch);
                } else if (Character.isTitleCase(ch)) {
                    ch = Character.toLowerCase(ch);
                } else if (Character.isLowerCase(ch)) {
                    ch = Character.toUpperCase(ch);
                }

                buffer.append(ch);
            }

            return buffer.toString();
        } else {
            return str;
        }
    }

    public boolean stringStartsWith(String str, String fragment) {
        return StringUtils.startsWith(str, fragment);
    }

    public boolean stringEndsWith(String str, String fragment) {
        return StringUtils.endsWith(str, fragment);
    }

    public boolean stringStartsWithIgnoreCase(String str, String fragment) {
        return StringUtils.startsWithIgnoreCase(str, fragment);
    }

    public boolean stringEndsWithIgnoreCase(String str, String fragment) {
        return StringUtils.endsWithIgnoreCase(str, fragment);
    }

    public String stringCharAt(String str, int index) {
        if (stringIsEmpty(str) || index >= str.length() || index < 0) {
            return null;
        }

        return String.valueOf(str.charAt(index));
    }

    public boolean stringIsLetter(String str) {
        return stringIs(str, Character::isLetter);
    }

    public boolean stringIsLetterAt(String str, int index) {
        return stringIsAt(str, index, Character::isLetter);
    }

    public boolean stringIsDigit(String str) {
        return stringIs(str, Character::isDigit);
    }

    public boolean stringIsDigitAt(String str, int index) {
        return stringIsAt(str, index, Character::isDigit);
    }

    public boolean stringIsWhitespace(String str) {
        return stringIs(str, Character::isWhitespace);
    }

    public boolean stringIsWhitespaceAt(String str, int index) {
        return stringIsAt(str, index, Character::isWhitespace);
    }

    public String stringLeft(String str, int len) {
        return str == null ? null : (len < 0 ? "" : (str.length() <= len ? str : str.substring(0, len)));
    }

    public String stringRight(String str, int len) {
        return str == null ? null : (len < 0 ? "" : (str.length() <= len ? str : str.substring(str.length() - len)));
    }

    public String stringMiddle(String str, int pos, int len) {
        if (str == null) {
            return null;
        } else if (len >= 0 && pos <= str.length()) {
            if (pos < 0) {
                pos = 0;
            }

            return str.length() <= pos + len ? str.substring(pos) : str.substring(pos, pos + len);
        } else {
            return "";
        }
    }

    public String stringRepeat(String str, int times) {
        return StringUtils.repeat(str, times);
    }

    public String stringInsert(String str, int atLocation, String phrase) {
        if (stringIsEmpty(str)) {
            return phrase;
        }
        if (stringIsEmpty(phrase)) {
            return str;
        }
        if (str.length() <= atLocation) {
            return str + phrase;
        }
        if (atLocation < 0) {
            atLocation = 0;
        }
        return str.substring(0, atLocation) + phrase + str.substring(atLocation);
    }

    private boolean stringIs(String str, Function<Character, Boolean> predicate) {
        if (stringIsEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!predicate.apply(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean stringIsAt(String str, int index, Function<Character, Boolean> predicate) {
        if (stringIsEmpty(str) || index >= str.length() || index < 0) {
            return false;
        }
        return predicate.apply(str.charAt(index));
    }
}


