package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.expression.Expression;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.util.JsonUtil;
import pl.fhframework.core.util.SpelUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.optimized.ColumnOptimized;
import pl.fhframework.model.forms.validation.ValidationFactory;
import pl.fhframework.validation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, documentationExample = true, value = "Enables users to quickly find and select from a pre-populated list of values as they type, leveraging searching and filtering.",
        icon = "fa fa-outdent")
@DesignerControl(defaultWidth = 3)
@Control(parents = {PanelGroup.class, Group.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
public class SelectComboMenu extends BaseInputFieldWithKeySupport {
    private static final String ON_SPECIAL_KEY_ATTR = "onSpecialKey";
    private static final String ON_DBL_SPECIAL_KEY_ATTR = "onDblSpecialKey";
    private static final String ON_INPUT_ATTR = "onInput";
    private static final String ON_EMPTY_VALUE_ATTR = "onEmptyValue";
    private static final String VALUES_ATTR = "values";
    protected static final String TEXT = "text";
    private static final String FILTERED_VALUES = "filteredValues";
    private static final String HIGHLIGHTED_VALUE = "highlightedValue";
    private static final String FIRE_CHANGE_ACTION = "fireChange";
    private static final String FILTER_FUNCTION_ATTR = "filterFunction";
    protected static final String SELECTED_INDEX_ATTR = "selectedIndex";
    private static final String FORMATTER_ATTR = "formatter";
    private static final String FREE_TYPING = "freeTyping";
    private static final String DISPLAY_FUNCTION_ATTR = "displayFunction";
    private static final String DISPLAY_RULE_ATTR = "displayExpression";

    @Getter
    protected static class SelectComboItemDTO {

        private boolean displayAsTarget;

        private String targetValue;

        private String displayedValue;

        private Long targetId;

        public SelectComboItemDTO(String targetValue, Long targetId, String displayedValue) {
            this.displayAsTarget = false;
            this.targetValue = targetValue;
            this.displayedValue = displayedValue;
            this.targetId = targetId;
        }

        public SelectComboItemDTO(String targetValue, Long targetId) {
            this.displayAsTarget = true;
            this.targetValue = targetValue;
            this.targetId = targetId;
            this.displayedValue = targetValue;
        }

        public SelectComboItemDTO(IComboItem comboItem) {
            this.displayAsTarget = false;
            this.targetValue = comboItem.getTargetValue();
            this.targetId = comboItem.getTargetId();
            this.displayedValue = comboItem.getDisplayedValue();
        }
    }

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(defaultValue = "false", value = "Determines if empty value should be displayed on list of options. Remember to set parameter for empty label text (emptyLabelText).")
    @DesignerXMLProperty(priority = 60, functionalArea = CONTENT)
    protected boolean emptyLabel;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Determines empty value text displayed on list of options.")
    @DesignerXMLProperty(priority = 60, functionalArea = CONTENT)
    private String emptyLabelText;

    @Getter
    @XMLProperty(defaultValue = "-")
    @DesignerXMLProperty(functionalArea = DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR)
    @DocumentedComponentAttribute(value = "If there is some value, representing method in use case, then on every action in input, " +
            " that method will be executed. Action is fired, while component is active.", defaultValue = "-")
    private ActionBinding onInput;

    @Getter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR)
    @DocumentedComponentAttribute(value = "If there is some value, representing method in use case, then on clearing value, " +
            " that method will be executed. Action is fired, while component is active.")
    private ActionBinding onEmptyValue;

    @Getter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR)
    @DocumentedComponentAttribute(value = "If there is some value, representing method in use case, that will be called" +
            " every time a special key (Ctrl+Space) is pressed.")
    private ActionBinding onSpecialKey;

    @Getter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR)
    @DocumentedComponentAttribute(value = "If there is some value, representing method in use case, that will be called" +
            " every time a special key (Ctrl+Space) is pressed 2 times.")
    private ActionBinding onDblSpecialKey;

    @JsonIgnore
    protected Object selectedItem;

    @JsonIgnore
    protected Integer selectedItemIndex;

    @Getter
    protected String rawValue;

    @JsonIgnore
    protected String filterText = "";

    @JsonIgnore
    protected List<Object> values = new LinkedList<>();

    @JsonIgnore
    protected List<Object> filteredObjectValues = new LinkedList<>();

    @Getter
    protected List<SelectComboItemDTO> filteredValues = new LinkedList<>();

    @JsonIgnore
    private Object highlightedObjectValue = null;

    @Getter
    protected SelectComboItemDTO highlightedValue = null;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = VALUES_ATTR)
    @DesignerXMLProperty(commonUse = true, allowedTypes = {Collection.class, String.class}, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT, priority = 81)
    private ModelBinding valuesBinding;

    @JsonIgnore
    protected BiPredicate<Object, String> filterFunction;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = FILTER_FUNCTION_ATTR)
    @DesignerXMLProperty(allowedTypes = BiPredicate.class)
    @DocumentedComponentAttribute(defaultValue = "Default function: (model, value) -> ((String) model).toLowerCase().contains(value.toLowerCase())", boundable = true, value = "Name of model object (java.util.function.BiPredicate) which will be used to filter items by text.")
    private ModelBinding filterFunctionBinding;

    @JsonIgnore
    protected boolean filterInvoked;

    @JsonIgnore
    protected boolean fireOnchange = false;

    @JsonIgnore
    protected boolean highlightNullValue = false;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(defaultValue = "false", value = "Defines if combo values should be present even if no text is typed")
    protected boolean preload;

    @JsonIgnore
    @XMLProperty(value = FORMATTER_ATTR)
    @Setter
    @Getter
    @DocumentedComponentAttribute(value = "Id of formatter which will format object to String. It must be consistent with value of pl.fhframework.formatter.FhFormatter annotation.")
    private String formatter;

    @JsonIgnore
    protected boolean firstLoad = true;

    @Getter
    protected boolean freeTyping = false;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = FREE_TYPING)
    @DocumentedComponentAttribute(boundable = true, defaultValue = "false", value = "Defines if new values could be typed be user.  Binding changes may not be respected after initially showing this control.")
    private ModelBinding<Boolean> freeTypingBinding;

    @SuppressWarnings("rawtypes")
    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = DISPLAY_FUNCTION_ATTR)
    @DesignerXMLProperty(allowedTypes = java.util.function.Function.class)
    @DocumentedComponentAttribute(
            boundable = true,
            value = "Name of model object (java.util.function.Function) which will be used to format items as text.")
    private ModelBinding displayFunctionBinding;


    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = DISPLAY_RULE_ATTR)
    @DesignerXMLProperty(commonUse = true, allowedTypes = String.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    // TODO: String ???
    @DocumentedComponentAttribute(
            boundable = true,
            value = "Rule which will be used to format items as text.")
    private String displayExpression; // TODO: String ???

    @JsonIgnore
    @Getter
    @Setter
    private Function<Object, String> displayExpressionFunction;

    public SelectComboMenu(Form form) {
        super(form);
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (ON_INPUT_ATTR.equals(eventData.getEventType())) {
            return Optional.ofNullable(onInput);
        } else if (ON_SPECIAL_KEY_ATTR.equals(eventData.getEventType())) {
            return Optional.ofNullable(onSpecialKey);
        } else if (ON_DBL_SPECIAL_KEY_ATTR.equals(eventData.getEventType())) {
            return Optional.ofNullable(onDblSpecialKey);
        } else if (ON_EMPTY_VALUE_ATTR.equals(eventData.getEventType())) {
            return Optional.ofNullable(onEmptyValue);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        Object textObj = valueChange.getStringAttribute(TEXT);

        if (valueChange.hasAttributeChanged(SELECTED_INDEX_ATTR)) {
            this.selectedItemIndex = valueChange.getIntAttribute(SELECTED_INDEX_ATTR);
            this.selectedItem = (this.selectedItemIndex > 0) ? this.filteredObjectValues.get(selectedItemIndex -1 ) : null;
            changeSelectedItemBinding();
            this.rawValue = (selectedItem != null) ? toRawValue(selectedItem) : null;
            this.filterText = rawValue != null ? rawValue : "";
            processFiltering(this.filterText);
            if(this.selectedItemIndex == 0){
                highlightNullValue = true;
            }

        } else {

                String text = (String) textObj;
                this.filterText = text;
                processFiltering(text);
                firstLoad = false;
                selectItemByFilterText();
                changeSelectedItemBinding();
                // if free typing is allowed, use typed value as selected item
                if (freeTyping) {
                    this.selectedItem = StringUtils.emptyToNull(text);
                    this.rawValue = (String) this.selectedItem;
//                    this.selectedItemIndex = -1;
                    changeSelectedItemBinding();
                }
        }

    }

    private void selectItemByFilterText() {
        List<SelectComboItemDTO> entry = collectValues(filteredObjectValues);

        for (SelectComboItemDTO item : entry) {
            if (Objects.equals(this.filterText, item.isDisplayAsTarget() ? item.getTargetValue() : item.getDisplayedValue())) {
                this.selectedItemIndex = entry.indexOf(item);
                //Array is always bigger (null value was added at the beginning of it) then filteredObjectValues so we need to minus selected index to get right value.
                this.selectedItem = (this.selectedItemIndex > 0) ? this.filteredObjectValues.get(selectedItemIndex -1) : null;
                this.rawValue = toRawValue(this.selectedItem);
                this.fireOnchange = true;
                return;
            }
        }
    }

    //maybe instead of supporting selectItemBinding, it should use modelBinding from BaseInputField
    @Override
    public void validate() {
        if (this.getModelBinding() != null && this.getModelBinding().getBindingResult() != null) {
            this.validConversion = Objects.equals(StringUtils.nullToEmpty(this.filterText), StringUtils.nullToEmpty(this.rawValue));
            ValidationManager<SelectComboMenu> vm = ValidationFactory.getInstance().getSelectComboValidationProcess();
            List<ConstraintViolation<SelectComboMenu>> formComponentValidationResult = vm.validate(this);
            IValidationResults validationResults = getForm().getAbstractUseCase().getUserSession().getValidationResults();
            BindingResult bindingResult = this.getModelBinding().getBindingResult();
            formComponentValidationResult.forEach(x ->
                    validationResults.addCustomMessageForComponent(this, bindingResult.getParent(), bindingResult.getAttributeName(), x.getMessage(), PresentationStyleEnum.BLOCKER)
            );
        }
    }

    @Override
    public void prepareComponentAfterValidation(ElementChanges elementChanges) {
        IValidationResults validationResults = getForm().getAbstractUseCase().getUserSession().getValidationResults();

        BindingResult bindingResult = this.getModelBinding() != null ? this.getModelBinding().getBindingResult() : null;
        List<FieldValidationResult> fieldValidationResultFor = bindingResult == null ? Collections.emptyList() : validationResults.getFieldValidationResultFor(bindingResult.getParent(), bindingResult.getAttributeName());
        if (getAvailability() != AccessibilityEnum.EDIT) {
            fieldValidationResultFor.removeIf(FieldValidationResult::isFormSource);
        }
        processStylesAndHints(elementChanges, fieldValidationResultFor);
    }

    @Override
    protected FormFieldHints processPresentationStyle(ElementChanges elementChanges, List<FieldValidationResult> fieldValidationResults) {
        PresentationStyleEnum oldPresentationStyle = this.getPresentationStyle();
        FormFieldHints formFieldHints = null;
        BindingResult bindingResult = getModelBinding() != null ? getModelBinding().getBindingResult() : null;
        if (bindingResult != null) {
            formFieldHints = calculatePresentationStyle(getModelBinding().getBindingResult());
            this.setPresentationStyle((formFieldHints != null) ? formFieldHints.getPresentationStyleEnum() : null);
        } else {
            this.setPresentationStyle(null);
        }
        if (!fieldValidationResults.isEmpty() && (this.getPresentationStyle() == null || this.getPresentationStyle() != PresentationStyleEnum.BLOCKER)) {
            this.setPresentationStyle(PresentationStyleEnum.BLOCKER);
        }
        if (oldPresentationStyle != this.getPresentationStyle()) {
            elementChanges.addChange(PRESENTATION_STYLE_ATTR, this.getPresentationStyle());
        }
        return formFieldHints;
    }

    @JsonIgnore
    @Override
    public List<ModelBinding> getAllBingings() {
        List<ModelBinding> allBindings = new ArrayList<>();
        allBindings.add(getModelBinding());
        allBindings.add(getLabelModelBinding());
        allBindings.add(getModelBinding());
        return allBindings;
    }

    private void changeSelectedItemBinding() {
        if (getModelBinding() != null) {
            getModelBinding().setValue(selectedItem);
        }
    }

    private void processFiltering(String text) {
        filteredObjectValues.clear();
        filteredObjectValues.addAll(values);

        highlightedObjectValue = values.stream()
                .filter(d -> filterFunction.test(d, text))
                .findAny().orElse(null);

        filterInvoked = true;
    }

    @Override
    public ElementChanges updateView() {
        final ElementChanges elementChanges = super.updateView();
        boolean selectedBindingChanged = elementChanges.getChangedAttributes().containsKey(RAW_VALUE_ATTR);
//        boolean selectedBindingChanged = elementChanges.getChangedAttributes().containsKey(SELECTED_INDEX_ATTR);

        if (freeTypingBinding != null) {
            freeTyping = freeTypingBinding.resolveValueAndAddChanges(this, elementChanges, freeTyping, FREE_TYPING);
        }
        setFilterFunction();
        refreshAvailability(elementChanges);
        boolean valuesChanged = processValuesBinding();
        if (selectedBindingChanged || valuesChanged) {
            processFiltering(this.filterText);
        }
        processFilterBinding(elementChanges, valuesChanged);
        processLabelBinding(elementChanges);

        this.prepareComponentAfterValidation(elementChanges);

        if (elementChanges.containsAnyChanges()) {
            refreshView();
        }

        if(this.fireOnchange){
            //Fire Onchange event from frontend when model has change after serach.
            elementChanges.addChange(FIRE_CHANGE_ACTION, true);
            this.fireOnchange = false;
        }

        return elementChanges;
    }

    protected void setFilterFunction() {
        this.filterFunction = (model, value) -> {
            if (StringUtils.isNullOrEmpty(value)) return false;
            return objectToString(model).toLowerCase().startsWith(value.toLowerCase());
        };
    }

    protected boolean processValuesBinding() {
        boolean valuesChanged = false;
        if (valuesBinding != null) {
            BindingResult valuesBindingResult = valuesBinding.getBindingResult();
            if (valuesBindingResult != null) {
                Object value = valuesBindingResult.getValue();
                if (value instanceof String) {
                    String valuesAsString = (String) value;
                    String[] allValues = valuesAsString.split("\\|");
                    if (allValues.length > 0) {
                        List<Object> newValues = Arrays.stream(allValues).collect(Collectors.toList());
                        if (!Objects.equals(newValues, values)) {
                            this.values.clear();
                            this.values.addAll(newValues);
                            return true;
                        }
                    }
                } else if (value instanceof List && !Objects.equals(value, values)) {
                    List collection = (List) value;
                    this.values.clear();
                    this.values.addAll(new LinkedList<>(collection));
                    return true;
                }
            }
        }
        return valuesChanged;
    }

    private boolean processFilterBinding(ElementChanges elementChanges, boolean valuesChanged) {
        if (!preload && firstLoad &&  !valuesChanged && !filterInvoked) {
            return false;
        }

        boolean result = false;
        if (valuesChanged) {
            this.filteredValues = collectValues(filteredObjectValues);
            elementChanges.addChange(FILTERED_VALUES, this.filteredValues);
            result = true;
        }
        if (filterInvoked) {
            this.highlightedValue = collectValue(highlightedObjectValue);
            elementChanges.addChange(HIGHLIGHTED_VALUE, this.highlightedValue);

            filterInvoked = false;
            result = true;
        }


        return result;
    }

    @Override
    protected boolean processValueBinding(ElementChanges elementChanges) {
        if (getModelBinding() != null) {
            BindingResult selectedBindingResult = getModelBinding().getBindingResult();
            if (selectedBindingResult != null) {
                Object value = selectedBindingResult.getValue();
                if (!Objects.equals(value, selectedItem)) {
                    this.selectedItem = value;
                    this.rawValue = toRawValue(value);
                    elementChanges.addChange(RAW_VALUE_ATTR, this.rawValue);
                    this.filterText = rawValue != null ? rawValue : "";
//                    this.rawValue = this.convertValueToString(value, getOptionalFormatter().orElse(null));
                    return true;
                }
            }
        }
        return false;
    }

    private List<SelectComboItemDTO> collectValues(List<Object> valuesToConvert) {
        List<SelectComboItemDTO> filteredConvertedValues = new LinkedList<>();

        /**
         * Add empty element to evry list.
         * Empty value will be always on list for proper null value binding with frontend.
         */

        SelectComboItemDTO nullItem = getNullItem();
        filteredConvertedValues.add(nullItem);

        AtomicReference<Long> idx = new AtomicReference<>(0L);
        valuesToConvert.forEach(value -> {
            SelectComboItemDTO item;
            if (value instanceof IComboItem) {
                item = new SelectComboItemDTO((IComboItem) value);
                item.targetId = idx.get();
            } else {
                item = new SelectComboItemDTO(objectToString(value), idx.get());
            }
            idx.getAndSet(idx.get() + 1);
            filteredConvertedValues.add(item);
        });



        return filteredConvertedValues;
    }

    private SelectComboItemDTO getNullItem() {
        return new SelectComboItemDTO(
                "nullValue", -1L, this.emptyLabelText == null ? "" : this.emptyLabelText);
    }

    private SelectComboItemDTO collectValue(Object value) {

        if(highlightNullValue){
            highlightNullValue = false;
            return getNullItem();
        }

        AtomicReference<Long> idx = new AtomicReference<>(0L);
        SelectComboItemDTO item;
        if (value instanceof IComboItem) {
            item = new SelectComboItemDTO((IComboItem) value);
            item.targetId = idx.get();
        } else {
            item = new SelectComboItemDTO(objectToString(value), idx.get());
        }
        idx.getAndSet(idx.get() + 1);
        return item;
    }

    private String toRawValue(Object s) {
        if (s instanceof List) {
            return JsonUtil.writeValue(((List) s).stream().map(this::toRawElementValue).collect(Collectors.toList()));
        }
        return toRawElementValue(s);
    }

    private String toRawElementValue(Object s) {
        if (s instanceof IComboItem) {
            return ((IComboItem) s).getTargetValue();
        } else {
            return objectToString(s);
        }
    }

    private String objectToString(Object s) {
        Optional<String> formatter = getOptionalFormatter();
        if (formatter.isPresent()) {
            return this.convertValueToString(s, formatter.get());
        }

        if (displayFunctionBinding != null) {
            return objectToStringAsDisplayFunction(s);
        }

        if (displayExpression != null) {
            return objectToStringAsDisplayExpresssion(s);
        }

        return this.convertValueToString(s, "");
    }

    @Override
    protected String convertToRaw(BindingResult<?> bindingResult) {
        Object value = bindingResult == null ? null : bindingResult.getValue();
        if (value == null) {
            return "";
        }

        return toRawValue(value);
    }

    private String objectToStringAsDisplayExpresssion(Object item) {
        if (item instanceof String) {
            return (String) item;
        }
        if (displayExpressionFunction == null) {
            //simple caching
            Expression exp = SpelUtils.parseExpression(displayExpression);
            displayExpressionFunction = obj -> convertValueToString(SpelUtils.evaluateExpression(exp, obj));
        }
        return displayExpressionFunction.apply(item);
    }

    private String objectToStringAsDisplayFunction(Object s) {
        @SuppressWarnings("unchecked")
        BindingResult<Function<Object, String>> bindingResult = displayFunctionBinding.getBindingResult();
        if (bindingResult == null) {
            throw new FhBindingException("No binding function for " + displayFunctionBinding.getBindingExpression());
        }
        Function<Object, String> function = bindingResult.getValue();
        if (function == null) {
            throw new FhBindingException("No binding function for " + displayFunctionBinding.getBindingExpression());
        }

        return function.apply(s);
    }


    @Override
    public Optional<String> getOptionalFormatter() {
        return Optional.ofNullable(this.formatter);
    }

    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }

    @Override
    public SelectComboMenu createNewSameComponent() {
        return new SelectComboMenu(getForm());
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, BaseInputField baseClone) {
        super.doCopy(table, iteratorReplacements, baseClone);
        SelectComboMenu clone = (SelectComboMenu) baseClone;

        clone.setOnInput(table.getRowBinding(this.getOnInput(), clone, iteratorReplacements));
        clone.setOnEmptyValue(table.getRowBinding(this.getOnEmptyValue(), clone, iteratorReplacements));
        clone.setOnSpecialKey(table.getRowBinding(this.getOnSpecialKey(), clone, iteratorReplacements));
        clone.setOnDblSpecialKey(table.getRowBinding(this.getOnDblSpecialKey(), clone, iteratorReplacements));
        clone.setValuesBinding(table.getRowBinding(this.getValuesBinding(), clone, iteratorReplacements));
        clone.setFilterFunctionBinding(table.getRowBinding(this.getFilterFunctionBinding(), clone, iteratorReplacements));
        clone.setDisplayFunctionBinding(table.getRowBinding(this.getDisplayFunctionBinding(), clone, iteratorReplacements));
        clone.setDisplayExpression(getDisplayExpression());
        clone.setPreload(this.isPreload());
        clone.setFormatter(getFormatter());
        clone.setEmptyLabel(isEmptyLabel());
        clone.setEmptyLabelText(getEmptyLabelText());
        clone.setFreeTypingBinding(table.getRowBinding(this.getFreeTypingBinding(), clone, iteratorReplacements));
    }

    public void setOnInput(ActionBinding onInput) {
        this.onInput = onInput;
    }

    public IActionCallbackContext setOnInput(IActionCallback onInput) {
        return CallbackActionBinding.createAndSet(onInput, this::setOnInput);
    }

    public void setOnEmptyValue(ActionBinding onEmptyValue) {
        this.onEmptyValue = onEmptyValue;
    }

    public IActionCallbackContext setOnEmptyValue(IActionCallback onEmptyValue) {
        return CallbackActionBinding.createAndSet(onEmptyValue, this::setOnEmptyValue);
    }

    public void setOnSpecialKey(ActionBinding onSpecialKey) {
        this.onSpecialKey = onSpecialKey;
    }

    public void setOnDblSpecialKey(ActionBinding onDblSpecialKey) {
        this.onDblSpecialKey = onDblSpecialKey;
    }

    public IActionCallbackContext setOnSpecialKey(IActionCallback onSpecialKey) {
        return CallbackActionBinding.createAndSet(onSpecialKey, this::setOnSpecialKey);
    }
}
