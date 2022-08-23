package pl.fhframework.model.forms;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.core.forms.IHasBoundableLabel;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.forms.config.BasicControlsConfiguration;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.designer.ButtonStyleFixedValuesProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.*;

/**
 * Class representing xml component of Button. Every field represents xml attribute of button tag.
 * Example {@code <Button label="button_1"/>}. Every field is parsed as json for javascript. If field should
 * be ingored by JSON, use <code>@JsonIgnore</code>. There can be used any annotations for json
 * generator.
 */
@TemplateControl(tagName = "fh-button")
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Form.class, ButtonGroup.class, Group.class, Row.class, Footer.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.BUTTONS_AND_OTHER, documentationExample = true, value = "Button which represents HTML input", icon = "fa fa-square")
@DesignerControl(defaultWidth = 2)
public class Button extends FormElementWithConfirmationSupport implements TableComponent<Button>, Styleable, IHasBoundableLabel {

    public static final String ATTR_LABEL = "label";
    public static final String ATTR_STYLE = "style";
    public static final String ATTR_ON_CLICK = "onClick";
    public static final String TYPE_NAME = "Button";

    @Getter
    @XMLProperty
    @DesignerXMLProperty(commonUse = true, functionalArea = BEHAVIOR, priority = 90)
    @DocumentedComponentAttribute(value = "If the button is clicked that method will be executed. Action is fired, while component is active.")
    private ActionBinding onClick;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_LABEL)
    @DesignerXMLProperty(commonUse = true, functionalArea = CONTENT, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, priority = 100)
    // todo; continue this concept on monday
    @DocumentedComponentAttribute(boundable = true, value = "Component label")
    private ModelBinding labelModelBinding;

    @Getter
    private String label;

    @Getter
    private Style style = Style.PRIMARY;

    @Getter
    @Setter
    @XMLProperty()
    @DocumentedComponentAttribute(defaultValue = "test", value = "test")
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 78, allowedTypes = String.class)
    private String leftPadding = null;

    @Getter
    @Setter
    @JsonIgnore
    @XMLProperty(value = ATTR_STYLE)
    @DocumentedComponentAttribute(boundable = true, defaultValue = "primary", value = "Determines style of a Button. It is possible to select one of six Bootstrap classes: default, primary, success, info, warning, danger or bind it with variable.")
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 77, fixedValuesProvider = ButtonStyleFixedValuesProvider.class)
    private ModelBinding styleModelBinding;


    @Getter
    @Setter
    @XMLProperty()
    @DocumentedComponentAttribute(defaultValue = "false")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 78, allowedTypes = boolean.class)
    private boolean reCAPTCHA = false;

    @Getter
    @Setter
    private String captchaSiteKey = null;

    @Getter
    @Setter
    @JsonIgnore
    private String captchaServerKey = null;

    public Button(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        calculateAndSetDefaultSize();
        if (this.reCAPTCHA == true) {
            this.setCaptchaSiteKey(BasicControlsConfiguration.getInstance().getCaptchaSiteKey());
            this.setCaptchaServerKey(BasicControlsConfiguration.getInstance().getCaptchaServerKey());
        }
    }

    private void calculateAndSetDefaultSize() {
        if (!StringUtils.hasText(this.getWidth())) {
            this.setWidth("md-3");
        }
    }

    @Override
    public String getType() {
        return TYPE_NAME;
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
    public ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        if (labelModelBinding != null) {
            Object newLabelValueObj = labelModelBinding.getBindingResult().getValue();
            String newLabelValue = newLabelValueObj != null ? newLabelValueObj.toString() : null;
            if (!areValuesTheSame(newLabelValue, label)) {
                //refreshView();
                this.label = newLabelValue;
                elementChanges.addChange(ATTR_LABEL, this.label);
            }
            boolean refreshView = processStyleBinding(elementChanges, false);
            if (refreshView) {
                refreshView();
            }
        }
        return elementChanges;
    }

    protected boolean processStyleBinding(ElementChanges elementChanges, boolean refreshView) {
        BindingResult labelBidingResult = styleModelBinding != null ? styleModelBinding.getBindingResult() : null;
        if (labelBidingResult != null) {
            String newLabelValue = this.convertBindingValueToString(labelBidingResult);
            if (!areValuesTheSame(newLabelValue, style.toValue())) {
                this.style = Style.forValue(newLabelValue);
                elementChanges.addChange(ATTR_STYLE, this.style);
                refreshView = true;
            }
        }
        return refreshView;
    }

    @Override
    public Button createNewSameComponent() {
        return new Button(getForm());
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, Button clonedButton) {
        TableComponent.super.doCopy(table, iteratorReplacements, clonedButton);
        clonedButton.setLeftPadding(this.getLeftPadding());
        clonedButton.setOnClick(table.getRowBinding(this.getOnClick(), clonedButton, iteratorReplacements));
        clonedButton.setLabelModelBinding(table.getRowBinding(this.getLabelModelBinding(), clonedButton, iteratorReplacements));
        clonedButton.setStyleModelBinding(table.getRowBinding(this.getStyleModelBinding(), clonedButton, iteratorReplacements));
        clonedButton.setAriaLabelBinding(table.getRowBinding(this.getAriaLabelBinding(), clonedButton, iteratorReplacements));
    }

    @Override
    protected List<ActionBinding> getAvailablityAffectingActions() {
        return Arrays.asList(onClick);
    }

    public IActionCallbackContext setOnClick(IActionCallback callback) {
        return CallbackActionBinding.createAndSet(callback, this::setOnClick);
    }

    public void setOnClick(ActionBinding onClick) {
        this.onClick = onClick;
    }
}
