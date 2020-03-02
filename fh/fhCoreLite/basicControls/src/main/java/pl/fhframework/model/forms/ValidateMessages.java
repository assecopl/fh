package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.CollectionUtils;

import pl.fhframework.core.forms.IValidatedComponent;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.attribute.CommaSeparatedStringListAttrConverter;
import pl.fhframework.validation.FieldValidationResult;
import pl.fhframework.validation.IValidationResults;

import java.util.*;

import lombok.Getter;
import lombok.Setter;

@Control(parents = {}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, value = "Component to aggregate validation messages", icon = "fa fa-exclamation")
public class ValidateMessages extends FormElement implements IComponentsReferrer {

    public static final String ANY_COMPONENT = "*";
    public static final String NOT_ASSIGNED_BUSINESS = "+";

    @Getter
    @Setter
    private List<MessageData> validateMessages = new ArrayList<>();

    @Getter
    @Setter
    @XMLProperty(required = true)
    @DesignerXMLProperty(commonUse = true)
    @DocumentedComponentAttribute(value = "Represents level of displayed validation messages, like warning, error, information. Possible values: ok, info, warning, error, blocker")
    private PresentationStyleEnum level;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "false")
    @DocumentedComponentAttribute(value = "Defines if display messages strictly from choosen level", defaultValue = "false")
    private boolean strictLevel;

    @Getter
    @Setter
    @XMLProperty(converter = CommaSeparatedStringListAttrConverter.class, required = true)
    @DesignerXMLProperty(commonUse = true)
    @DocumentedComponentAttribute(value = "Parent component list of ids that group components for validation delimited by \",\" or * to match all or + to include unassigned custom messages")
    private List<String> componentIds;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "true")
    @DocumentedComponentAttribute(value = "Defines if field navigation should be handled", defaultValue = "true")
    private boolean navigation = true;

    @JsonIgnore
    private boolean showAttributeAsLabel;

    public ValidateMessages(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        showAttributeAsLabel = !"false".equals(AutowireHelper.getApplicationProperty("fhframework.validation.attributeAsLabel"));

    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        elementChanges.setFormElementId(this.getId());

        validateMessages.clear();
        validateMessages.addAll(processValidationResult());
        elementChanges.addChange("validateMessages", validateMessages);

        return elementChanges;
    }

    private List<MessageData> processValidationResult() {
        if (getForm().getViewMode() == Form.ViewMode.NORMAL) {
            IValidationResults validationResults = this.getForm().getAbstractUseCase().getUserSession().getValidationResults();

            return createMessagesForValidationErrors(validationResults);
        } else {
            return Arrays.asList(new MessageData("fake", "This is place", "for validation messages of: " + StringUtils.join(componentIds, ", ", false)));
        }
    }

    private List<MessageData> createMessagesForValidationErrors(IValidationResults validationResults) {
        List<MessageData> resolvedMessages = new ArrayList<>();
        for (Map.Entry<Object, Map<String, List<FieldValidationResult>>> entry : validationResults.getValidationErrors().entrySet()) {
            Map<String, List<FieldValidationResult>> fieldsInModelWithValidationResults = entry.getValue();
            for (Map.Entry<String, List<FieldValidationResult>> fieldWithValidation : fieldsInModelWithValidationResults.entrySet()) {
                String fieldName = fieldWithValidation.getKey();
                Object parentModelName = entry.getKey();
                List<FieldValidationResult> validationResult = fieldWithValidation.getValue();
                validationResult.stream()
                        .filter(x -> this.level.ordinal() == x.getPresentationStyleEnum().ordinal() || !this.strictLevel && this.level.ordinal() < x.getPresentationStyleEnum().ordinal())
                        .forEach(x -> {
                            MessageData msg = createMessageData(x, parentModelName, fieldName);
                            if (msg != null) {
                                resolvedMessages.add(msg);
                            }
                        });
            }
        }
        return resolvedMessages;
    }

    private MessageData createMessageData(FieldValidationResult x, Object parentModelName, String fieldName) {
        Optional<FormElement> foundComponent;
        if (x.getKnownSourceComponentId() != null) { // message is component-based and source component id is known
            FormElement knownSourceComponent = getForm().getFormElement(x.getKnownSourceComponentId());
            if (knownSourceComponent != null) {
                foundComponent = Optional.of(knownSourceComponent);
            } else {
                // source component is not currently displayed - no sense to show message to this component
                return null;
            }
        } else {
            // todo: optimize
            foundComponent = findComponentBasedOnAttribute(getForm(), fieldName, parentModelName);
        }
        MessageData msg = null;
        if (foundComponent.isPresent()) {
            if (!CollectionUtils.isEmpty(componentIds) && (ANY_COMPONENT.equals(componentIds.get(0)) || isChildOfComponent(foundComponent.get()))) {
                IValidatedComponent validationComponent = (IValidatedComponent) foundComponent.get();

                String label;
                if (validationComponent.getValidationLabelModelBinding() != null) {
                    label = validationComponent.getValidationLabelModelBinding().getBindingResult().getValue();
                } else {
                    label = validationComponent.getLabel();
                    if (StringUtils.isNullOrEmpty(label) && validationComponent instanceof BaseInputField) {
                        if (!StringUtils.isNullOrEmpty(((BaseInputField) validationComponent).getLabelId())) {
                            FormElement labelComponent = getForm().getFormElement(((BaseInputField) validationComponent).getLabelId());
                            if (labelComponent instanceof OutputLabel) {
                                label = ((OutputLabel) labelComponent).getValue();
                            }
                        }
                    }
                    if (StringUtils.isNullOrEmpty(label) && validationComponent instanceof BaseInputField && ((BaseInputField) validationComponent).getLabelModelBinding() != null) {
                        label = ((BaseInputField) validationComponent).convertBindingValueToString(((BaseInputField) validationComponent).getLabelModelBinding().getBindingResult());
                    }
                    if (StringUtils.isNullOrEmpty(label) && showAttributeAsLabel) {
                        label = convertIdToLabel(validationComponent.getId());
                    }
                }
                msg = new MessageData(validationComponent.getId(), label, x.getMessage());
            }
        } else if (!CollectionUtils.isEmpty(componentIds) && (ANY_COMPONENT.equals(componentIds.get(0)) || !x.isFormSource() && componentIds.contains(NOT_ASSIGNED_BUSINESS))) {
            if (showAttributeAsLabel) {
                msg = new MessageData(null, fieldName, x.getMessage()); // set null as element id
            } else {
                msg = new MessageData(null, null, x.getMessage()); // set null as element id and attribute name
            }
        }
        return msg;
    }

    private Optional<FormElement> findComponentBasedOnAttribute(Component basicFormElement, String attribute, Object parentObject) {
        // todo: optimize
        if (attribute == null || parentObject == null) {
            return Optional.empty();
        }
        if (basicFormElement instanceof IValidatedComponent) {
            boolean foundMatchingBinding = ((IValidatedComponent) basicFormElement).getAllBingings().stream()
                    .anyMatch(binding -> bindingMatches(binding, parentObject, attribute));
            if (foundMatchingBinding) {
                return Optional.ofNullable((FormElement) basicFormElement);
            }
        }
        Optional<FormElement> foundElement = Optional.empty();
        if (basicFormElement instanceof IGroupingComponent) {
            List<Component> subcomponents = ((IGroupingComponent) basicFormElement).getSubcomponents();
            for (Component subComponent : subcomponents) {
                Optional<FormElement> componentBasedOnAttribute = findComponentBasedOnAttribute(subComponent, attribute, parentObject);
                if (componentBasedOnAttribute.isPresent() && (!foundElement.isPresent() || componentBasedOnAttribute.get().getAvailability().ordinal() > foundElement.get().getAvailability().ordinal())) {
                    foundElement = componentBasedOnAttribute;
                    if (foundElement.get().getAvailability() == AccessibilityEnum.EDIT) {
                        break;
                    }
                }
            }
        }
        return foundElement;
    }

    private boolean bindingMatches(ModelBinding<?> binding, Object parentObject, String attributeName) {
        if (binding == null) {
            return false;
        }
        BindingResult<?> bindingResult = binding.getBindingResult();
        if (bindingResult == null) {
            return false;
        }
        return Objects.equals(attributeName, binding.getBindingResult().getAttributeName())
                && Objects.equals(parentObject, binding.getBindingResult().getParent());
    }

    private String convertIdToLabel(String msg) {
        if (msg != null) {
            if (msg.contains("[") && msg.contains("]")) {
                String oldRowNum;
                String newRowNum;
                oldRowNum = msg.substring(msg.indexOf("[") + 1, msg.indexOf("]"));
                newRowNum = String.valueOf(Integer.valueOf(oldRowNum) + 1);
                msg = msg.replace(oldRowNum, newRowNum);
                msg = msg.replace("[", " wiersz: ");
                msg = msg.replace("]", " - ");
            }
            if (msg.contains("_"))
                msg = msg.replaceAll("_", " ");

            if (msg.contains("  "))
                msg = msg.replaceAll("  ", " ");

        }
        return msg;
    }

    private boolean isChildOfComponent(FormElement formElement) {
        if (formElement == null) {
            return false;
        }
        if (componentIds.contains(formElement.getId())) {
            return true;
        } else {
            return isChildOfComponent((FormElement) formElement.getGroupingParentComponent());
        }
    }
}
