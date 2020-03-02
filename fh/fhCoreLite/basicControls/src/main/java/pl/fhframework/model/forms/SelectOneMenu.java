package pl.fhframework.model.forms;


import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

/**
 * Class representing xml component of SelectOneMenu. Every field represents xml attribute of
 * selectOneMenu tag.
 * <p>
 * Example {@code <SelectOneMenu onInput="onInputAction" list="values" value="{value_1}"
 * label="label_1:" required="true|false"/>}.
 * <p>
 * Every field is parsed as json for javascript. If field should be ingored by JSON, use
 * <code>@JsonIgnore</code>. There can be used any annotations for json generator.
 */
@TemplateControl(tagName = "fh-select-one-menu")
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, value = "Component responsible for displaying list of values, " +
        "with possibility of selecting only one value.", icon = "fa fa-caret-square-down")
@DesignerControl(defaultWidth = 3)
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Group.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
public class SelectOneMenu extends BaseInputListField {

    public SelectOneMenu(Form form) {
        super(form);
    }

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(defaultValue = "false", value = "Determines if selected value that has been removed from values collection should still be present in control.")
    @DesignerXMLProperty(priority = 90, functionalArea = BEHAVIOR)
    private boolean keepRemovedValue;

    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Determines empty value text displayed on list of options.")
    @DesignerXMLProperty(priority = 60, functionalArea = CONTENT)
    private String emptyLabelText;

    @Override
    public SelectOneMenu createNewSameComponent() {
        return new SelectOneMenu(getForm());
    }

    public String getModelBindingText() {
        return getModelBinding() == null ? "" : getModelBinding().getBindingExpression();
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, BaseInputField baseClone) {
        super.doCopy(table, iteratorReplacements, baseClone);
        SelectOneMenu clone = (SelectOneMenu) baseClone;
        clone.setKeepRemovedValue(isKeepRemovedValue());
    }

    @Override
    protected Object getChosenObjectAndSetIndex(String newValue) {
        this.newIndex = !StringUtils.isNullOrEmpty(newValue) ? Integer.parseInt(newValue) : -1;
        if (this.newIndex == -1) {
            return null;
        }
        Object value = presentedValues.get(this.newIndex);

        if (value == null || (newIndex == 0 && value.equals(emptyLabelText))) {
            return null;
        }

        return (this.newIndex >= 0) ? value : null;
    }

    @Override
    protected List<?> calculateBindingValues(List<?> value) {
        List<Object> values = new LinkedList<>(value);

        if (isEmptyLabel()) {
            values.add(0, this.emptyLabelText);
        }
        if (isKeepRemovedValue()) {
            BindingResult bindingResult = getModelBinding().getBindingResult();
            if (bindingResult != null) {
                Object removedValue = bindingResult.getValue();
                if (!values.contains(removedValue) && removedValue != null) {
                    values.add(removedValue);
                }
            }
        }
        return values;
    }

    @Override
    protected String convertToRaw(BindingResult<?> bindingResult) {
        Object value = bindingResult == null ? null : bindingResult.getValue();
        if (value == null) {
            return "";
        }

        return convertMainValueToString(value, getOptionalFormatter());
    }
}
