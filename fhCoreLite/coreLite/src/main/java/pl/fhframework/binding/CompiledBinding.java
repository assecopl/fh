package pl.fhframework.binding;

import lombok.Getter;
import org.springframework.util.ClassUtils;
import pl.fhframework.core.FhAuthorizationException;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.BindingResult;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.format.FhConversionService;
import pl.fhframework.model.forms.Component;

import java.text.ParseException;
import java.util.Optional;
import java.util.function.*;

/**
 * Created by Piotr on 2017-02-14.
 */
public class CompiledBinding<T> extends ModelBinding<T> {

    @FunctionalInterface
    public interface BaseObjectGetter<B> extends Supplier<B> {}

    @FunctionalInterface
    public interface ValueGetterForBaseObject<B, V> extends Function<B, V> {}

    @FunctionalInterface
    public interface ValueSetterForBaseObject<B, V> extends BiConsumer<B, V> {}

    @FunctionalInterface
    public interface ValueGetter<V> extends Supplier<V> {}

    @FunctionalInterface
    public interface ValueSetter<V> extends Consumer<V> {}

    private String attributeName;

    @Getter
    private Class<?> targetType;
    private Supplier<FhConversionService> conversionServiceSupplier;

    // fields for base object usage
    private BaseObjectGetter<Object> baseObjectGetter;
    private ValueGetterForBaseObject<Object, T> valueGetterForBaseObject;
    private ValueSetterForBaseObject<Object, T> valueSetterForBaseObject;

    // fields without base object usage
    private ValueGetter<T> valueGetter;
    private ValueSetter<T> valueSetter;

    public <B> CompiledBinding(String bindingExpression, String attributeName, Class<?> targetType,
                               Supplier<FhConversionService> conversionServiceSupplier,
                               BaseObjectGetter<B> baseObjectGetter,
                           ValueGetterForBaseObject<B, T> valueGetterForBaseObject,
                           ValueSetterForBaseObject<B, T> valueSetterForBaseObject) {
        super(bindingExpression);
        this.attributeName = attributeName;
        this.baseObjectGetter = (BaseObjectGetter<Object>) baseObjectGetter;
        this.valueGetterForBaseObject = (ValueGetterForBaseObject<Object, T>) valueGetterForBaseObject;
        this.valueSetterForBaseObject = (ValueSetterForBaseObject<Object, T>) valueSetterForBaseObject;
        this.targetType = targetType;
        this.conversionServiceSupplier = conversionServiceSupplier;
    }

    public CompiledBinding(String bindingExpression, String attributeName, Class<?> targetType,
                           Supplier<FhConversionService> conversionServiceSupplier,
                           ValueGetter<T> valueGetter, ValueSetter<T> valueSetter) {
        super(bindingExpression);
        this.attributeName = attributeName;
        this.valueGetter = valueGetter;
        this.valueSetter = valueSetter;
        this.targetType = targetType;
        this.conversionServiceSupplier = conversionServiceSupplier;
    }

    public CompiledBinding(Class<T> targetType, ValueGetter<T> valueGetter, ValueSetter<T> valueSetter) {
        super("__INLINE_BINDING__");
        this.valueGetter = valueGetter;
        this.valueSetter = valueSetter;
        this.targetType = targetType;
    }

    public CompiledBinding(Class<T> targetType, ValueGetter<T> valueGetter) {
        this(targetType, valueGetter, null);
    }


    @Override
    public boolean canChange() {
        return valueSetter != null || (baseObjectGetter != null && valueSetterForBaseObject != null);
    }

    @Override
    public void setValue(T value, Optional<String> formatter) {
        if (!canChange()) {
            throw new RuntimeException(bindingExpression + " is a read-only binding");
        }
        try {
            value = (T) convertValue(value, formatter);
            if (baseObjectGetter != null) {
                valueSetterForBaseObject.accept(baseObjectGetter.get(), value);
            } else {
                valueSetter.accept(value);
            }
        } catch (Exception e) {
            throw new FhBindingException("Exception while setting value to compiled binding: " + bindingExpression, e);
        }
    }

    @Override
    public BindingResult<T> getBindingResult() {
        try {
            if (baseObjectGetter != null) {
                Object baseObject = baseObjectGetter.get();
                return new BindingResult<>(baseObject, attributeName, valueGetterForBaseObject.apply(baseObject));
            } else {
                return new BindingResult<>(null, attributeName, valueGetter.get());
            }
        } catch (FhAuthorizationException e) {
            throw e;
        } catch (Exception e) {
            throw new FhBindingException("Exception while getting value to compiled binding: " + bindingExpression, e);
        }
    }

    @Override
    public ModelBinding clone(Component newOwner) {
        return this; // compiled binding is immutable
    }

    public Object convertValue(Object newValue, Optional<String> formatterOpt) throws ParseException {
        if (newValue == null) {
            return null;
        }
        Class<?> valueType = newValue.getClass();

        // TODO: move this logic to java generation at least when using Strings or formatters
        if (formatterOpt.isPresent() && ClassUtils.isAssignable(valueType, String.class)) {
            if (conversionServiceSupplier == null) {
                throw new FhBindingException("Formatter is specified but conversion service is not supplied");
            }
            String formatter = formatterOpt.get();
            FhConversionService conversionService = conversionServiceSupplier.get();
            if (conversionService.canConvertToObject(targetType, formatter)) {
               return conversionService.parseUsingCustomFormatter((String) newValue, formatter);
            }
        }

        if (valueType != targetType && !targetType.isAssignableFrom(valueType)) {
            if (conversionServiceSupplier == null) {
                throw new FhBindingException("Value: '" + newValue + "' needs conversion to model of type: '" + targetType.getName() + "', but conversion service is not supplied");
            }
            // TODO: move this logic to java generation at least when using Strings or formatters
            FhConversionService conversionService = conversionServiceSupplier.get();
            if (conversionService.canConvert(ReflectionUtils.resolveToRealClass(valueType), targetType)) {
                newValue = conversionService.convert(newValue, targetType);
            } else {
                throw new FhBindingException("Can't convert value: '" + newValue + "' to model of type: '" + targetType.getName() + "'");
            }
        }

        return newValue;
    }
}
