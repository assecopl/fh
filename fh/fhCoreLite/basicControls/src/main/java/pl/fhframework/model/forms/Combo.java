package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.expression.Expression;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.util.JsonUtil;
import pl.fhframework.core.util.SpelUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;
import pl.fhframework.model.forms.validation.ValidationFactory;
import pl.fhframework.validation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.*;

@DesignerControl(defaultWidth = 3)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, value = "Enables users to quickly find and select from a pre-populated list of values as they type, leveraging searching and filtering.",
        icon = "fa fa-outdent")
@Control(parents = {PanelGroup.class, Group.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
public class Combo extends BaseInputFieldWithKeySupport {

    /**
     * Old name of value property used by Combo
     */
    public static final String SELECTED_ITEM_ATTR = "selectedItem";
    protected static final String MULTISELECT_RAW_VALUE_ATTR = "multiselectRawValue";
    private static final String ON_SPECIAL_KEY_ATTR = "onSpecialKey";
    private static final String ON_DBL_SPECIAL_KEY_ATTR = "onDblSpecialKey";
    private static final String ON_INPUT_ATTR = "onInput";
    private static final String ON_EMPTY_VALUE_ATTR = "onEmptyValue";
    private static final String VALUES_ATTR = "values";
    protected static final String TEXT = "text";
    protected static final String ADDED_TAG = "addedTag";
    private static final String FILTERED_VALUES = "filteredValues";
    private static final String FILTER_FUNCTION_ATTR = "filterFunction";
    private static final String FILTER_TEXT = "filterText";
    protected static final String SELECTED_INDEX_ATTR = "selectedIndex";
    protected static final String REMOVED_INDEX_ATTR = "removedIndex";
    protected static final String SELECTED_INDEX_GROUP_ATTR = "selectedIndexGroup";
    protected static final String CLEARED = "cleared";
    private static final String FORMATTER_ATTR = "formatter";
    protected static final String CURSOR = "cursor";
    private static final String FREE_TYPING = "freeTyping";
    private static final String EMPTY_VALUE_ATTR = "emptyValue";
    private static final String DISPLAY_FUNCTION_ATTR = "displayFunction";
    private static final String DISPLAY_RULE_ATTR = "displayExpression";
    private static final String ATTR_OPEN_ON_FOCUS = "openOnFocus";

    @Getter
    protected static class ComboItemDTO {

        private boolean displayAsTarget;

        private Object targetValue;

        private String displayedValue;

        private Integer targetCursorPosition;

        private Long targetId;

        public ComboItemDTO(String targetValue, Long targetId) {
            this.displayAsTarget = true;
            this.targetValue = targetValue;
            this.targetId = targetId;
        }

        public ComboItemDTO(Object targetValue, Long targetId, boolean displayAsTarget, String displayedValue) {
            this.displayAsTarget = displayAsTarget;
            this.targetValue = targetValue;
            this.targetId = targetId;
            this.displayedValue = displayedValue;
        }

        public ComboItemDTO(IComboItem comboItem) {
            this.displayAsTarget = false;
            this.targetValue = comboItem.getTargetValue();
            this.targetId = comboItem.getTargetId();
            this.displayedValue = comboItem.getDisplayedValue();
            this.targetCursorPosition = comboItem.getTargetCursorPosition();
        }
    }

    @Getter
    @Setter
    @XMLProperty(value = ATTR_OPEN_ON_FOCUS, defaultValue = "true")
    @DesignerXMLProperty(functionalArea = BEHAVIOR, priority = 86)
    @DocumentedComponentAttribute(defaultValue = "true", value = "Should prompt window be opened when field gains focus.")
    private Boolean openOnFocus;

    @Setter
    @Getter
    @XMLProperty(defaultValue = "0")
    @DesignerXMLProperty(functionalArea = DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR)
    @DocumentedComponentAttribute(value = "Delay onInput action for specific miliseconds. Value must be between 0 and 10000.", defaultValue = "0")
    private Integer onInputTimeout;

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
    protected int selectedItemIndex;

    @Getter
    protected String rawValue;

    @JsonIgnore
    protected String filterText = "";

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = FILTER_TEXT)
    @DesignerXMLProperty(previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 120, functionalArea = SPECIFIC)
    @DocumentedComponentAttribute(boundable = true, value = "Binding represents value of filter text")
    private ModelBinding filterTextBinding;

    @JsonIgnore
    protected MultiValueMap<String, Object> values = new LinkedMultiValueMap();

    @JsonIgnore
    protected MultiValueMap<String, Object> filteredObjectValues = new LinkedMultiValueMap();

    @Getter
    protected MultiValueMap<String, ComboItemDTO> filteredValues = new LinkedMultiValueMap();

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = VALUES_ATTR)
    @DesignerXMLProperty(commonUse = true, allowedTypes = {Collection.class, MultiValueMap.class, String.class}, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT, priority = 81)
    private ModelBinding valuesBinding;

    @Getter
    @Setter
    protected boolean cleared = false;

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

    @Getter
    private boolean emptyValue;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(EMPTY_VALUE_ATTR)
    @DesignerXMLProperty(functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    @DocumentedComponentAttribute(defaultValue = "false", value = "Defines if value passed can be empty", boundable = true)
    private ModelBinding<Boolean> emptyValueBinding;

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

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(defaultValue = "false", value = "Determines if multiselect is enabled in combo. If multiselect is set to true, value has to be set to Collection.")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 10)
    protected boolean multiselect;

    @Getter
    protected String multiselectRawValue;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = FREE_TYPING)
    @DocumentedComponentAttribute(boundable = true, defaultValue = "false", value = "Defines if new values could be typed be user.  Binding changes may not be respected after initially showing this control.")
    private ModelBinding<Boolean> freeTypingBinding;

    protected Class<?> modelType = String.class;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = CURSOR)
    @DesignerXMLProperty(allowedTypes = Integer.class, functionalArea = DesignerXMLProperty.PropertyFunctionalArea.CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Binding represents cursor from model of Form, used inside of '{}', like {model}.")
    protected ModelBinding cursorBinding;

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

    @Getter
    protected Integer cursor = 0;

    public Combo(Form form) {
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
        Boolean addedTag = valueChange.getBooleanAttribute(ADDED_TAG);

        if (textObj != null && textObj.equals("") && !this.multiselect) {
            this.cleared = true;
            this.filterText = "";
            processFiltering(this.filterText);
            this.selectedItemIndex = -1;
            this.selectedItem = null;
            this.rawValue = null;
            changeSelectedItemBinding();
        } else if (textObj != null) {
            String text = (String) textObj;
            this.filterText = text;
            processFiltering(text);
            firstLoad = false;
            if (!isMultiselect()) {
                selectItemByFilterText();
                changeSelectedItemBinding();
            }
            // if free typing is allowed, use typed value as selected item
            if (freeTyping) {
                this.selectedItem = StringUtils.emptyToNull(text);
                if (isMultiselect() && Boolean.TRUE.equals(addedTag) && !StringUtils.isNullOrEmpty(this.rawValue)) {
                    this.selectedItem = this.rawValue;
                    changeSelectedItemBinding();
                } else {
                    this.rawValue = (String) this.selectedItem;
                }
                if (!isMultiselect()) {
                    changeSelectedItemBinding();
                }
            }
            updateFilterTextBinding();
        }
        Boolean cleared = valueChange.getBooleanAttribute(CLEARED);
        if (cleared != null && cleared && textObj == null) {
            this.cleared = cleared;
            this.filterText = "";
            processFiltering(this.filterText);
            this.selectedItemIndex = -1;
            this.selectedItem = null;
            this.rawValue = null;
            this.multiselectRawValue = null;
            changeSelectedItemBinding();
            updateFilterTextBinding();
        } else if (valueChange.hasAttributeChanged(SELECTED_INDEX_ATTR)) {
            this.cleared = false;
            String key = valueChange.getStringAttribute(SELECTED_INDEX_GROUP_ATTR);
            this.selectedItemIndex = valueChange.getIntAttribute(SELECTED_INDEX_ATTR);
            this.selectedItem = (this.selectedItemIndex >= 0) ? this.filteredObjectValues.get(key).get(selectedItemIndex) : null;
            changeSelectedItemBinding();
            this.rawValue = (!isMultiselect() && selectedItem != null) ? toRawValue(selectedItem) : null;
            this.multiselectRawValue = (isMultiselect() && selectedItem != null) ? toRawValue(selectedItem) : null;
            this.filterText = rawValue != null ? rawValue : "";
            processFiltering(this.filterText);
            updateFilterTextBinding();
        }
        if (valueChange.hasAttributeChanged(REMOVED_INDEX_ATTR)) {
            int removedIndexAttr = valueChange.getIntAttribute(REMOVED_INDEX_ATTR);
            List multiSelected = (List) getModelBinding().getBindingResult().getValue();
            multiSelected.remove(removedIndexAttr);
            this.selectedItem = new ArrayList<>(multiSelected);
            this.rawValue = (!isMultiselect() && selectedItem != null) ? toRawValue(selectedItem) : null;
            this.multiselectRawValue = (isMultiselect() && selectedItem != null) ? toRawValue(selectedItem) : null;
        }

        if (cursorBinding != null) {
            Integer cursor = valueChange.getIntAttribute(CURSOR);
            if (cursor != null && !Objects.equals(this.cursor, cursor)) {
                this.updateBindingForValue(cursor, cursorBinding, cursorBinding.getBindingExpression(), this.getOptionalFormatter());
                this.cursor = cursor;
            }
        }
    }

    protected void updateFilterTextBinding() {
        if (this.filterTextBinding != null) {
            updateBindingForValue(this.filterText, filterTextBinding, this.filterText);
        }
    }

    protected void selectItemByFilterText() {
        if (filteredObjectValues.size() == 1 && filteredObjectValues.entrySet().iterator().next().getValue().size() == 1) {
            for (Map.Entry<String, List<ComboItemDTO>> entry : collectValues(filteredObjectValues).entrySet()) {
                for (ComboItemDTO item : entry.getValue()) {
                    if (Objects.equals(this.filterText, item.isDisplayAsTarget() ? item.getTargetValue() : item.getDisplayedValue())) {
                        this.selectedItemIndex = entry.getValue().indexOf(item);
                        this.selectedItem = filteredObjectValues.get(entry.getKey()).get(this.selectedItemIndex);
                        this.rawValue = toRawValue(this.selectedItem);
                        return;
                    }
                }
            }
        }
    }

    //maybe instead of supporting selectItemBinding, it should use modelBinding from BaseInputField
    @Override
    public void validate() {
        if (this.getModelBinding() != null && this.getModelBinding().getBindingResult() != null) {
            if (!multiselect && !Objects.equals(StringUtils.nullToEmpty(this.filterText), StringUtils.nullToEmpty(this.rawValue))) {
                this.validConversion = false;
            } else {
                this.validConversion = true;
            }
            ValidationManager<Combo> vm = ValidationFactory.getInstance().getComboValidationProcess();
            List<ConstraintViolation<Combo>> formComponentValidationResult = vm.validate(this);
            IValidationResults validationResults = getForm().getAbstractUseCase().getUserSession().getValidationResults();
            BindingResult bindingResult = this.getModelBinding().getBindingResult();
            formComponentValidationResult.stream().forEach(x ->
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


    private String getFormatterName() {
        Optional<String> formatter = getOptionalFormatter();
        return (formatter.isPresent()) ? formatter.get() : "";
    }

    protected void changeSelectedItemBinding() {
        if (getModelBinding() != null) {
            if (isMultiselect() && getModelBinding().getBindingResult() != null) {
                if (getModelBinding().getBindingResult().getValue() != null) {
                    if (this.selectedItem != null) {
                        ((List) getModelBinding().getBindingResult().getValue()).add(selectedItem);
                    } else {
                        ((List) getModelBinding().getBindingResult().getValue()).clear();
                    }
                } else {
                    getModelBinding().setValue(selectedItem);
                }
                this.selectedItem = new ArrayList<>((List) getModelBinding().getBindingResult().getValue());
            } else {
                if (selectedItem instanceof IComboItem) {
                    getModelBinding().setValue(((IComboItem) selectedItem).getTargetValue());
                }
                else {
                    getModelBinding().setValue(selectedItem);
                }
            }
        }
    }

    protected void processFiltering(String text) {
        Map<String, List<Object>> filtered = values.entrySet()
                .stream()
                .filter(a -> a.getValue().stream().anyMatch(b -> filterFunction.test(b, text)))
                .collect(Collectors.toMap(Map.Entry::getKey, p -> p.getValue().stream().filter(s -> filterFunction.test(s, text)).collect(Collectors.toList())));
        filteredObjectValues.clear();
        filteredObjectValues.putAll(filtered);
        filterInvoked = true;
    }

    @Override
    public ElementChanges updateView() {
        final ElementChanges elementChanges = super.updateView();
        boolean selectedBindingChanged = elementChanges.getChangedAttributes().containsKey(RAW_VALUE_ATTR);

        if (freeTypingBinding != null) {
            freeTyping = freeTypingBinding.resolveValueAndAddChanges(this, elementChanges, freeTyping, FREE_TYPING);
        }
        if (emptyValueBinding != null) {
            emptyValue = emptyValueBinding.resolveValueAndAddChanges(this, elementChanges, emptyValue, EMPTY_VALUE_ATTR);
        }
        if (this.cleared) {
            this.filterText = "";
            updateFilterTextBinding();
        }
        processFilterTextBinding(elementChanges);
        setFilterFunction();
        refreshAvailability(elementChanges);
        boolean valuesChanged = processValuesBinding();
        if (selectedBindingChanged || valuesChanged) {
            processFiltering(this.filterText);
        }
        processFilterBinding(elementChanges, valuesChanged);
        processLabelBinding(elementChanges);
        processCursorBinding(this, elementChanges);

        this.prepareComponentAfterValidation(elementChanges);

        if (elementChanges.containsAnyChanges()) {
            refreshView();
        }
        this.cleared = false;

        return elementChanges;
    }

    private void processFilterTextBinding(ElementChanges elementChanges) {
        if (getModelBinding() == null || getModelBinding().getBindingResult() == null || getModelBinding().getBindingResult().getValue() == null) {
            if (getFilterTextBinding() != null) {
                BindingResult filterTextResult = getFilterTextBinding().getBindingResult();
                if (!Objects.equals(filterTextResult.getValue(), this.filterText)) {
                    this.filterText = StringUtils.nullToEmpty((String) filterTextResult.getValue());
                    this.rawValue = filterText;
                    elementChanges.addChange(RAW_VALUE_ATTR, this.rawValue);
                    processFiltering(this.filterText);
                    if (!StringUtils.isNullOrEmpty(this.filterText)) {
                        this.cursor = this.filterText.length();
                        if (cursorBinding != null) {
                            this.updateBindingForValue(cursor, cursorBinding, cursorBinding.getBindingExpression(), this.getOptionalFormatter());
                        }
                    }
                }
            }
        }

    }

    private void setFilterFunction() {
        BindingResult filterBindingResult = getFilterFunctionBinding() != null ? getFilterFunctionBinding().getBindingResult() : null;
        if (filterBindingResult != null) {
            this.filterFunction = (BiPredicate<Object, String>) filterBindingResult.getValue();
        } else {
            this.filterFunction = (model, value) -> objectToString(model).toLowerCase().contains(value.toLowerCase());
        }
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
                        MultiValueMap<String, Object> newValues = new LinkedMultiValueMap();
                        newValues.put("", Arrays.stream(allValues).collect(Collectors.toList()));
                        if (!Objects.equals(newValues, values)) {
                            values.clear();
                            values.putAll(newValues);
                            return true;
                        }
                    }
                } else if (value instanceof List) {
                    List collection = (List) value;
                    if (!CollectionUtils.isEmpty(collection)) {
                        this.modelType = collection.stream().findFirst().get().getClass();
                    }
                    MultiValueMap<String, Object> newValues = new LinkedMultiValueMap();
                    newValues.put("", new LinkedList<>(collection));
                    if (!Objects.equals(newValues, values)) {
                        values.clear();
                        values.putAll(newValues);
                        return true;
                    }
                } else if (value instanceof MultiValueMap && !Objects.equals(value, values)) {
                    MultiValueMap<String, Object> mapFromBinding = (MultiValueMap<String, Object>) value;
                    resolveModelType(mapFromBinding);
                    mapFromBinding.entrySet().stream().forEach(entry -> this.values.put(entry.getKey(), new LinkedList<>(entry.getValue())));
                    return true;
                }
            }
        }
        return valuesChanged;
    }

    private void resolveModelType(MultiValueMap<String, Object> mapFromBinding) {
        Set<Map.Entry<String, List<Object>>> entries = mapFromBinding.entrySet();
        for (Map.Entry<String, List<Object>> entry : entries) {
            List<Object> values = entry.getValue();
            if (!CollectionUtils.isEmpty(values)) {
                this.modelType = values.get(0).getClass();
                return;
            }
        }
    }

    protected boolean processFilterBinding(ElementChanges elementChanges, boolean valuesChanged) {
        if (!preload && firstLoad && StringUtils.isNullOrEmpty(this.filterText) && !valuesChanged) {
            return false;
        }
        if (filterInvoked || valuesChanged) {
            this.filteredValues = collectValues(filteredObjectValues);
            elementChanges.addChange(FILTERED_VALUES, this.filteredValues);
            filterInvoked = false;
            return true;
        }
        return false;
    }

    @Override
    protected boolean processValueBinding(ElementChanges elementChanges) {
        if (getModelBinding() != null) {
            BindingResult selectedBindingResult = getModelBinding().getBindingResult();
            if (selectedBindingResult != null) {
                Object value = selectedBindingResult.getValue();
                if (!Objects.equals(value, selectedItem)) {
                    this.selectedItem = value;
                    if (isMultiselect()) {
                        this.selectedItem = new ArrayList<>((List) value);
                        this.multiselectRawValue = toRawValue(value);
                        elementChanges.addChange(MULTISELECT_RAW_VALUE_ATTR, this.multiselectRawValue);
                    } else {
                        this.rawValue = toRawValue(value);
                        elementChanges.addChange(RAW_VALUE_ATTR, this.rawValue);
                    }
                    this.filterText = rawValue != null ? rawValue : "";
//                    this.rawValue = this.convertValueToString(value, getOptionalFormatter().orElse(null));
                    updateFilterTextBinding();
                    return true;
                }
            }
        }
        return false;
    }

    protected MultiValueMap<String, ComboItemDTO> collectValues(MultiValueMap<String, Object> valuesToConvert) {
        MultiValueMap<String, ComboItemDTO> filteredConvertedValues = new LinkedMultiValueMap<>();
        AtomicReference<Long> idx = new AtomicReference<>(0L);
        valuesToConvert.forEach((key, values) -> values.forEach(value -> {
            ComboItemDTO item;
            if (value instanceof IComboItem) {
                item = new ComboItemDTO((IComboItem) value);
                item.targetId = idx.get();
            } else {
                item = new ComboItemDTO(objectToString(value), idx.get());
            }
            idx.getAndSet(idx.get() + 1);
            filteredConvertedValues.add(key, item);
        }));
        return filteredConvertedValues;
    }

    protected String toRawValue(Object s) {
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

    protected String objectToString(Object s) {
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

    //todo - temporary solution, remove Spel in future
    private String objectToStringAsDisplayExpresssion(Object item) {
        if (item == null) {
            return null;
        }
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

    private boolean processCursorBinding(FormElement formElement, ElementChanges elementChanges) {
        if (getCursorBinding() != null) {
            Integer oldValue = getCursor();
            if (getCursorBinding() != null) {
                BindingResult<Integer> bindingResult = getCursorBinding().getBindingResult();
                if (bindingResult != null) {
                    if (bindingResult.getValue() != null) {
                        Integer newValue = Integer.valueOf(bindingResult.getValue());
                        if (!formElement.areValuesTheSame(newValue, oldValue)) {
                            formElement.refreshView();
                            elementChanges.addChange(CURSOR, newValue);
                            this.cursor = newValue;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public Combo createNewSameComponent() {
        return new Combo(getForm());
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, BaseInputField baseClone) {
        super.doCopy(table, iteratorReplacements, baseClone);
        Combo clone = (Combo) baseClone;

        clone.setOnInput(table.getRowBinding(this.getOnInput(), clone, iteratorReplacements));
        clone.setOnEmptyValue(table.getRowBinding(this.getOnEmptyValue(), clone, iteratorReplacements));
        clone.setOnSpecialKey(table.getRowBinding(this.getOnSpecialKey(), clone, iteratorReplacements));
        clone.setOnDblSpecialKey(table.getRowBinding(this.getOnDblSpecialKey(), clone, iteratorReplacements));
        clone.setValuesBinding(table.getRowBinding(this.getValuesBinding(), clone, iteratorReplacements));
        clone.setFilterFunctionBinding(table.getRowBinding(this.getFilterFunctionBinding(), clone, iteratorReplacements));
        clone.setDisplayFunctionBinding(table.getRowBinding(this.getDisplayFunctionBinding(), clone, iteratorReplacements));
        clone.setDisplayExpression(getDisplayExpression());
        clone.setEmptyValueBinding(table.getRowBinding(this.getEmptyValueBinding(), clone, iteratorReplacements));
        clone.setPreload(this.isPreload());
        clone.setFormatter(getFormatter());
        clone.setFreeTypingBinding(table.getRowBinding(this.getFreeTypingBinding(), clone, iteratorReplacements));
        clone.setCursorBinding(table.getRowBinding(this.getCursorBinding(), clone, iteratorReplacements));
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
