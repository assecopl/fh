package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.fhframework.core.FhBindingException;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ValueChange;

import java.util.Optional;

public interface Boundable {

    default void updateBinding(ValueChange valueChange, ModelBinding modelBinding, Object binding) {
        updateBinding(valueChange, modelBinding, binding, Optional.empty());
    }

    default void updateBinding(ValueChange valueChange, ModelBinding modelBinding, Object binding, Optional<String> formatter) {
        updateBindingForValue(valueChange.getMainValue(), modelBinding, binding, formatter);
    }

    default void updateBindingForValue(Object newValue, ModelBinding modelBinding, Object binding) {
        updateBindingForValue(newValue, modelBinding, binding, Optional.empty());
    }

    default void updateBindingForValue(Object newValue, ModelBinding modelBinding, Object binding, Optional<String> formatter) {
        if (modelBinding == null) {
            return;
        }
        if (modelBinding.canChange()) {
            modelBinding.setValue(newValue, formatter);
        } else {
            throw new FhBindingException("Can't change read only property '" + binding + "'. Usually this error occurs when  curly brackets '{}' are omitted !");
        }
    }

    @JsonIgnore
    default Optional<String> getOptionalFormatter() {
        return Optional.empty();
    }
}
