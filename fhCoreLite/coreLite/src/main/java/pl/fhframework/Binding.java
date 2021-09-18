package pl.fhframework;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.util.Pair;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.CollectionsUtils;
import pl.fhframework.binding.ComponentBindingContext;
import pl.fhframework.binding.RowNumberBindingContext;
import pl.fhframework.format.FhConversionService;
import pl.fhframework.core.generator.I18nBindingResolver;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.PageModel;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Gabriel on 2015-12-15.
 */
public class Binding {

    private Map<String, BindingData> bindingCache = new HashMap<>();
    private Object basicModelContainer;
    private boolean _active;
    private Form form;

    @Autowired
    private FhConversionService conversionService;

    @Autowired
    private MessageService messageService;

    public Binding() {
        AutowireHelper.autowire(this, conversionService, messageService);
    }

    public boolean isActive() {
        return _active;
    }

    /**
     * Returns value in model for specified biding
     *
     * @param binding        Biding without characters: '{', '}'. All sections are separated by
     *                       '.'.
     * @param bindingContext binding context, may be null
     * @return Returns bound object with specified model value
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public <TYP> TYP getModeValue(String binding, ComponentBindingContext bindingContext) {
        if (basicModelContainer == null) {
            throw new FhBindingException("BidingMethods are not activated - can't read value");
        }
        BindingData db = getBindingData(binding, bindingContext);
        if (db.keyI18n != null) {
            if (db.bundleI18n == null) {
                return (TYP) getMessageByKey(db.keyI18n);
            } else
                return (TYP) getMessageByKey(db.bundleI18n, db.keyI18n);
        } else if (db.fieldName == null) {
            return (TYP) db.container;
        }
        return (TYP) getFieldValue(db.container, db.fieldName);
    }

    @SuppressWarnings("unchecked")
    public <TYP> BindingResult<TYP> getBindingResult(String binding, ComponentBindingContext bindingContext) {
        if (basicModelContainer == null) {
            throw new FhBindingException("BidingMethods are not activated - can't read value");
        }
        BindingData db = getBindingData(binding, bindingContext);
        if (db.keyI18n != null) {
            if (db.bundleI18n == null) {
                return new BindingResult<>(db.container, null, (TYP) getMessageByKey(db.keyI18n));
            } else {
                return new BindingResult<>(db.container, null, (TYP) getMessageByKey(db.bundleI18n, db.keyI18n));
            }

        } else if (db.fieldName == null) {
            return new BindingResult<>(db.container, null, (TYP) db.container);
        }
        return new BindingResult<>(db.container, db.fieldName, (TYP) getFieldValue(db.container, db.fieldName));
    }

    /**
     * Modifies attribute of object specified through biding value.
     *
     * @param binding        Biding without characters: '{', '}'. All sections are separated by
     *                       '.'.
     * @param value          Value to set, can be null.
     * @param bindingContext binding context, may be null
     */
    public void setModelValue(String binding, Object value, ComponentBindingContext bindingContext) {
        setModelValue(binding, value, Optional.empty(), bindingContext);
    }

    /**
     * Modifies attribute of object specified through biding value.
     *
     * @param binding        Biding without characters: '{', '}'. All sections are separated by
     *                       '.'.
     * @param value          Value to set, can be null.
     * @param formatter      Optional value formatter
     * @param bindingContext binding context, may be null
     */
    public void setModelValue(String binding, Object value, Optional<String> formatter, ComponentBindingContext bindingContext) {
        if (basicModelContainer == null) {
            throw new FhBindingException("BidingMethods are not activated - can't set value");
        }
        BindingData db = getBindingData(binding, bindingContext);
        if (db.fieldName == null) {
            throw new FhBindingException("Can't modify this object!");
        }
        setFieldValue(db.container, db.fieldName, value, formatter);
    }

    /**
     * Activates service for returning values. Initializes object.
     */
    public void activate(Object basicModelContainer, Form form) {
        this.form = form;
        bindingCache.clear();
        this.basicModelContainer = basicModelContainer;
        _active = true;
    }

    /**
     * Deactivates service. All temporary data are being removed.
     */
    public void deactivate() {
        bindingCache.clear();
        basicModelContainer = null;
        _active = false;
    }

    public String convertBindingValueToString(BindingResult bindingResult) {
        return convertBindingValueToString(bindingResult, Optional.empty());
    }

    public String convertBindingValueToString(BindingResult bindingResult, Optional<String> converterNameOpt) {
        Object value = bindingResult.getValue();
        if (value == null) {
            return "";
        }
        Class<?> valueClass = value.getClass();

        if (converterNameOpt.isPresent()) {
            String converterName = converterNameOpt.get();
            if (conversionService.canConvertObjectToString(getClass(valueClass), converterName)) {
                return conversionService.printUsingCustomFormatter(value, converterName);
            } else if (!valueClass.equals(String.class)) {
                throw new FhBindingException("Can't convert value of binding result: '" + value + "' to String.");
            }
        }

        if (valueClass.equals(String.class)) {
            return (String) value;
        }
        if (conversionService.canConvert(valueClass, String.class)) {
            return conversionService.convert(bindingResult.getValue(), String.class);
        } else {
            throw new FhBindingException("Can't convert value of binding result: '" + value + "' to String.");
        }
    }

    public String convertValueToString(Object value) {
        return convertValueToString(value, null);
    }

    public <T> T convertValue(Object value, Class<T> clazz) {
        if (value == null) {
            return null;
        }
        if (conversionService.canConvert(getClass(value.getClass()), getClass(clazz))) {
            return conversionService.convert(value, clazz);
        } else {
            throw new FhBindingException("Can't convert value: '" + value + "' to " + clazz.getName() + " .");
        }
    }

    public String convertValueToString(Object value, String converter) {
        if (value == null) {
            return "";
        }
        Class<?> valueClass = value.getClass();
        if (!StringUtils.isEmpty(converter)) {
            if (conversionService.canConvertObjectToString(getClass(valueClass), converter)) {
                return conversionService.printUsingCustomFormatter(value, converter);
            } else if (!valueClass.equals(String.class)) {
                throw new FhBindingException("Can't convert value of binding result: '" + value + "' to String.");
            }
        }
        if (valueClass.equals(String.class)) {
            return (String) value;
        }
        if (conversionService.canConvert(getClass(valueClass), String.class)) {
            return conversionService.convert(value, String.class);
        } else {
            return value.toString();
        }
    }

    private static class BindingData {
        Object container;
        String fieldName;
        String keyI18n;
        String bundleI18n;
    }

    private BindingData getBindingData(String binding, ComponentBindingContext bindingContext) {
        // resolve row numbers of iterators without using cache
        if (bindingContext != null && bindingContext.getRowNumberBindingContexts() != null) {
            for (RowNumberBindingContext rowNumberContext : bindingContext.getRowNumberBindingContexts()) {
                if (binding.equals(rowNumberContext.getIterator() + "$rowNo")) {
                    int rowNumber = rowNumberContext.getRowNumber();
                    if (rowNumberContext.getOffsetSupplier() != null) {
                        rowNumber += rowNumberContext.getOffsetSupplier().getRowNumberOffset();
                    }
                    BindingData result = new BindingData();
                    result.container = String.valueOf(rowNumber);
                    return result;
                }
            }
        }

        if (bindingCache.containsKey(binding)) {
            return bindingCache.get(binding);
        } else {
            BindingData bindingData = new BindingData();
            bindingCache.put(binding, bindingData);
            Pair<String, String> bundleAndKeyFrom = I18nBindingResolver.getBundleAndKeyFrom(binding);
            if (bundleAndKeyFrom != null) {
                bindingData.container = null;
                bindingData.fieldName = null;
                bindingData.bundleI18n = bundleAndKeyFrom.getFirst();
                bindingData.keyI18n = bundleAndKeyFrom.getSecond();
            } else if ("THIS".equals(binding)) {
                bindingData.container = basicModelContainer;
                bindingData.fieldName = null;
            } else if ("FORM".equals(binding)) {
                bindingData.container = this.form;
                bindingData.fieldName = null;
            } else if (binding.contains(".")) {
                int coords = binding.lastIndexOf(".");
                String containerPath = binding.substring(0, coords);
                bindingData.fieldName = binding.substring(coords + 1);
                BindingData containerBiding;
                if (containerPath.charAt(containerPath.length() - 1) == ']') {
                    String indexString = containerPath.substring(containerPath.lastIndexOf("[") + 1, containerPath.length() - 1);
                    int index = Integer.parseInt(indexString);
                    containerPath = containerPath.substring(0, containerPath.lastIndexOf("["));
                    containerBiding = getBindingData(containerPath, bindingContext);
                    Object fieldValue = getFieldValue(containerBiding.container, containerBiding.fieldName);
                    if (fieldValue instanceof List) {
                        bindingData.container = ((List) fieldValue).get(index);
                    } else if (fieldValue instanceof Page) {
                        bindingData.container = ((Page) fieldValue).getContent().get(index);
                    } else if (fieldValue instanceof PageModel)
                        bindingData.container = ((PageModel) fieldValue).getPage().getContent().get(index);
                    else if (fieldValue instanceof Collection) {
                        bindingData.container = CollectionsUtils.get((Collection) fieldValue, index);
                    } else if (fieldValue == null) {
                        return null;
                    } else {
                        throw new FhBindingException(fieldValue.getClass().getName() + " is not supported in collection binding");
                    }
                } else {
                    containerBiding = getBindingData(containerPath, bindingContext);
                    bindingData.container = getFieldValue(containerBiding.container, containerBiding.fieldName);
                }


            } else if (binding.startsWith("THIS[")) {
                String indexString = binding.substring(binding.indexOf("[") + 1, binding.length() - 1);
                int index = Integer.parseInt(indexString);
                bindingData.fieldName = null;
                bindingData.container = ((List) basicModelContainer).get(index);
            } else {
                bindingData.fieldName = binding;
                bindingData.container = basicModelContainer;
            }
            return bindingData;
        }
    }

    private String getMessageByKey(String bundle, String key) {
        return messageService.getBundle(bundle).getMessage(key);
    }

    private String getMessageByKey(String key) {
        return messageService.getAllBundles().getMessage(key);
    }

    private Object getFieldValue(Object container, String fieldName) {
        if (container == null) {
            return null;//TODO: Verify if this is ok - if no parent - should we return null???
        }
        if (fieldName == null) return container;
        if (!fieldName.startsWith("{") && fieldName.endsWith("}")) {
            fieldName = fieldName.substring(0, fieldName.length() - 1);
        }
        int index = -1;
        if (fieldName.charAt(fieldName.length() - 1) == ']') {
            String indexString = fieldName.substring(fieldName.indexOf("[") + 1, fieldName.length() - 1);
            index = Integer.parseInt(indexString);
            fieldName = fieldName.substring(0, fieldName.indexOf("["));
            if (index < 0) return null;
        }
        try {
            Method method = getPropertyDescriptor(container, fieldName).getReadMethod();
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }

            Object result = method.invoke(container, (Object[]) null);

            if (index == -1) {
                return result;
            } else if (result instanceof List) {
                return ((List) result).get(index);
            } else if (result instanceof Collection) {
                return CollectionsUtils.get((Collection) result, index);
            } else if (result instanceof PageImpl) {
                PageImpl pageImpl = (PageImpl) result;
                return CollectionsUtils.get(pageImpl.getContent(), index);
            } else if (result instanceof PageModel) {
                return CollectionsUtils.get(((PageModel) result).getPage().getContent(), index);
            } else if (result == null) {
                return null;
            } else {
                throw new FhBindingException(result.getClass().getName() + " is not supported in collection binding");
            }
        } catch (InvocationTargetException | IllegalAccessException exc) {
            FhLogger.error("Error in reading data {}.{}", container.getClass(), fieldName, exc);
            throw new FhBindingException("Error in reading data " + container.getClass() + "." + fieldName);
        }
    }

    private void setFieldValue(Object container, String fieldName, Object value, Optional<String> formatterOpt) {
        try {
            PropertyDescriptor pd = getPropertyDescriptor(container, fieldName);
            boolean error = false;
            Class<?> propertyType = pd.getPropertyType();
            if (propertyType != null && value != null) {
                Class<?> valueType = value.getClass();
                if (!propertyType.equals(valueType)) {
                    //TODO: think about better solution (without only String as input type)
                    // TODO: Add logic to conversionService.canConvertToObject for org.springframework.core.convert.converter.Converter
                    // TODO: Add logic to conversionService.parseUsingCustomFormatter for org.springframework.core.convert.converter.Converter
                    if (formatterOpt.isPresent() && ClassUtils.isAssignable(valueType, String.class)) {
                        String formatter = formatterOpt.get();
                        if (conversionService.canConvertToObject(propertyType, formatter)) {
                            value = conversionService.parseUsingCustomFormatter((String) value, formatter);
                        }
                    } else {
                        if (conversionService.canConvert(getClass(valueType), propertyType)) {
                            value = conversionService.convert(value, propertyType);
                        } else {
                            throw new FhBindingException("Can't convert value: '" + value + "' to model of type: '" + propertyType.getName() + "'");
                        }
                    }
                }
            }
            if (!error) {
                Method writeMethod = pd.getWriteMethod();
                if (writeMethod != null) {
                    if (propertyType.equals(List.class) && ClassUtils.isAssignableValue(String.class, value)) {
                        List<?> attributes = Arrays.asList(value.toString());
                        writeMethod.invoke(container, attributes);
                    } else {
                        Object[] objectAttributes = {value};
                        writeMethod.invoke(container, objectAttributes);
                    }
                } else {
                    throw new FhBindingException("No setter found: " + pd.getName() + " in " + container.getClass());
                }
            }
        } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException exc) {
            FhLogger.error("Error while writing {}.{}", container.getClass(), fieldName, exc);
            throw new FhBindingException("Error while writing " + container.getClass() + "." + fieldName);
        } catch (ParseException pe) {
            throw  new FhBindingException(pe);
        }
    }

    private PropertyDescriptor getPropertyDescriptor(Object container, String fieldName) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(container.getClass());
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : descriptors) {
                if (pd.getName().equals(fieldName)) {
                    return pd;
                }
            }
            throw new FhBindingException("No property in class '" + container.getClass().getName() + "' with name '" + fieldName + "'");
        } catch (IntrospectionException exc) {
            throw new FhBindingException(exc);
        }
    }

    /**
     * There can be javassist class. Return proper class
     *
     * @param input object
     * @return class of object
     */
    private Class getClass(final Class input) {
        if (input != null) {
            if (input.getName().contains("_$$_")) {
                return input.getSuperclass();
            }
            return input;
        }

        return null;
    }
}
