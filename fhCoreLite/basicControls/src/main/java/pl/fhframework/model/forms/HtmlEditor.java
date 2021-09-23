package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.designer.BindingExpressionDesignerPreviewProvider;
import pl.fhframework.model.forms.optimized.ColumnOptimized;

import java.util.Objects;
import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.*;

@Getter
@Setter
@OverridenPropertyAnnotations(designerXmlProperty = @DesignerXMLProperty(skip = true), property = "value")
@OverridenPropertyAnnotations(designerXmlProperty = @DesignerXMLProperty(skip = true), property = "modelBinding")
@OverridenPropertyAnnotations(designerXmlProperty = @DesignerXMLProperty(skip = true), property = "rawValue")
@DesignerControl(defaultWidth = 12)
@Control(parents = {PanelGroup.class, Column.class, ColumnOptimized.class, Tab.class, Row.class, Form.class, Repeater.class, Group.class}, invalidParents = {Table.class}, canBeDesigned = true)
@DocumentedComponent(category = DocumentedComponent.Category.IMAGE_HTML_MD, documentationExample = true, value = "Visual HTML text editor. Allows visual editing of text in HTML markup.", icon = "fa fa-eye")
public class HtmlEditor extends BaseInputField {
    public static final String ATTR_TEXT = "text";

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_TEXT)
    @DesignerXMLProperty(commonUse = true, previewValueProvider = BindingExpressionDesignerPreviewProvider.class, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Text to edit.")
    private ModelBinding<String> textModelBinding;

    @Getter
    private String text;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Id of formatter which will format object to String. It must be consistent with value of pl.fhframework.formatter.FhFormatter annotation.")
    @DesignerXMLProperty(functionalArea = SPECIFIC, priority = 93)
    private String formatter;

    public HtmlEditor(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        BindingResult bindingResult;
        if (textModelBinding != null) {
            bindingResult = textModelBinding.getBindingResult();
            if (bindingResult != null) {
                this.text = convertBindingValueToString(bindingResult);
            }
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();

        if (textModelBinding != null) {
            BindingResult<String> bindingResult = textModelBinding.getBindingResult();
            if (bindingResult != null) {
                String newLabelValue = bindingResult.getValue();

                if (this.formatter != null) {
                    newLabelValue = getForm().convertValueToString(newLabelValue, formatter);
                }
                if (!areValuesTheSame(newLabelValue, text)) {
                    this.text = newLabelValue;
                    elementChanges.addChange(ATTR_TEXT, text);
                }
            }
        }

        return elementChanges;
    }

    @Override
    public void updateModel(ValueChange valueChange) {
        String newText = valueChange.getStringAttribute("text");

        if (!Objects.equals(text, newText)) {
            this.text = newText;
            this.updateBindingForValue(newText, textModelBinding, newText, Optional.ofNullable(formatter));
        }
    }

    @Override
    public HtmlEditor createNewSameComponent() {
        return new HtmlEditor(getForm());
    }
}
