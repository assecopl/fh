package pl.fhframework.validation;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pl.fhframework.core.forms.IValidatedComponent;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.model.PresentationStyleEnum;

import java.util.*;
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ValidationResults implements IValidationResults {

    @Autowired
    private MessageService messageService;

    /**
     * key - is a model used by Form value - map of attributes, and validation results as list
     */
    @Getter
    private Map<Object, Map<String, List<FieldValidationResult>>> objectAttributeToFieldValidationResult = new HashMap<>();

    /**
     * Are messages currently reported from web layer (form and jsr-303)
     */
    private boolean formMode;

    @Override
    public FormFieldHints getPresentationStyleForField(Object parent, String attributeName) {
        Map<String, List<FieldValidationResult>> attributeToFieldValidationResult = objectAttributeToFieldValidationResult.get(parent);
        if (attributeToFieldValidationResult == null) {
            return null;
        }
        List<FieldValidationResult> validationResults = attributeToFieldValidationResult.get(attributeName);
        if (validationResults == null) {
            return null;
        }
        //TODO:Instead of this creation of new objects, form component (asking object) should pass our structure for refreshment.
        return new FormFieldHints(validationResults);
    }

    @Override
    public Map<Object, Map<String, List<FieldValidationResult>>> getValidationErrors() {
        return objectAttributeToFieldValidationResult;
    }

    @Override
    public void addCustomMessageForComponent(IValidatedComponent component, Object parent, String attributeName, String message, PresentationStyleEnum presentationStyleEnum) {
        this.addFieldValidationResult(Optional.of(component), parent, attributeName, message, presentationStyleEnum);
    }

    @Override
    public void addCustomMessage(Object parent, String attributeName, String message, PresentationStyleEnum presentationStyleEnum) {
        this.addFieldValidationResult(Optional.empty(), parent, attributeName, message, presentationStyleEnum);
    }

    @Override
    public void addCustomTemplateMessage(Object parent, String attributeName, String keyMessage, PresentationStyleEnum presentationStyleEnum) {
        MessageService.MessageBundle allBundles = messageService.getAllBundles();
        String message = allBundles.getMessage(keyMessage);
        this.addFieldValidationResult(Optional.empty(), parent, attributeName, message, presentationStyleEnum);
    }

    @Override
    public void addCustomTemplateMessage(Object parent, String attributeName, String keyMessage, Object[] arguments, PresentationStyleEnum presentationStyleEnum) {
        MessageService.MessageBundle allBundles = messageService.getAllBundles();
        String message = allBundles.getMessage(keyMessage, arguments);
        this.addFieldValidationResult(Optional.empty(), parent, attributeName, message, presentationStyleEnum);
    }

    @Override
    public boolean areAnyValidationMessages() {
        return !CollectionUtils.isEmpty(objectAttributeToFieldValidationResult);
    }

    @Override
    public boolean areAnyValidationMessages(PresentationStyleEnum minimalStyleEnum) {
        if (!CollectionUtils.isEmpty(objectAttributeToFieldValidationResult)) {
            for (Map<String, List<FieldValidationResult>> parentResults : objectAttributeToFieldValidationResult.values()) {
                if (!CollectionUtils.isEmpty(parentResults)) {
                    for (List<FieldValidationResult> results : parentResults.values()) {
                        if (!CollectionUtils.isEmpty(results)) {
                            for (FieldValidationResult result : results) {
                                if (result != null && result.getPresentationStyleEnum() != null && result.getPresentationStyleEnum().ordinal() >= minimalStyleEnum.ordinal()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasAtLeastErrors() {
        return hasAtLeastLevel(PresentationStyleEnum.ERROR);
    }

    @Override
    public boolean hasBlockers() {
        return hasAtLeastLevel(PresentationStyleEnum.BLOCKER);
    }

    @Override
    public boolean hasAtLeastLevel(PresentationStyleEnum level) {
        return objectAttributeToFieldValidationResult.values().stream().map(Map::values).flatMap(Collection::stream).flatMap(Collection::stream).anyMatch(field -> field.getPresentationStyleEnum().ordinal() >= level.ordinal());
    }

    @Override
    public void clearValidationErrors() {
        objectAttributeToFieldValidationResult.clear();
    }

    @Override
    public List<FieldValidationResult> getFieldValidationResultFor(Object parent, String attributeName) {
        Map<String, List<FieldValidationResult>> stringListMap = objectAttributeToFieldValidationResult.get(parent);
        if (CollectionUtils.isEmpty(stringListMap)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(stringListMap.getOrDefault(attributeName, Collections.emptyList()));
    }

    void addFieldValidationResult(Object parent, String attributeName, FieldValidationResult fieldValidationResult) {
        fieldValidationResult.setFormSource(this.formMode);

        if (!objectAttributeToFieldValidationResult.containsKey(parent)) {
            objectAttributeToFieldValidationResult.put(parent, new HashMap<>());
        }
        Map<String, List<FieldValidationResult>> attributeToFieldValidationResult = objectAttributeToFieldValidationResult.get(parent);
        if (!attributeToFieldValidationResult.containsKey(attributeName)) {
            attributeToFieldValidationResult.put(attributeName, new ArrayList<>());
        }
        attributeToFieldValidationResult.get(attributeName).add(fieldValidationResult);
    }

    public void addValidationResults(IValidationResults validationResults) {
        validationResults.getValidationErrors().forEach((object, map) -> {
            map.forEach((attr, resultList) -> resultList.forEach(result -> addFieldValidationResult(object, attr, result)));
        });
    }

    void addFieldValidationResult(Optional<IValidatedComponent> component, Object parent, String attributeName, String message, PresentationStyleEnum presentationStyleEnum) {
        FieldValidationResult fieldValidationResult = createFieldValidationResult(component, message, presentationStyleEnum);
        addFieldValidationResult(parent, attributeName, fieldValidationResult);
    }

    FieldValidationResult createFieldValidationResult(Optional<IValidatedComponent> component, String message, PresentationStyleEnum presentationStyleEnum) {
        FieldValidationResult fieldValidationResult = new FieldValidationResult();
        fieldValidationResult.setMessage(message);
        fieldValidationResult.setPresentationStyleEnum(presentationStyleEnum);
        if (component.isPresent()) {
            fieldValidationResult.setKnownSourceComponentId(component.get().getId());
        }
        return fieldValidationResult;
    }

    void setFormMode() {
        this.formMode = true;
    }

    void setBusinessMode() {
        this.formMode = false;
    }
}
