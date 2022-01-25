package pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour;

import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-03-29.
 */
public interface WithParameters<T extends Parental> extends Activity<T> {
    List<Parameter> getParameters();

    default Parameter getParameterByName(String name) {
        return getParameters().stream().filter(parameter -> name.equals(parameter.getName())).findFirst().orElse(null);
    }

    default List<Parameter> getParametersByValue(String value) {
        return getParameters().stream().filter(parameter -> Optional.ofNullable(parameter.getValue()).orElse("").contains(value)).collect(Collectors.toList());
    }

    default void addParameter(Parameter parameter) {
        getParameters().add(parameter);
    }

    default void removeParameter(Parameter parameter) {
        getParameters().remove(parameter);
    }
}
