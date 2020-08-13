package pl.fhframework.model.forms;

import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.OverridenPropertyAnnotations;

/**
 * Non-visual form element.
 */
@OverridenPropertyAnnotations(designerXmlProperty = @DesignerXMLProperty(skip = true), property = "width")
@OverridenPropertyAnnotations(designerXmlProperty = @DesignerXMLProperty(skip = true), property = "hintBinding")
@OverridenPropertyAnnotations(designerXmlProperty = @DesignerXMLProperty(skip = true), property = "styleClasses")
@OverridenPropertyAnnotations(designerXmlProperty = @DesignerXMLProperty(skip = true), property = "verticalAlign")
@OverridenPropertyAnnotations(designerXmlProperty = @DesignerXMLProperty(skip = true), property = "horizontalAlign")
@OverridenPropertyAnnotations(designerXmlProperty = @DesignerXMLProperty(skip = true), property = "height")
public abstract class NonVisualFormElement extends FormElement {

    public NonVisualFormElement(Form form) {
        super(form);
    }

    public abstract String getNonVisualToolboxIcon();
}
