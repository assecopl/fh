package pl.fhframework.compiler.core.uc.dynamic.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.model.SimpleType;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.forms.PageModel;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pawel.Ruta on 2017-06-08.
 */
@Getter
@Setter
public class VariableType implements Serializable {
    private static final Map<String, String> TYPE_MAPPER = new HashMap<>();

    private String type;

    private TypeMultiplicityEnum multiplicity;

    @JsonIgnore
    private Type resolvedType;

    static {
        TYPE_MAPPER.put("java.time.LocalDate", "Date");
        TYPE_MAPPER.put("jav.util.Date", "Timestamp");
        TYPE_MAPPER.put("jav.time.LocalDateTime", "LocalDateTime");
    }

    private VariableType(String type, TypeMultiplicityEnum multiplicity) {
        this.type = type;
        this.multiplicity = multiplicity;
    }

    private VariableType(Type resolvedType) {
        this.resolvedType = resolvedType;
        if (ReflectionUtils.isAssignablFrom(Collection.class, resolvedType)) {
            multiplicity = TypeMultiplicityEnum.Collection;
        }
        else if (ReflectionUtils.isAssignablFrom(PageModel.class, resolvedType)) {
            multiplicity = TypeMultiplicityEnum.MultiplePageable;
        }
        else {
            multiplicity = TypeMultiplicityEnum.Element;
        }
        if (isCollection() || isPageable()) {
            Type[] arguments = ReflectionUtils.getGenericArguments(resolvedType);
            if (arguments.length == 0 || arguments[0] instanceof TypeVariable) {
                type = ReflectionUtils.getClassName(Object.class);
            }
            else {
                type = ReflectionUtils.getClassName(ReflectionUtils.getGenericArgumentsRawClasses(resolvedType)[0]);
            }
        }
        else {
            type = ReflectionUtils.getClassName(ReflectionUtils.getRawClass(resolvedType));
        }
        type = TYPE_MAPPER.getOrDefault(type, type);
    }

    @JsonIgnore
    public String getTypeName() {
        String baseType = getBaseSimpleName();

        if (isCollection()) {
            return baseType.concat(" [1..*]");
        }
        if (isPageable()) {
            return baseType.concat(( " {page}"));
        }
        return baseType;
    }

    @JsonIgnore
    public boolean isCollection() {
        if (multiplicity != null) {
            return multiplicity == TypeMultiplicityEnum.Collection;
        }

        return Collection.class.isAssignableFrom(ReflectionUtils.getRawClass(resolvedType));
    }

    @JsonIgnore
    public boolean isPageable() {
        return multiplicity == TypeMultiplicityEnum.MultiplePageable;
    }

    @JsonIgnore
    public String getBaseType() {
        if (!StringUtils.isNullOrEmpty(type)) {
            return type;
        }
        if (isCollection() || isPageable()) {
            Class[] arguments = ReflectionUtils.getGenericArgumentsRawClasses(resolvedType);
            if (arguments.length == 1) {
                return ReflectionUtils.getClassName(arguments[0]);
            }
        }
        return ReflectionUtils.getClassName(ReflectionUtils.getRawClass(resolvedType));
    }

    @JsonIgnore
    public String getBaseSimpleName() {
        if (!StringUtils.isNullOrEmpty(type)) {
            return DynamicClassName.forClassName(type).getBaseClassName();
        }
        if (isCollection() || isPageable()) {
            Class[] arguments = ReflectionUtils.getGenericArgumentsRawClasses(resolvedType);
            if (arguments.length == 1) {
                return mapTypeNames(ReflectionUtils.getSimpleClassName(arguments[0]));
            }
        }
        return mapTypeNames(ReflectionUtils.getSimpleClassName(ReflectionUtils.getRawClass(resolvedType)));
    }

    public static VariableType of(ParameterDefinition paramDef) {
        return of(paramDef.getType(), paramDef.getMultiplicity());
    }

    public static VariableType of(String type, TypeMultiplicityEnum multiplicity) {
        return new VariableType(type, multiplicity);
    }

    public static VariableType of(Type resolvedType) {
        return new VariableType(resolvedType);
    }

    public ParameterDefinition asParameterDefinition() {
        if (!StringUtils.isNullOrEmpty(type)) {
            return new ParameterDefinition(type, null, multiplicity);
        }
        return new ParameterDefinition(getBaseType(), null, isCollection() ? TypeMultiplicityEnum.Collection : TypeMultiplicityEnum.Element);
    }

    public static String getTypeName(Type type) {

        Class rawClass = ReflectionUtils.getRawClass(type);
        if (Collection.class.isAssignableFrom(rawClass)) {
            Class[] arguments = ReflectionUtils.getGenericArgumentsRawClasses(type);
            if (arguments.length == 1) {
                return mapTypeNames(ReflectionUtils.getSimpleClassName(arguments[0])).concat(" [1..*]");
            }
        }
        if (PageModel.class.isAssignableFrom(rawClass)) {
            Class[] arguments = ReflectionUtils.tryGetGenericArgumentsRawClasses(type);
            if (arguments.length == 1) {
                if (arguments[0] == null) {
                    return PageModel.class.getSimpleName();
                }
                return mapTypeNames(ReflectionUtils.getSimpleClassName(arguments[0])).concat(" {page}");
            }
        }
        return mapTypeNames(ReflectionUtils.getSimpleClassName(rawClass));
    }

    private static String mapTypeNames(String typeName) {
        if ("Date".equals(typeName)) {
            return SimpleType.TIMESTAMP.getType();
        }
        if ("LocalDate".equals(typeName)) {
            return SimpleType.DATE.getType();
        }
        if ("LocalDateTime".equals(typeName)) {
            return SimpleType.DATETIME.getType();
        }
        return typeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariableType that = (VariableType) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (multiplicity != that.multiplicity) return false;
        return resolvedType != null ? resolvedType.equals(that.resolvedType) : that.resolvedType == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (multiplicity != null ? multiplicity.hashCode() : 0);
        result = 31 * result + (resolvedType != null ? resolvedType.hashCode() : 0);
        return result;
    }
}
