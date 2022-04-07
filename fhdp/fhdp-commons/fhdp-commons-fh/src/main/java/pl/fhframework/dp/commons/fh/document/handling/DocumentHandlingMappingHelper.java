package pl.fhframework.dp.commons.fh.document.handling;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.*;

//TODO: generalize? J. Kosecki?
@Getter @Setter @Slf4j
public class DocumentHandlingMappingHelper {

    private Object document;

    private String PREFIX = "class";
    private String PREFIX_ = PREFIX + " ";
    private String FHDP_PREFIX = "pl.fhframework.dp";
    private String FH_PREFIX = "pl.fh";
    private String PRIMITIVE_PREFIX = "java.lang";
    private String PRIMITIVE_PREFIX_CLASS = "java.lang.Class";
    private String LIST_CLASS = "java.util.List";
    private String parent;

    private Config config;

    public DocumentHandlingMappingHelper(Object document) {
        this.document = document;
    }

    public DocumentHandlingMappingHelper(Object document, String parent) {
        this.document = document;
        this.parent = parent;
    }

    public DocumentHandlingMappingHelper(Object document, Config config){
        this(document);
        this.config = config;
    }

    public DocumentHandlingMappingHelper(Object document, String parent, Config config){
        this(document, parent);
        this.config = config;
    }

    private boolean isIncludedClass(String cls) {
        String temp = cls;
        if (temp.startsWith(PREFIX_)) {
            temp = temp.split(PREFIX_)[1];
        }
        List<String> list = new ArrayList<>();
        list.addAll(getIncludeClasses());
        Collections.addAll(list, FHDP_PREFIX, FH_PREFIX);
        return StringUtils.startsWithAny(temp, list.toArray(new String[]{}));
    }

    private List<String> getIncludeClasses(){
        if(null != this.config && null != this.config.getIncludeClasses()){
            return this.config.getIncludeClasses();
        }
        return new ArrayList<String>();
    }

    public Map<String, String> flatMap() {
        Map <String, String> res = new HashedMap();
        try {
            BeanWrapperImpl wrapper = new BeanWrapperImpl(document);
            PropertyDescriptor[] fields = wrapper.getPropertyDescriptors();
            for (PropertyDescriptor f : fields) {
                processProperty(f, wrapper, res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private void processProperty(PropertyDescriptor property, BeanWrapperImpl wrapper, Map <String, String> result){
        String propertyName = getPropertyClassName(property, wrapper);
        if (null == propertyName) {
            return;
        }

        if(LIST_CLASS.equals(propertyName)){
            processListProperty(property, wrapper, result);
        } else if (isIncludedClass(propertyName)) {
            processComplexProperty(property, wrapper, result);
        } else {
            processReadableProperty(property, wrapper, result);
        }
    }

    private void processComplexProperty(PropertyDescriptor property, BeanWrapperImpl wrapper, Map <String, String> result) {
        String propertyName = property.getName();
        Object val = wrapper.getPropertyValue(propertyName);
        if (null != val) {
            String propertyPath = createNewParentName(propertyName);
            DocumentHandlingMappingHelper temp = new DocumentHandlingMappingHelper(val, propertyPath, config);
            result.putAll(temp.flatMap());
        }
    }

    private void processListProperty(PropertyDescriptor property, BeanWrapperImpl wrapper, Map <String, String> result){
        String propertyName = property.getName();
        List<Object> list = (List<Object>) wrapper.getPropertyValue(propertyName);
        if(null == list) {
            return;
        }
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            Object e = list.get(i);
            String propertyPath = createNewParentName(propertyName, i);
            DocumentHandlingMappingHelper temp = new DocumentHandlingMappingHelper(e, propertyPath, config);
            result.putAll(temp.flatMap());
        }
    }

    private void processReadableProperty(PropertyDescriptor property, BeanWrapperImpl wrapper, Map <String, String> result){
        String propertyName = property.getName();
        if(PREFIX.equals(propertyName)){
            return;
        }
        if (wrapper.isReadableProperty(propertyName)) {
            Object rawval = wrapper.getPropertyValue(propertyName);
            String propertyPath = createNewParentName(propertyName);
            if (null == rawval) {
                log.debug(propertyPath + " = NULL");
                return;
            }
            String val = rawval.toString();

            result.put(propertyPath, val);
            log.debug(propertyPath + " = " + val);
        }
    }

    private String createNewParentName(String name){
        if (null != parent) {
            return parent + "." + name;
        }
        return name;
    }

    private String createNewParentName(String name, Integer index){
        return createNewParentName(name) + "[" + index + "]";
    }

    private String getPropertyClassName(PropertyDescriptor property, BeanWrapperImpl wrapper){
        if (property == null || property.getPropertyType() == null) {
            return null;
        }
        Class<?> propertyType = property.getPropertyType();
        String propertyClassName = propertyType.getName();

        if(PRIMITIVE_PREFIX_CLASS.equals(propertyClassName) || Object.class.getName().equals(propertyClassName)){
            Object value = wrapper.getPropertyValue(property.getName());
            if(null == value) {
                return null;
            }
            Class<?> valueClass = value.getClass();
            return valueClass.getName();
        }
        return propertyClassName;
    }

    @Data
    public static class Config {
        List<String> includeClasses;
    }
}
