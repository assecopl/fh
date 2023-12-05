package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.designer.InputFieldDesignerPreviewProvider;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;
@DesignerControl(defaultWidth = -1)
@Control(parents = {DictionaryComboFhDP.class}, canBeDesigned = false)
@DocumentedComponent(documentationExample = true, value = "It is used to construct columns of Table components.", icon = "fa fa-columns")
@ModelElement(type = ModelElementType.HIDDEN)
public class DictionaryComboParameterFhDP extends FormElement implements Boundable{


        public static final String ATTR_VALUE = "value";

        @Getter
        @Setter
        @XMLProperty
        private String name;

        @Getter
        private String value;

        @JsonIgnore
        @Getter
        @Setter
        @XMLProperty(value = ATTR_VALUE, aliases = Combo.SELECTED_ITEM_ATTR)
        // when changing @DesignerXMLProperty also change in @OverridenPropertyAnnotations on InputNumber,CheckBox,InputDate,InputText,InputTimestamp
        @DesignerXMLProperty(commonUse = true, previewValueProvider = InputFieldDesignerPreviewProvider.class, priority = 80, functionalArea = CONTENT)
        @DocumentedComponentAttribute(boundable = true, value = "Binding represents value from model of Form, used inside of '{}', like {model}.")
        private ModelBinding modelBinding;

        @JsonIgnore
        @Getter
        protected boolean validConversion = true;

        public DictionaryComboParameterFhDP(Form form) {
            super(form);
        }


        protected void processCoversionException(FhBindingException cfe) {
            throw cfe;
        }


    @Override
    protected ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        return elementChanges;
    }

    public String resolveValue() {
        if(modelBinding != null) {
            BindingResult bindingResult = modelBinding.getBindingResult();
            if (bindingResult != null) {
                String newLabelValue = this.convertValueToString(bindingResult.getValue());
                if (!areValuesTheSame(newLabelValue, value)) {
                    this.value = newLabelValue;
                }
            }

        }
        return this.value;
    }

    public Object getBindingValue() {
        if (modelBinding != null) {
            BindingResult bindingResult = modelBinding.getBindingResult();
            if (bindingResult != null) {
                return bindingResult.getValue();
            }
        }
        return null;
    }

    }
