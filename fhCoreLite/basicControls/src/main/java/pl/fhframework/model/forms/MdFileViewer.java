package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.model.LabelPosition;
import pl.fhframework.model.forms.widgets.Widget;

import java.util.Map;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.*;

/**
 * Class representing xml component of MdFileViewer. Every field represents xml attribute of MdFileViewer tag. <p>
 * Example <MdFileViewer src="src_1" />. <p> Every field is parsed as json for javascript.
 * If field should be ingored by JSON, use <code>@JsonIgnore</code>. There can be used any
 * annotations for json generator.
 */
@Getter
@Setter
@DesignerControl(defaultWidth = 12)
@Control(parents = {PanelGroup.class, Column.class, Tab.class, Row.class, Form.class, Repeater.class,Group.class}, invalidParents = {Table.class, Widget.class}, canBeDesigned = true)
@DocumentedComponent(value = "Md file viewer component for displaying *.md files. Allow inside navigation to other md file.", icon = "fa fa-eye")
public class MdFileViewer extends FormElement implements TableComponent<MdFileViewer> {

    public static final String ATTR_SRC = "src";
    public static final String ATTR_LABEL = "label";

    @Getter
    private String src;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_SRC)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Component source. Relative path to md file.")
    private ModelBinding<String> srcModelBinding;


    /**
     * Left margin of the component
     */
    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Resource base path for md files. Represent base path for relatives urls to other md files.")
    private String resourceBasePath;

    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = LOOK_AND_STYLE, priority = 95)
    @DocumentedComponentAttribute(value = "Defines position of a label. Position is one of: up, down, left, right.")
    private LabelPosition labelPosition;

    @Getter
    private String label;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_LABEL)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, priority = 100, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Represents label for created component. Supports FHML - FH Markup Language.")
    private ModelBinding labelModelBinding;

    public MdFileViewer(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChange = super.updateView();

        if (srcModelBinding != null) {
            src = srcModelBinding.resolveValueAndAddChanges(this, elementChange, src, ATTR_SRC);
        }
        if(labelModelBinding != null) {
            label = labelModelBinding.resolveValueAndAddChanges(this, elementChange, label, ATTR_LABEL);
        }

        return elementChange;
    }

    @Override
    public MdFileViewer createNewSameComponent() {
        return new MdFileViewer(getForm());
    }

    @Override
    public void doCopy(Table table, Map<String, String> iteratorReplacements, MdFileViewer clone) {
        TableComponent.super.doCopy(table, iteratorReplacements, clone);
        clone.setSrc(this.getSrc());
        clone.setLabelModelBinding(table.getRowBinding(this.getLabelModelBinding(), clone, iteratorReplacements));
        clone.setResourceBasePath(this.getResourceBasePath());

    }

}
