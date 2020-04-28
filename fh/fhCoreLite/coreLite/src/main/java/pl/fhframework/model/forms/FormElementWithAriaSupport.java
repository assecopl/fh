package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

/**
 *  Base component with aria  support.
 */
public abstract class FormElementWithAriaSupport extends FormElement implements Boundable {

    public static final String ATTR_ARIA_LABEL = "ariaLabel";


    @Getter
    public String ariaLabel = null;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = ATTR_ARIA_LABEL)
    @DesignerXMLProperty(priority = 200, functionalArea = CONTENT)
    @DocumentedComponentAttribute(boundable = true, value = "Binding represents value from confirmation message, used inside of '{}', like {model}.")
    public ModelBinding ariaLabelBinding = null;

    public FormElementWithAriaSupport(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        return updateAriaLabelView(elementChanges);
    }

    protected ElementChanges updateAriaLabelView(ElementChanges elementChanges) {
        if (ariaLabelBinding != null) {
            Object newAriaLabelValueObj = ariaLabelBinding.getBindingResult().getValue();
            String newAriaLabelValue = newAriaLabelValueObj != null ? newAriaLabelValueObj.toString() : null;

            if (!areValuesTheSame(newAriaLabelValue, ariaLabel)) {
                this.ariaLabel = newAriaLabelValue;
                elementChanges.addChange(ATTR_ARIA_LABEL, this.ariaLabel);
                refreshView();
            }
        }
        return elementChanges;
    }

}
