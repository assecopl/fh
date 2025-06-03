package pl.fhframework.compiler.core.uc.dynamic.model.converters;

import pl.fhframework.compiler.core.uc.dynamic.model.element.Action;
import pl.fhframework.format.AutoRegisteredConverter;
import pl.fhframework.format.FhConverter;

@FhConverter
public class ActionConverter
        extends AutoRegisteredConverter<Action, String> {

    @Override
    public String convert(Action action) {
        return action.getName();
    }
}
