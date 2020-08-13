package pl.fhframework.core.generator;

import org.springframework.data.util.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for resolving binding for i18n messages.
 */
public class I18nBindingResolver {

    /**
     * If binding contains bundle name and key it will return full pair, but if expression does not
     * contain bundle it will return empty bundle and only key.
     *
     * @param expressionForComment - binding
     * @return pair of bundle and key
     */
    public static Pair<String, String> getBundleAndKeyFrom(String expressionForComment) {
        Pattern patternWithBundle = Pattern.compile("^\\$(\\w+)\\.([\\w\\.]+)$");
        Pattern patternWithoutBundle = Pattern.compile("^\\$\\.([\\w\\.]+)$");
        Matcher bindingBundleMatcher = patternWithBundle.matcher(expressionForComment);
        Matcher bindingWithoutBundleMatcher = patternWithoutBundle.matcher(expressionForComment);

        Pair<String, String> bundleAndKey = null;
        if (bindingBundleMatcher.find()) {
            String bundle = bindingBundleMatcher.group(1); //not used for now
            String key = bindingBundleMatcher.group(2);
            bundleAndKey = Pair.of(bundle, key);
        } else if (bindingWithoutBundleMatcher.find()) {
            String key = bindingWithoutBundleMatcher.group(1);
            bundleAndKey = Pair.of("", key);
        }
        return bundleAndKey;
    }

}
