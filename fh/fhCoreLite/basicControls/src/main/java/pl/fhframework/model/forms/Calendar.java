package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.AbstractMap.SimpleImmutableEntry;
import static java.util.Map.Entry;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.SPECIFIC;

@DocumentedComponent(value = "Component responsible for displaying calendar,only date could be set in this field", icon = "fa fa-calendar")
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Group.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
public class Calendar extends BaseInputField {

    private static final String VALUES_ATTR = "values";
    private static final String MIN_DATE_ATTR = "minDate";
    private static final String MAX_DATE_ATTR = "maxDate";
    private static final String CHANGE_MONTH_ATTR = "changeMonth";
    private static final String CHANGE_YEAR_ATTR = "changeYear";
    private static final String BLOCKED_DATES_ATTR = "blockedDates";
    private static final String DELIMITER = "\\|";

    @Getter
    @Setter
    @XMLProperty
    private String currentDate;

    @Getter
    private String minDate;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(MIN_DATE_ATTR)
    @DocumentedComponentAttribute(boundable = true, value = "Minimum date for allowed selection.")
    private ModelBinding minDateBinding;

    @Getter
    private String maxDate;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(MAX_DATE_ATTR)
    @DocumentedComponentAttribute(boundable = true, value = "Maximum date for allowed selection.")
    private ModelBinding maxDateBinding;

    @Getter
    private List<String> blockedDates;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(BLOCKED_DATES_ATTR)
    @DocumentedComponentAttribute(boundable = true, value = "Collection of dates blocked for selection.")
    private ModelBinding blockedDatesBinding;

    @Getter
    private MultiValueMap<Entry<String, String>, String> values = new LinkedMultiValueMap();

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(VALUES_ATTR)
    @DocumentedComponentAttribute(boundable = true, value = "Collection of selected values of different types to be displayed.")
    private ModelBinding valuesBinding;

    @Getter
    private boolean changeMonth = false;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(CHANGE_MONTH_ATTR)
    @DesignerXMLProperty(priority = 5, functionalArea = SPECIFIC)
    @DocumentedComponentAttribute(boundable = true, defaultValue = "false", value = "User can define if calendar displays menu for month selection.")
    private ModelBinding<Boolean> changeMonthBinding;

    @Getter
    private boolean changeYear = false;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(CHANGE_YEAR_ATTR)
    @DesignerXMLProperty(priority = 4, functionalArea = SPECIFIC)
    @DocumentedComponentAttribute(boundable = true, defaultValue = "false", value = "User can define if calendar displays menu for year selection.")
    private ModelBinding<Boolean> changeYearBinding;

    public Calendar(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();

        this.currentDate = this.convertValueToString(LocalDate.now());

    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        elementChanges.setFormId(getForm().getId());
        elementChanges.setFormElementId(this.getId());

        if (this.changeMonthBinding != null) {
            this.changeMonth = this.changeMonthBinding.resolveValueAndAddChanges(this, elementChanges, this.changeMonth, CHANGE_MONTH_ATTR);
        }

        if (this.changeYearBinding != null) {
            this.changeYear = this.changeYearBinding.resolveValueAndAddChanges(this, elementChanges, this.changeYear, CHANGE_YEAR_ATTR);
        }

        boolean valuesChanged = processValuesBinding();
        if (valuesChanged) {
            elementChanges.addChange(VALUES_ATTR, this.values);
        }

        boolean minDateValueChanged = processMinDateBinding();
        if (minDateValueChanged) {
            elementChanges.addChange(MIN_DATE_ATTR, this.minDate);
        }

        boolean maxDateValueChanged = processMaxDateBinding();
        if (maxDateValueChanged) {
            elementChanges.addChange(MAX_DATE_ATTR, this.maxDate);
        }

        boolean blockedDatesValueChanged = processBlockedDatesBinding();
        if (blockedDatesValueChanged) {
            elementChanges.addChange(BLOCKED_DATES_ATTR, this.blockedDates);
        }

        if (valuesChanged || minDateValueChanged || maxDateValueChanged || blockedDatesValueChanged) {
            refreshView();
        }

        return elementChanges;
    }

    private boolean processMinDateBinding() {
        boolean minDateValueChanged = false;
        if (minDateBinding != null) {
            BindingResult minDateBindingResult = minDateBinding.getBindingResult();
            if (minDateBindingResult != null) {
                Object newValue = minDateBindingResult.getValue();
                if (newValue instanceof String) {
                    this.minDate = (String) newValue;
                    minDateValueChanged = true;
                } else if (newValue instanceof LocalDate) {
                    this.minDate = this.convertValueToString(newValue);
                    minDateValueChanged = true;
                }
            }
        }

        return minDateValueChanged;
    }

    private boolean processMaxDateBinding() {
        boolean maxDateValueChanged = false;
        if (maxDateBinding != null) {
            BindingResult maxDateBindingResult = maxDateBinding.getBindingResult();
            if (maxDateBindingResult != null) {
                Object newValue = maxDateBindingResult.getValue();
                if (newValue instanceof String) {
                    this.maxDate = (String) newValue;
                    maxDateValueChanged = true;
                } else if (newValue instanceof LocalDate) {
                    this.maxDate = this.convertValueToString(newValue);
                    maxDateValueChanged = true;
                }
            }
        }

        return maxDateValueChanged;
    }

    private boolean processBlockedDatesBinding() {
        boolean blockedDatesValueChanged = false;
        if (blockedDatesBinding != null) {
            BindingResult blockedDatesValueBindingResult = blockedDatesBinding.getBindingResult();
            if (blockedDatesValueBindingResult != null) {
                Object newValue = blockedDatesValueBindingResult.getValue();
                List<String> newValues;

                if (newValue instanceof String) {
                    String valuesAsString = (String) newValue;
                    String[] allValues = valuesAsString.split(DELIMITER);
                    if (allValues.length > 0) {
                        newValues = Arrays.stream(allValues).collect(Collectors.toList());
                        if (!Objects.equals(newValues, blockedDates)) {
                            this.blockedDates = newValues;
                            blockedDatesValueChanged = true;
                        }
                    }
                } else if (newValue instanceof Collection) {
                    newValues = ((Collection<?>) newValue).stream().map(p -> this.convertValueToString(p)).collect(Collectors.toList());
                    if (!Objects.equals(newValues, blockedDates)) {
                        this.blockedDates = newValues;
                        blockedDatesValueChanged = true;
                    }
                }
            }
        }

        return blockedDatesValueChanged;
    }

    private boolean processValuesBinding() {
        boolean valuesChanged = false;
        if (valuesBinding != null) {
            BindingResult valuesBindingResult = valuesBinding.getBindingResult();
            if (valuesBindingResult != null) {
                Object newValue = valuesBindingResult.getValue();
                MultiValueMap<Entry<String, String>, String> newValues = new LinkedMultiValueMap();

                if (newValue instanceof String) {
                    String valuesAsString = (String) newValue;
                    String[] allValues = valuesAsString.split(DELIMITER);
                    if (allValues.length > 0) {
                        newValues.put(new SimpleImmutableEntry<>("", ""), Arrays.stream(allValues).collect(Collectors.toList()));
                        if (!Objects.equals(newValues, values)) {
                            this.values = newValues;
                            return true;
                        }
                    }
                } else if (newValue instanceof Collection) {
                    newValues.put(new SimpleImmutableEntry<>("", ""), ((Collection<?>) newValue).stream().map(p -> this.convertValueToString(p)).collect(Collectors.toList()));
                    if (!Objects.equals(newValues, values)) {
                        this.values = newValues;
                        return true;
                    }
                } else if (newValue instanceof MultiValueMap) {
                    newValues = convertValues((MultiValueMap<Entry<String, String>, LocalDate>) newValue);
                    if (!Objects.equals(newValues, values)) {
                        this.values = newValues;
                        return true;
                    }
                }
            }
        }

        return valuesChanged;
    }

    private void setBinding(ModelBinding valuesBinding, ModelBinding minDateBinding, ModelBinding maxDateBinding, ModelBinding blockedDatesBinding) {
        this.valuesBinding = valuesBinding;
        this.minDateBinding = minDateBinding;
        this.maxDateBinding = maxDateBinding;
        this.blockedDatesBinding = blockedDatesBinding;
    }

    private MultiValueMap<Entry<String, String>, String> convertValues(MultiValueMap<Entry<String, String>, LocalDate> value) {
        MultiValueMap<Entry<String, String>, String> convertedValues = new LinkedMultiValueMap<>();
        convertedValues.putAll(value.entrySet()
                .stream()
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue().stream().map(s -> this.convertValueToString(s)).collect(Collectors.toList()))));
        return convertedValues;
    }

    @Override
    public Calendar createNewSameComponent() {
        return new Calendar(getForm());
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, BaseInputField baseClone) {
        super.doCopy(table, iteratorReplacements, baseClone);
        Calendar clone = (Calendar) baseClone;
        clone.setCurrentDate(getCurrentDate());
        clone.setMinDateBinding(table.getRowBinding(getMinDateBinding(), clone, iteratorReplacements));
        clone.setMaxDateBinding(table.getRowBinding(getMaxDateBinding(), clone, iteratorReplacements));
        clone.setBlockedDatesBinding(table.getRowBinding(getBlockedDatesBinding(), clone, iteratorReplacements));
        clone.setValuesBinding(table.getRowBinding(getValuesBinding(), clone, iteratorReplacements));
    }
}
