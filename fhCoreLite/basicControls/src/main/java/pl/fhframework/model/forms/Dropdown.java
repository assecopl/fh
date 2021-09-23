package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.fhframework.core.forms.IHasBoundableLabel;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.binding.StaticBinding;
import pl.fhframework.model.dto.ElementChanges;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.designer.IDesignerEventListener;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

/**
 * Created by k.czajkowski on 20.01.2017.
 */
@TemplateControl(tagName = "fh-dropdown")
@Control(parents = {ButtonGroup.class, PanelGroup.class, Group.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DesignerControl(defaultWidth = 2)
@DocumentedComponent(category = DocumentedComponent.Category.INPUTS_AND_VALIDATION, documentationExample = true, value = "PanelGroup component responsible for the grouping of DropdownItems.", icon = "fa fa-caret-square-down")
public class Dropdown extends GroupingComponent<FormElement> implements Boundable, Styleable, IHasBoundableLabel, CompactLayout, IDesignerEventListener {

    public static final String ATTR_LABEL = "label";
    public static final String ATTR_STYLE = "style";
    public static final String DROPDOWN_DEFAULT_LABEL = "Dropdown";

    @Getter
    private String label = DROPDOWN_DEFAULT_LABEL;

    @Getter
    @Setter
    private Styleable.Style style = Styleable.Style.PRIMARY;

    @Getter
    @Setter
    @JsonIgnore
    @XMLProperty(value = ATTR_STYLE)
    @DocumentedComponentAttribute(boundable = true, defaultValue = "primary", value = "Determines style of a Dropdown. It is possible to select one of six Bootstrap classes: default, primary, success, info, warning, danger or bind it with variable.")
    private ModelBinding styleModelBinding;

    @Getter
    @Setter
    @JsonIgnore
    @XMLProperty(required = true, value = ATTR_LABEL)
    @DesignerXMLProperty(functionalArea = CONTENT, previewValueProvider = BindingExpressionDesignerPreviewProvider.class)
    @DocumentedComponentAttribute(boundable = true, value = "Component label")
    private ModelBinding labelModelBinding;

    public Dropdown(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        if (styleModelBinding != null) {
            BindingResult styleBidingResult = styleModelBinding.getBindingResult();
            if (styleBidingResult != null) {
                this.style = Styleable.Style.forValue(convertBindingValueToString(styleBidingResult));
            }
        }


        if (labelModelBinding != null) {
            BindingResult labelBidingResult = labelModelBinding.getBindingResult();
            this.label = labelBidingResult != null ? convertBindingValueToString(labelBidingResult) : DROPDOWN_DEFAULT_LABEL;
        }
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        processValue(elementChanges);
        processStyleBinding(elementChanges);
        return elementChanges;
    }

    private void processValue(ElementChanges elementChanges) {
        if (labelModelBinding != null) {
            BindingResult bindingResult = labelModelBinding.getBindingResult();
            if (bindingResult != null) {
                String newLabelValue = this.convertBindingValueToString(bindingResult);
                if (!areValuesTheSame(newLabelValue, label)) {
                    this.refreshView();
                    this.label = newLabelValue;
                    elementChanges.addChange(ATTR_LABEL, this.label);
                }
            }
        }
    }

    private void processStyleBinding(ElementChanges elementChanges) {
        if (styleModelBinding != null) {
            BindingResult labelBidingResult = styleModelBinding.getBindingResult();
            if (labelBidingResult != null) {
                String newLabelValue = this.convertBindingValueToString(labelBidingResult);
                if (!areValuesTheSame(newLabelValue, style.toValue())) {
                    this.style = Style.forValue(newLabelValue);
                    elementChanges.addChange(ATTR_STYLE, this.style);
                }
            }
        }
    }

    @Override
    public void onDesignerAddDefaultSubcomponent(SpacerService spacerService) {
        addSubcomponent(createExampleDropdownItem(getSubcomponents().size() + 1));
    }

    @Override
    public void onDesignerBeforeAdding(IGroupingComponent<?> parent, SpacerService spacerService) {
        addSubcomponent(createExampleDropdownItem(1));
        addSubcomponent(createExampleDropdownItem(2));
        addSubcomponent(createExampleDropdownItem(3));
    }

    private DropdownItem createExampleDropdownItem(int nameSuffix) {
        DropdownItem tab = new DropdownItem(getForm());
        tab.setModelBindingForValue(new StaticBinding<>("Dropdown item " + nameSuffix));
        tab.setGroupingParentComponent(this);
        tab.init();
        return tab;
    }
}
