package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.forms.attribute.IconAlignment;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;
import pl.fhframework.tools.loading.IBodyXml;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.*;

/**
 * Class representing xml component of OutputLabel. Every field represents xml attribute of
 * outputLabel tag. Example {@code <OutputLabel value="{value_1}"/>}. Every field is parsed as json for
 * javascript. If field should be ingored by JSON, use <code>@JsonIgnore</code>. There can be used
 * any annotations for json generator.
 */
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, value = "Label component is responsible for displaying value.", icon = "fa fa-font")
@DesignerControl(defaultWidth = 2)
public class OutputLabel extends FormElement implements TableComponent<OutputLabel>, Iconable, IBodyXml {

    public static final String ATTR_VALUE = "value";
    public static final String ATTR_ON_CLICK = "onClick";

    private static final String FORMATTER_ATTR = "formatter";
    private static final String ICON_ATTR = "icon";

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ICON_ATTR)
    @DocumentedComponentAttribute(boundable = true, value = "Icon id. Please refer to http://fontawesome.io/icons/ for all available icons.")
    @DesignerXMLProperty(priority = 83, functionalArea = LOOK_AND_STYLE)
    private ModelBinding<String> iconBinding;

    @Getter
    private String icon;

    @Getter
    @Setter
    @XMLProperty(defaultValue = "before")
    @DesignerXMLProperty(priority = 84, functionalArea = LOOK_AND_STYLE)
    @DocumentedComponentAttribute(defaultValue = "before", boundable = true, value = "Icon alignment - possible values are before or after. Final alignment depends of component where this attribute is used.")
    private IconAlignment iconAlignment;

    @Getter
    private String value;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_VALUE)
    @DesignerXMLProperty(functionalArea = CONTENT, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, priority = 95)
    @DocumentedComponentAttribute(boundable = true, value = "Represents text value for created component.")
    private ModelBinding valueBinding;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = FORMATTER_ATTR)
    @DocumentedComponentAttribute(value = "Id of formatter which will format object to String. It must be consistent with value of pl.fhframework.formatter.FhFormatter annotation.")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 90)
    private String formatter;

    @Getter
    @XMLProperty
    @DesignerXMLProperty(commonUse = true, functionalArea = BEHAVIOR, priority = 90)
    @DocumentedComponentAttribute(value = "If the label is clicked that method will be executed. Action is fired, while component is active.")
    private ActionBinding onClick;

    public OutputLabel(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        calculateAndSetDefaultSize();
    }

    private void calculateAndSetDefaultSize() {
        if (!StringUtils.hasText(this.getWidth())) {
            this.setWidth(null);
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        processValue(elementChanges);
        return elementChanges;
    }

    private void processValue(ElementChanges elementChanges) {
        BindingResult bindingResult = valueBinding != null ? valueBinding.getBindingResult() : null;
        if (bindingResult != null) {
            String newLabelValue = this.convertValueToString(bindingResult.getValue(), formatter);
            if (!areValuesTheSame(newLabelValue, value)) {
                this.refreshView();
                this.value = newLabelValue;
                elementChanges.addChange(ATTR_VALUE, this.value);
            }
        }
        if (iconBinding != null) {
            icon = resolveIconBinding(this, elementChanges);
        }
    }

    @Deprecated
    public void setValueBindingAdHoc(String binding) {
        setValueBinding(createAdHocModelBinding(binding));
    }

    @Override
    public OutputLabel createNewSameComponent() {
        return new OutputLabel(getForm());
    }

    public void doCopy(Table table, Map<String, String> iteratorReplacements, OutputLabel clone) {
        TableComponent.super.doCopy(table, iteratorReplacements, clone);
        clone.setIconBinding(table.getRowBinding(this.getIconBinding(), clone, iteratorReplacements));
        clone.setValueBinding(table.getRowBinding(this.getValueBinding(), clone, iteratorReplacements));
        clone.setFormatter(table.getRowBinding(this.getFormatter(), iteratorReplacements));
        clone.setOnClick(table.getRowBinding(this.getOnClick(), clone, iteratorReplacements));
    }

    @Override
    public void setBody(String body) {
        setValueBinding(createAdHocModelBinding(body));
        this.value = "body";
    }

    @Override
    public String getBodyAttributeName() {
        return ATTR_VALUE;
    }

    @Override
    public ModelBinding getIconBinding() {
        return iconBinding;
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (eventData.getEventType().equals(ATTR_ON_CLICK)) {
            return Optional.ofNullable(onClick);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    @Override
    protected List<ActionBinding> getAvailablityAffectingActions() {
        return Collections.singletonList(onClick);
    }

    public IActionCallbackContext setOnClick(IActionCallback callback) {
        return CallbackActionBinding.createAndSet(callback, this::setOnClick);
    }

    public void setOnClick(ActionBinding onClick) {
        this.onClick = onClick;
    }

    @Override
    public boolean isModificationEvent(String eventType) {
        if (ATTR_ON_CLICK.equals(eventType)) {
            return false;
        } else {
            return super.isModificationEvent(eventType);
        }
    }

}
