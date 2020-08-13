package pl.fhframework.binding;


import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.RepeaterTraversable;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.FormElement;

import java.util.Objects;
import java.util.Optional;

import lombok.Getter;

@RepeaterTraversable
public abstract class ModelBinding<V> {

    /**
     * Full/original expression used
     */
    @Getter
    @Setter
    protected String bindingExpression;

    public ModelBinding(String bindingExpression) {
        this.bindingExpression = bindingExpression;
    }

    public abstract boolean canChange();

    public void setValue(V value) {
        setValue(value, Optional.empty());
    }

    public abstract void setValue(V value, Optional<String> formatter);

    public abstract BindingResult<V> getBindingResult();

    public abstract ModelBinding clone(Component newOwner);

    public <T> T resolveValueAndAddChanges(FormElement formElement, ElementChanges elementChanges, T oldValue, String attributeName) {
        BindingResult<Object> bindingResult = (BindingResult<Object>) getBindingResult();
        if (bindingResult != null) {
            T newValue = (T) bindingResult.getValue();
            if (!Objects.equals(newValue, oldValue)) {
                formElement.getForm().refreshElementView(formElement);
                elementChanges.addChange(attributeName, newValue);
                return newValue;
            }
        }
        return oldValue; // just pass-through
    }

    public String resolveValueAndAddChanges(FormElement formElement, ElementChanges elementChanges, String oldValue, String attributeName) {
        BindingResult<String> bindingResult = (BindingResult<String>) getBindingResult();
        if (bindingResult != null) {
            String newValue = bindingResult.getValue();
            if (!Objects.equals(newValue, oldValue)) {
                formElement.getForm().refreshElementView(formElement);
                elementChanges.addChange(attributeName, newValue);
                return newValue;
            }
        }
        return oldValue; // just pass-through
    }

    public boolean resolveValueAndAddChanges(FormElement formElement, ElementChanges elementChanges, boolean oldValue, String attributeName) {
        BindingResult<?> bindingResult = getBindingResult();
        if (bindingResult != null) {
            Object newValueObj = bindingResult.getValue();
            if (newValueObj != null) {
                boolean newValue = formElement.convertValue(newValueObj, boolean.class);
                if (!Objects.equals(newValue, oldValue)) {
                    formElement.getForm().refreshElementView(formElement);
                    elementChanges.addChange(attributeName, newValue);
                    return newValue;
                }
            }
        }
        return oldValue; // just pass-through
    }

    public Object resolveValue(Object oldValue) {
        BindingResult<Object> bindingResult = (BindingResult<Object>) getBindingResult();
        if (bindingResult != null) {
            Object newValue = bindingResult.getValue();
            if (!Objects.equals(newValue, oldValue)) {
                return newValue;
            }
        }
        return oldValue; // just pass-through
    }
}
