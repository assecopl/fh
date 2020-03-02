package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.attributes.AttributeHolder;
import pl.fhframework.model.forms.attributes.AttributeHolderBuilder;
import pl.fhframework.model.forms.attributes.clock.DayNamesAttribute;
import pl.fhframework.model.forms.optimized.ColumnOptimized;
import pl.fhframework.model.forms.widgets.Widget;

@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Widget.class, Group.class}, canBeDesigned = true)
//@DocumentedComponent(value="Label component is responsible for displaying value.", icon = "fa fa-font")
public class Clock extends FormElement implements Boundable {

    @JsonUnwrapped
    @Getter
    private AttributeHolder attributeHolder;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(DayNamesAttribute.DAY_NAMES_ATTR)
    private ModelBinding dayNames;

    public Clock(Form form) {
        super(form);
    }

    public void init() {
        super.init();
        attributeHolder = new AttributeHolderBuilder()
                .attribute(new DayNamesAttribute(getForm(), this, dayNames))
                .build();
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        attributeHolder.updateView(this, elementChanges);
        return elementChanges;
    }
}