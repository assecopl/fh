package pl.fhframework.model.forms.attributes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.fhframework.core.FhBindingException;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.Component;

import java.util.Optional;

public interface BoundableAttribute {

    ElementChanges updateView(Component component, ElementChanges elementChanges);

    void updateModel(Component component, ValueChange valueChange);

    default void updateBinding(ValueChange valueChange, ModelBinding modelBinding, Object binding) {
        updateBinding(valueChange, modelBinding, binding, Optional.empty());
    }

    default void updateBinding(ValueChange valueChange, ModelBinding modelBinding, Object binding, Optional<String> formatter) {
        updateBinding(valueChange.getMainValue(), modelBinding, binding, formatter);
    }

    default void updateBindingForValue(Object newValue, ModelBinding modelBinding, Object binding) {
        updateBinding(newValue, modelBinding, binding, Optional.empty());
    }

    default void updateBinding(Object newValue, ModelBinding modelBinding, Object binding, Optional<String> formatter) {
        if (modelBinding != null) {
            if (modelBinding.canChange()) {
                modelBinding.setValue(newValue);
            } else {
                throw new FhBindingException("Can't change read only property '" + binding + "'. Usually this error occurs when  curly brackets '{}' are omitted !");
            }
        }
    }

    @JsonIgnore
    default Optional<String> getOptionalFormatter() {
        return Optional.empty();
    }

    @JsonIgnore
    ModelBinding getModelBinding();

    @JsonIgnore
    void setModelBinding(ModelBinding modelBinding);
}
