package pl.fhframework.binding;

import lombok.Getter;
import pl.fhframework.BindingResult;
import pl.fhframework.model.forms.Component;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Piotr on 2017-02-14.
 */
public class StaticBinding<T> extends ModelBinding<T> {

    @Getter
    private T staticValue;

    public StaticBinding(T staticValue) {
        super("(STATIC VALUE)");
        this.staticValue = staticValue;
    }

    @Override
    public boolean canChange() {
        // treat static value in bounded field as an initial value
        return true;
    }

    @Override
    public void setValue(T value, Optional<String> formatter) {
        // treat static value in bounded field as an initial value
        staticValue = value;
    }

    @Override
    public BindingResult<T> getBindingResult() {
        return new BindingResult<>(null, null, staticValue);
    }

    @Override
    public ModelBinding<T> clone(Component newOwner) {
        return new StaticBinding<>(staticValue); // static binding is immutable
    }
}
