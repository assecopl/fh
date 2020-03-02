package pl.fhframework;

import pl.fhframework.binding.ComponentBindingContext;

import java.util.Optional;

/**
 * Created by Adam Zareba on 21.03.2017. Binding class simulating fake binding for designer purpose
 * - especially for Tables. {@see pl.fhframework.binding.DesignerModelBinding}
 */
public class DesignerBinding<TYP> extends Binding {

    @Override
    public <TYP> BindingResult<TYP> getBindingResult(String binding, ComponentBindingContext bindingContext) {
        return new BindingResult<>(null, null, null);
    }

    @Override
    public void setModelValue(String binding, Object value, Optional<String> formatter, ComponentBindingContext bindingContext) {

    }
}