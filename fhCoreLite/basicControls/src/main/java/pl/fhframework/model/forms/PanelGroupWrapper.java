package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.IDesignerEventListener;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import java.util.Objects;
import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.LOOK_AND_STYLE;

@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Group.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.ARRANGEMENT, documentationExample = true, value = "PanelGroupWrapper component responsible for the grouping of sub-elements(PanelGroups) and allow to open/close all", icon = "fa fa-caret-down")
public class PanelGroupWrapper extends Accordion {

    public static final String TOGGLE_ALL = "toggleAll";

    @Getter
    private Boolean toggleAll = null;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(TOGGLE_ALL)
    @DocumentedComponentAttribute(boundable = true)
    @DesignerXMLProperty(priority = 84, functionalArea = BEHAVIOR)
    private ModelBinding<Boolean> toggleAllBinding;

    public PanelGroupWrapper(Form form) {
        super(form);
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();

        if (toggleAllBinding != null) {
            BindingResult<Boolean> bindingResult = toggleAllBinding.getBindingResult();
            if (bindingResult != null) {
                Boolean newValue = bindingResult.getValue();
                if (!Objects.equals(newValue, toggleAll)) {
                    elementChanges.addChange(TOGGLE_ALL, newValue);
                    this.toggleAll = newValue;
                }
            }
        }
        return elementChanges;
    }

}
