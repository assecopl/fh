package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.expression.Expression;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.util.SpelUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;

import java.util.*;
import java.util.function.Function;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;


public abstract class BaseInputListField extends BaseInputField {

    private static final String VALUES_ATTR = "values";
    private static final String RAW_OPTIONS_ATTR = "rawOptions";
    protected static final String SELECTED_INDEX = "selectedIndex";
    private static final String FORMATTER_ATTR = "formatter";
    private static final String EMPTY_VALUE_ATTR = "emptyValue";
    private static final String EMPTY_LABEL_ATTR = "emptyLabel";

    @Getter
    private List<String> rawOptions;

    @Getter
    private int selectedIndex = -1;

    @JsonIgnore
    protected int newIndex = 0;

    @JsonIgnore
    private List<?> currentlyCalculatedValues;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = VALUES_ATTR)
    @DesignerXMLProperty(priority = 90, functionalArea = CONTENT, commonUse = true)
    private ModelBinding listBinding;

    @JsonIgnore
    @Getter
    protected List<?> presentedValues;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = FORMATTER_ATTR)
    @DocumentedComponentAttribute(value = "Id of formatter which will format object to String. It must be consistent with value of pl.fhframework.formatter.FhFormatter annotation.")
    @DesignerXMLProperty(priority = 100)
    private String formatter;

    @Getter
    private boolean emptyValue;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(EMPTY_VALUE_ATTR)
    @DocumentedComponentAttribute(defaultValue = "false", value = "Defines if value passed can be empty", boundable = true)
    private ModelBinding<Boolean> emptyValueBinding;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(defaultValue = "false", value = "Determines if empty value should be displayed on list of options.")
    @DesignerXMLProperty(priority = 60, functionalArea = CONTENT)
    protected boolean emptyLabel;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(aliases = {"displayAttribute"})
    @DesignerXMLProperty(priority = 79, functionalArea = CONTENT, commonUse = true)
    @DocumentedComponentAttribute(value = "Defines an attribute of a single value object to be used for display. If not set, converter will be used.")
    private String displayExpression;

    @JsonIgnore
    @Getter
    @Setter
    private Function<Object, Object> displayExpressionFunction;

    public BaseInputListField(Form form) {
        super(form);
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        if (this.getModelBinding() != null) {
            Object chosenObject = getChosenObjectAndSetIndex(valueChange.getMainValue());
            this.getModelBinding().setValue(chosenObject);
        }
    }

    protected Object getChosenObjectAndSetIndex(String newValue) {
        this.newIndex = !StringUtils.isNullOrEmpty(newValue) ? Integer.parseInt(newValue) : -1;
        return (this.newIndex >= 0) ? presentedValues.get(this.newIndex) : null;
    }

    protected boolean areValuesEqual(List<String> list1, List<String> list2) {
        if (list1 == list2) return true;
        if ((list1 == null) || (list2 == null)) return false;
        if (list1.size() != list2.size()) return false;
        int size = list1.size();
        for (int i = 0; i < size; i++) {
            if (!areValuesTheSame(list1.get(i), list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        currentlyCalculatedValues = calculateBindingValues();
        elementChanges.setFormId(getForm().getId());
        elementChanges.setFormElementId(this.getId());

        if (emptyValueBinding != null) {
            emptyValue = emptyValueBinding.resolveValueAndAddChanges(this, elementChanges, emptyValue, EMPTY_VALUE_ATTR);
        }

        refreshAvailability(elementChanges);

        boolean shouldRefresh = false;
        List<String> newRawValues = calculateRawValuesCollection(currentlyCalculatedValues, formatter);
        if (!areValuesEqual(newRawValues, rawOptions)) {
            this.rawOptions = newRawValues;
            elementChanges.addChange(RAW_OPTIONS_ATTR, this.rawOptions);
            this.presentedValues = currentlyCalculatedValues;
            shouldRefresh = true;
        }

        ModelBinding modelBinding = getModelBinding();
        if (modelBinding != null) {
            skipSettingPresentation(elementChanges, getForm());
            BindingResult bindingResult = modelBinding.getBindingResult();
            boolean selectedIndexUpdated = false;
            if (this.newIndex != this.selectedIndex) {
                this.selectedIndex = newIndex;
                selectedIndexUpdated = true;
                shouldRefresh = true;
            }
            Object selectedValue = (this.selectedIndex >= 0 && this.selectedIndex < presentedValues.size()) ? presentedValues.get(selectedIndex) : null;
            if (bindingResult != null && !presentedValues.isEmpty() && !areValuesTheSame(bindingResult.getValue(), selectedValue)) {
                this.selectedIndex = presentedValues.indexOf(bindingResult.getValue());
                selectedIndexUpdated = true;
                shouldRefresh = true;
            }
            if (selectedIndexUpdated) {
                elementChanges.addChange(SELECTED_INDEX, this.selectedIndex);
            }
        }
        shouldRefresh |= processLabelBinding(elementChanges);
        this.prepareComponentAfterValidation(elementChanges);
        if (shouldRefresh) {
            refreshView();
        }

        return elementChanges;
    }


    protected List<?> calculateBindingValues() {
        BindingResult bindingResult = listBinding == null ? null : listBinding.getBindingResult();
        Object value = bindingResult != null ? bindingResult.getValue() : null;
        if (value == null) {
            if (getForm().getViewMode() == Form.ViewMode.DESIGN || getForm().getViewMode() == Form.ViewMode.PREVIEW) {
                return calculateBindingValues(Arrays.asList("Option 1", "Option 2", "Option 3"));
            }
            return calculateBindingValues(Collections.emptyList());
        } else if (value instanceof String) {
            List<String> adhoc = new ArrayList<>();
            String[] adhocOptions = value.toString().split("\\|");
            Collections.addAll(adhoc, adhocOptions);
            return calculateBindingValues(adhoc);
        } else if (value instanceof List) {
            return calculateBindingValues((List) value);
        } else {
            throw new FhBindingException("Bound value is not a List instance!!");
        }
    }

    protected List<?> calculateBindingValues(List<?> value) {
        List<?> values = new LinkedList(value);
        if (emptyLabel) {
            values.add(0, null);
        }
        return values;
    }

    protected List<String> calculateRawValuesCollection(List<?> values, String converter) {
        List<String> result = new ArrayList<>();
        Set<Class<?>> displayAttributeNotFound = new HashSet<>();
        for (Object element : values) {
            element = maybeGetDisplayAttribute(element);
            if (element instanceof IComboItem) {
                result.add(((IComboItem) element).getTargetValue());
            } else {
                result.add(this.convertValueToString(element, converter));
            }
        }
        return result;
    }

    @JsonIgnore
    @Override
    public Optional<String> getOptionalFormatter() {
        return Optional.ofNullable(formatter);
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, BaseInputField inputFieldClone) {
        super.doCopy(table, iteratorReplacements, inputFieldClone);
        BaseInputListField clone = (BaseInputListField) inputFieldClone;
        clone.setListBinding(table.getRowBinding(getListBinding(), clone, iteratorReplacements));
        clone.setFormatter(getFormatter());
        clone.setEmptyValueBinding(table.getRowBinding(getEmptyValueBinding(), clone, iteratorReplacements));
        clone.setEmptyLabel(isEmptyLabel());
    }

    private Object maybeGetDisplayAttribute(Object element) {
        if (getForm().getViewMode() == Form.ViewMode.NORMAL && element != null && StringUtils.hasText(displayExpression)) {
            if (displayExpressionFunction == null) {
                //simple caching
                Expression exp = SpelUtils.parseExpression(displayExpression);
                displayExpressionFunction = obj -> {
                    if (obj instanceof String) {
                        return obj;
                    }

                    return convertValueToString(SpelUtils.evaluateExpression(exp, obj));
                };
            }
            return displayExpressionFunction.apply(element);
        }
        return element;
    }

    @Override
    protected String convertMainValueToString(Object value, Optional<String> converterName) {
        value = maybeGetDisplayAttribute(value);
        return super.convertMainValueToString(value, converterName);
    }
}
