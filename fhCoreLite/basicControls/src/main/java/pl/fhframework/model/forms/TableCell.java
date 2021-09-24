package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.TemplateControl;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.model.forms.attribute.HorizontalAlign;
import pl.fhframework.model.forms.attribute.VerticalAlign;

import java.util.List;
import java.util.function.Consumer;

/**
 * Component used to group other form's elements and attach them to Table component. It has no
 * direct javascript representation.
 */
@TemplateControl(tagName = "fh-table-cell")
public class TableCell extends GroupingComponent<FormElement> {

    @Getter
    @Setter
    private int rowspan;

    @Getter
    @Setter
    private HorizontalAlign horizontalAlign;

    @Getter
    @Setter
    private VerticalAlign verticalAlign;

    @Getter
    @Setter
    private AccessibilityEnum visibility;

    @JsonIgnore
    @Getter
    @Setter
    public int rowIndex;

    public TableCell(Form form) {
        super(form);
    }

    @JsonProperty(value="tableCells")
    @Override
    public List<FormElement> getSubcomponents() {
        return super.getSubcomponents();
    }

    @Override
    public void doActionForEverySubcomponent(Consumer<FormElement> action) {
        for (FormElement cell : getSubcomponents()) {
            action.accept(cell);
            if (cell instanceof IGroupingComponent) {
                ((IGroupingComponent<FormElement>) cell).doActionForEverySubcomponent(action);
            }
        }
    }
}
