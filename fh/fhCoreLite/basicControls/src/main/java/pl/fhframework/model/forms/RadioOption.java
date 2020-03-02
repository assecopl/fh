package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.CompiledBinding;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.binding.StaticBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;
import pl.fhframework.model.forms.validation.ValidationFactory;
import pl.fhframework.validation.ValidationManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

/**
 * RadioOption
 * It is used to add common logic to options group eg. reaction to 'on change' events.
 */
@TemplateControl(tagName = "fh-radio-option")
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, value = "RadioOption represents single radio component", icon = "fa fa-circle", ignoreFields = {"emptyValue", "emptyLabel"})
public class RadioOption extends BaseInputField implements IPairableComponent<String> {
    private static final String GROUP_VALUE_ATTR = "targetValue";
    private static final String CHECKED_ATTR = "checkedRadio";

    public RadioOption(Form form) {
        super(form);
    }

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = GROUP_VALUE_ATTR)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Target value for group of RadioOption controls")
    private ModelBinding groupModelBinding;

    @Getter
    @JsonIgnore
    private String targetValue;

    @Getter
    @JsonIgnore
    private boolean checkedRadio;

    @Getter
    @Setter
    private String groupName;

    public RadioOption createNewSameComponent() {
        return new RadioOption(getForm());
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        if (groupModelBinding != null && !valueChange.getChangedAttributes().isEmpty()) {
            this.updateBinding(valueChange, groupModelBinding, groupModelBinding.getBindingExpression(), this.getOptionalFormatter());
        }
    }

    @Override
    public void setPairData(String data) {
        this.groupName = String.valueOf(data);
    }

    @Override
    @JsonIgnore
    public String getPairDiscriminator() {
        int resultHash;

        if (groupModelBinding instanceof CompiledBinding) {
            resultHash = Objects.hash(
                    ((CompiledBinding) groupModelBinding).getTargetType(),
                    groupModelBinding.getBindingResult().getAttributeName()
            );
        } else if (groupModelBinding instanceof StaticBinding) {
            resultHash = Objects.hash(((StaticBinding) groupModelBinding).getStaticValue());
        } else {
            return UUID.randomUUID().toString();
        }

        return String.valueOf(resultHash);
    }

    @SuppressWarnings("unused")
    public boolean isChecked() {
        if (groupModelBinding == null || getModelBinding() == null) return false;

        Object a1 = groupModelBinding.getBindingResult().getValue();
        Object a2 = getModelBinding().getBindingResult().getValue();

        if (a1 == null || a2 == null) return false;

        return a1.equals(a2);
    }

    @Override
    protected void checkBinding() {
        if (getAvailability() == null || getAvailability() == AccessibilityEnum.EDIT) {
            if (groupModelBinding != null && !groupModelBinding.canChange()) {
                setAvailability(AccessibilityEnum.VIEW);
            }
        }
    }

    protected String convertToRaw(BindingResult<?> bindingResult) {
        if (bindingResult == null) {
            return "";
        }
        Optional<String> converterName = this.getOptionalFormatter();

        if (!converterName.isPresent() && bindingResult.getValue() instanceof Enum) {
            return ((Enum) bindingResult.getValue()).name();
        }

        return this.convertBindingValueToString(bindingResult, converterName);
    }

    @Override
    protected ValidationManager<BaseInputField> createValidationManager() {
        return ValidationFactory.getInstance().getRadioOptionValidationProcess();
    }

    @Override
    protected ModelBinding getTargetModelBinding() {
        return getGroupModelBinding();
    }

    @JsonIgnore
    @Override
    public List<ModelBinding> getAllBingings() {
        List<ModelBinding> allBindings = super.getAllBingings();
        allBindings.add(getGroupModelBinding());
        return allBindings;
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        processValue(elementChanges);
        return elementChanges;
    }

    private void processValue(ElementChanges elementChanges) {
        BindingResult bindingResult = groupModelBinding != null ? groupModelBinding.getBindingResult() : null;
        if (bindingResult != null) {
            String newLabelValue = this.convertValueToString(bindingResult.getValue());
            if (!areValuesTheSame(newLabelValue, targetValue)) {
                this.refreshView();
                this.targetValue = newLabelValue;

                boolean isChecked = isChecked();
                if (!areValuesTheSame(isChecked, checkedRadio)) {
                    this.checkedRadio = isChecked;
                    elementChanges.addChange(CHECKED_ATTR, this.checkedRadio);
                }
            }
        }
    }

}
