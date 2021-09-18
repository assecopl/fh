package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.util.StringUtils;

import pl.fhframework.core.forms.IHasBoundableLabel;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.annotations.Control;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.model.LabelPosition;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;
import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.LOOK_AND_STYLE;

/**
 * Created by krzysztof.kobylarek on 2016-11-30.
 */
@DesignerControl(defaultWidth = 2)
@Control(parents = {PanelGroup.class, Group.class, Column.class, Tab.class, Row.class, Form.class, Repeater.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(value = "Label component is responsible for displaying HTML link.", icon = "fa fa-link")
public class Link extends OutputLabel implements IHasBoundableLabel {

    private static final String ATTR_URL = "url";
    protected static final String LABEL_ATTR = "label";

    @Getter
    @Setter
    @XMLProperty(defaultValue = "true", required = true)
    @DocumentedComponentAttribute(defaultValue = "true", value = "If url is provided, then user can set if link should be displayed in new window")
    private boolean newWindow = true;

    @Getter
    private String url;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_URL)
    @DesignerXMLProperty(commonUse = true, allowedTypes = String.class)
    @DocumentedComponentAttribute(boundable = true, value = "Hyperlink to resource")
    private ModelBinding<String> urlBinding;

    @Getter
    private String label = "";   // requires default value for designer (for now). Otherwise there are some errors, while displaying component with JS.

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = LABEL_ATTR)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, priority = 100, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Represents label for created component. Supports FHML - Fh Markup Language.")
    private ModelBinding labelModelBinding;

    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 95)
    @DocumentedComponentAttribute(value = "Defines position of a label. Position is one of: up, down, left, right.")
    private LabelPosition labelPosition;

    public Link(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        calculateAndSetDefaultSize();
    }

    private void calculateAndSetDefaultSize() {
        if (!StringUtils.hasText(this.getWidth())) {
            this.setWidth("md-12");
        }
    }

    private void processURL(ElementChanges elementChanges) {
        if (urlBinding == null) {
            return;
        }
        url = urlBinding.resolveValueAndAddChanges(this, elementChanges, url, ATTR_URL);
    }

    @Override
    public ElementChanges updateView() {
        final ElementChanges elementChanges = super.updateView();
        processLabelBinding(elementChanges);
        processURL(elementChanges);
        return elementChanges;
    }

    private void processLabelBinding(ElementChanges elementChanges) {
        BindingResult labelBidingResult = labelModelBinding != null ? labelModelBinding.getBindingResult() : null;
        String newLabelValue = labelBidingResult == null ? null : this.convertBindingValueToString(labelBidingResult);
        if (!areValuesTheSame(newLabelValue, label)) {
            this.label = newLabelValue;
            elementChanges.addChange(LABEL_ATTR, this.label);
        }
    }

    @Override
    public Link createNewSameComponent() {
        return new Link(getForm());
    }

    public void doCopy(Table table, Map<String, String> iteratorReplacements, OutputLabel baseClone) {
        super.doCopy(table, iteratorReplacements, baseClone);
        Link clone = (Link) baseClone;
        clone.setUrlBinding(table.getRowBinding(this.getUrlBinding(), clone, iteratorReplacements));
        clone.setLabelModelBinding(table.getLabelModelBinding());
        clone.setLabelPosition(getLabelPosition());
        clone.setNewWindow(isNewWindow());
    }
}
