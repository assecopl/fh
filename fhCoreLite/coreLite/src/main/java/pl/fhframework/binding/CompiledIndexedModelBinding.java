package pl.fhframework.binding;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import lombok.Getter;
import pl.fhframework.core.forms.iterators.IIndexedBindingOwner;
import pl.fhframework.core.forms.iterators.IRepeatableIteratorInfo;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.BindingResult;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compiled indexed model binding.
 */
@JsonIgnoreType
@Getter
public class CompiledIndexedModelBinding<T> extends IndexedModelBinding<T> {

    private Function<int[], T> getter;

    public CompiledIndexedModelBinding(String bindingExpression, Function<int[], T> getter) {
        super(bindingExpression);
        this.getter = getter;
    }

    @Override
    public T getValue(int[] indices) {
        return this.getter.apply(indices);
    }
}
