package pl.fhframework.binding;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import lombok.Getter;
import pl.fhframework.core.forms.iterators.IIndexedBindingOwner;
import pl.fhframework.core.forms.iterators.IRepeatableIteratorInfo;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.BindingResult;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ad-hoc indexed model binding.
 */
@JsonIgnoreType
@Getter
public class AdHocIndexedModelBinding<T> extends IndexedModelBinding<T> {

    private static final String TMP_INDEX_MARKER = "Z_________Z"; // cheap hack, but works

    private String indexedBindingTemplate;

    private IIndexedBindingOwner indexedBindingOwner;

    private Form form;

    public AdHocIndexedModelBinding(String bindingExpression, IIndexedBindingOwner indexedBindingOwner, Form form) {
        super(bindingExpression);
        this.indexedBindingOwner = indexedBindingOwner;
        this.form = form;
    }

    @Override
    public T getValue(int[] indices) {
        if (getBindingExpression() != null && indexedBindingTemplate == null) {
            Map<String, String> replacementTemplates = buildIteratorReplacementTemplates(getIndexedBindingOwner().getIteratorInfos());
            indexedBindingTemplate = replaceIteratorsInBinding(getBindingExpression(), replacementTemplates, true);
        }
        if (indexedBindingTemplate != null) {
            BindingResult bindingResult = form.getBindingResult(
                    fillIteratorReplacementTemplate(indexedBindingTemplate, indices),
                    (Component) indexedBindingOwner);
            if (bindingResult != null) {
                return (T) bindingResult.getValue();
            }
        }
        return null;
    }

    /**
     * Creates iterator replacement templates for later filling.<br>
     * May be used in two ways:<br>
     * To replace given indices in many bindings: 1 x create replacement templates, 1 x fill templates with indices, n x replace in bindings.<br>
     * To prepare re-fillable single binding template: 1 x create replacement templates, 1 x replace in binding, n x fill binding with indices.
     * @param currentIterators iterators
     * @return map iterator name -&gt; replacement template
     */
    public static Map<String, String> buildIteratorReplacementTemplates(List<IRepeatableIteratorInfo> currentIterators) {
        Map<String, String> replacementMap = new LinkedHashMap<>();
        for (IRepeatableIteratorInfo currentIterator : currentIterators) {
            AtomicReference<String> currentBinding = new AtomicReference<>(StringUtils.removeSurroundingBraces(currentIterator.getCollectionBinding())
                    + "[" + TMP_INDEX_MARKER + replacementMap.size() + TMP_INDEX_MARKER + "]");

            replacementMap.forEach((parentIter, replacement) -> {
                // will match {iterator}, {iterator.prop}, {method(iterator)}, {method(iterator.prop)}, {method(iterator).prop}, {method(iterator[4])} and more
                Pattern pattern = Pattern.compile("^(.*[\\(\\{\\s\\,])?(" + Matcher.quoteReplacement(parentIter) + ")([\\,\\s\\}\\)\\.\\[].*)$");
                int counter = 0;
                do {
                    Matcher bindingMatcher = pattern.matcher(currentBinding.get());
                    if (bindingMatcher.find()) {
                        // replace iterator to its replacement eg. iter => col[Z_________Z3Z_________Z]
                        // where Z_________Z3Z_________Z is temporaty holder of an index of 4th iterator (index is 0 based)
                        String prefix = StringUtils.nullToEmpty(bindingMatcher.group(1));
                        String suffix = StringUtils.nullToEmpty(bindingMatcher.group(3));
                        currentBinding.set(prefix + replacement + suffix);
                    } else {
                        break; // no more occurrences of iterator
                    }
                } while (counter++ < 20); // infinite loop protection
            });

            replacementMap.put(currentIterator.getName(), currentBinding.get());
        }
        return replacementMap;
    }

    /**
     * Fills template with given iterator indices.
     * @param template template
     * @param indices indices
     * @return filled template
     */
    public static String fillIteratorReplacementTemplate(String template, int[] indices) {
        String replacement = template;
        for (int i = 0; i < indices.length; i++) {
            replacement = replacement.replace(TMP_INDEX_MARKER + i + TMP_INDEX_MARKER, String.valueOf(indices[i]));
        }
        return replacement;
    }

    /**
     * Fills templates with given iterator indices.
     * @param templates iterator template map
     * @param indices indices
     * @return filled iterator template map
     */
    public static  Map<String, String> fillIteratorReplacementTemplates(Map<String, String> templates, int[] indices) {
        Map<String, String> result = new LinkedHashMap<>();
        templates.forEach((iter, template) -> {
            result.put(iter, fillIteratorReplacementTemplate(template, indices));
        });
        return result;
    }

    /**
     * Replaces iterators in binding with provided replacements.
     * @param binding binding
     * @param iteratorReplacements iterator replacement map (iterator name -&gt; replacement)
     * @param useCurlyBrackets if binding is using curly brackets
     * @return replaces binding
     */
    public static String replaceIteratorsInBinding(String binding, Map<String, String> iteratorReplacements, boolean useCurlyBrackets) {
        if (binding == null) {
            return null;
        }
        if (!useCurlyBrackets || (binding.contains("{") && binding.contains("}"))) {
            for (Map.Entry<String, String> iteratorEntry : iteratorReplacements.entrySet()) {
                String iteratorId = iteratorEntry.getKey();
                String iteratorReplacement = iteratorEntry.getValue();

                // will match {iterator}, {iterator.prop}, {method(iterator)}, {method(iterator.prop)}, {method(iterator).prop}, {method(iterator[4])} and more
                Pattern pattern = Pattern.compile("^(.*[\\(\\{\\s\\,])?(" + Matcher.quoteReplacement(iteratorId) + ")([\\,\\s\\}\\)\\.\\[].*)$");

                int counter = 0;
                do {
                    Matcher bindingMatcher = pattern.matcher(binding);
                    if (bindingMatcher.find()) {
                        binding = bindingMatcher.group(1) + iteratorReplacement + bindingMatcher.group(3);
                    } else {
                        break; // no more occurrences of iterator
                    }
                } while (counter++ < 20); // infinite loop protection
            }
            if (!useCurlyBrackets) {
                binding = StringUtils.removeSurroundingBraces(binding);
            }
            return binding;
        } else {
            return binding;
        }
    }
}
