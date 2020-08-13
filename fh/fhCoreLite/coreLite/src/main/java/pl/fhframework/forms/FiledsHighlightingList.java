package pl.fhframework.forms;

import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pl.fhframework.core.forms.IValidatedComponent;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.validation.FieldValidationResult;
import pl.fhframework.validation.FormFieldHints;
import pl.fhframework.validation.IValidationResults;

import java.util.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FiledsHighlightingList implements IFieldsHighlightingList {

    /**
     * key - is a model used by Form value - map of attributes, and highlighting results as list
     */
    private Map<Object, Map<String, FormFieldHints>> objectAttributeToPresentationStyle = new HashMap<>();

    @Override
    public FormFieldHints getPresentationStyleForField(Object parent, String attributeName) {
        Map<String, FormFieldHints> attributeToFieldValidationResult = objectAttributeToPresentationStyle.getOrDefault(parent, Collections.emptyMap());
        return attributeToFieldValidationResult.get(attributeName);
    }

    @Override
    public Map<Object, Map<String, FormFieldHints>> getFieldsHighlight() {
        return objectAttributeToPresentationStyle;
    }

    @Override
    public void add(Object parent, String attributeName, PresentationStyleEnum presentationStyleEnum) {
        add(parent, attributeName, presentationStyleEnum, null);
    }

    @Override
    public void add(Object parent, String attributeName, PresentationStyleEnum presentationStyleEnum, String hint) {
        objectAttributeToPresentationStyle.computeIfAbsent(parent, key -> new HashMap<>());
        Map<String, FormFieldHints> attributeToFieldValidationResult = objectAttributeToPresentationStyle.get(parent);
        attributeToFieldValidationResult.put(attributeName, new FormFieldHints(presentationStyleEnum, hint));
    }

    @Override
    public void remove(Object parent, String attributeName) {
        objectAttributeToPresentationStyle.computeIfAbsent(parent, key -> new HashMap<>());
        objectAttributeToPresentationStyle.get(parent).remove(attributeName);
    }

    public void clear() {
        objectAttributeToPresentationStyle.clear();
    }
}
