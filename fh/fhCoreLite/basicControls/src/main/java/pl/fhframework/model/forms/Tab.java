package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.fhframework.core.forms.IHasBoundableLabel;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.annotations.Control;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.attribute.Layout;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.designer.IDesignerEventListener;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

/**
 * Class represents Tab as grouping element. Tab consists of label and layout position. Example:
 * {@code <Group></Group>} Should be used within TabContainer.
 */
@TemplateControl(tagName = "fh-tab")
@Control(parents = {TabContainer.class, Wizard.class})
@DocumentedComponent(ignoreFields = {"width"},
        value = "Tab component which represents a single tab", icon = "fa fa-window-maximize")
public class Tab extends GroupingComponent implements IHasBoundableLabel, IDesignerEventListener {

    public static final String ATTR_LABEL = "label";
    public static final String TYPE_NAME = "Tab";
    public static final String DEFAULT_LAYOUT = "vertical";

    @Getter
    private String label;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_LABEL)
    @DesignerXMLProperty(commonUse = true, functionalArea = CONTENT,
            previewValueProvider = BindingExpressionDesignerPreviewProvider.class)
    @DocumentedComponentAttribute(boundable = true, value = "Component label")
    private ModelBinding labelModelBinding;

    @Getter
    @Setter
    @XMLProperty(defaultValue = DEFAULT_LAYOUT)
    private Layout layout;

    @Getter
    @JsonIgnore
    private boolean layoutGenerated = true;

    public Tab(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        if (this.label == null) {
            this.label = this.getId();
        }
    }

    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        boolean refreshView = processLabelBinding(elementChanges, false);
        if (refreshView) {
            refreshView();
        }
        return elementChanges;
    }

    protected boolean processLabelBinding(ElementChanges elementChanges, boolean refreshView) {
        BindingResult labelBidingResult = labelModelBinding != null ? labelModelBinding.getBindingResult() : null;
        if (labelBidingResult != null) {
            String newLabelValue = this.convertBindingValueToString(labelBidingResult);
            if (!areValuesTheSame(newLabelValue, label)) {
                this.label = newLabelValue;
                elementChanges.addChange(ATTR_LABEL, this.label);
                refreshView = true;
            }
        }
        return refreshView;
    }

    @Override
    public String getType() {
        return TYPE_NAME;
    }

    @Override
    public void onDesignerAddDefaultSubcomponent(SpacerService spacerService) {
        addSubcomponent(createNewRow());
    }

    public Row createNewRow() {
        Row row = new Row(getForm());
        row.setGroupingParentComponent(this);
        row.init();
        return row;
    }
}
