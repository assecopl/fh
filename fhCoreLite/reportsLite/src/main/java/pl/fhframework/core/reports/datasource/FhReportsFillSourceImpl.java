package pl.fhframework.core.reports.datasource;

import lombok.Getter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.reports.JrReportUtils;
import pl.fhframework.core.reports.FhReportsException;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.format.FhConversionService;
import pl.fhframework.helper.AutowireHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by pawel.ruta on 2017-11-09.
 */
public abstract class FhReportsFillSourceImpl<T, I> implements FhReportsFillSource<T, I> {
    @Getter
    private T reportModel;

    @Getter
    private Iterator<I> iterator;

    private I currentRow;

    @Autowired
    private FhConversionService conversionService;

    @Autowired
    private JrReportUtils jrReportUtils;

    public FhReportsFillSourceImpl(T reportModel, Iterator<I> iterator) {
        this.reportModel = reportModel;
        this.iterator = iterator;
        AutowireHelper.autowire(this ,conversionService);
    }

    public void moveFirst() throws JRException {
    }

    public boolean next() throws JRException {
        if (iterator.hasNext()) {
            currentRow = iterator.next();
            return true;
        }
        return false;
    }

    public I getRow() {
        return getCurrentRow();
    }

    @Override
    public Map<String, Object> getValues() {
        Map<String, Object> values = new LinkedHashMap<>();

        List<Field> properties = ReflectionUtils.getFields(reportModel.getClass());
        for (Field property : properties) {
            Optional<Method> propertyGetter = ReflectionUtils.findGetter(reportModel.getClass(), property);
            if (propertyGetter.isPresent()) {
                if (!ReflectionUtils.isAssignablFrom(Iterator.class, property.getType()) &&
                        !ReflectionUtils.isAssignablFrom(Iterable.class, property.getType())) {
                    values.put(property.getName(), getPropertyValueStr(propertyGetter.get(), reportModel));
                }
                values.put(property.getName() + "$obj", getPropertyValue(propertyGetter.get(), reportModel));
            }
        }

        values.put("$utils", jrReportUtils);
        values.put("$model", reportModel);

        return values;
    }

    public Object getFieldValue(JRField jrField) throws JRException {
        if ("$row".equals(jrField.getName())) {
            return getCurrentRow();
        }
        if (String.class.isAssignableFrom(jrField.getValueClass())) {
            return getFieldValueStr(jrField, getRow());
        }
        return getFieldValue(jrField, getRow());
    }

    protected String getFieldValueStr(JRField jrField, I row) {
        try {
            return getPropertyValueStr(ReflectionUtils.findGetter(row.getClass(), row.getClass().getDeclaredField(jrField.getName())).get(), row);
        } catch (Exception e) {
            throw new FhReportsException("No value present", e);
        }
    }

    protected Object getFieldValue(JRField jrField, I row) {
        try {
            return getPropertyValue(ReflectionUtils.findGetter(row.getClass(), row.getClass().getDeclaredField(jrField.getName())).get(), row);
        } catch (Exception e) {
            throw new FhReportsException("No value present", e);
        }
    }

    private String getPropertyValueStr(Method getter, Object object) {
        try {
            Object value = getter.invoke(object);
            return formatValue(value);
        } catch (Exception e) {
            throw new FhReportsException(String.format("Error while retrieving Report value '%s'", getter.getName()), e);
        }
    }

    private Object getPropertyValue(Method getter, Object object) {
        try {
            return getter.invoke(object);
        } catch (Exception e) {
            throw new FhReportsException(String.format("Error while retrieving Report value '%s'", getter.getName()), e);
        }
    }

    public I getCurrentRow() {
        return currentRow;
    }

    protected String formatValue(Object value) {
        return conversionService.convert(value, String.class);
    }
}
