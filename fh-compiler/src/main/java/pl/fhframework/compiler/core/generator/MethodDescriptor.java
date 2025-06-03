package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class MethodDescriptor {

    private Class<?> declaringClass;

    private String name;

    private Class<?> returnType;

    private Type genericReturnType;

    private Class<?>[] parameterTypes;

    private VariableType[] genericParameterTypes;

    private String[] parameterNames;

    private String[] parameterComments;

    private boolean isStatic;

    private boolean hintable;

    private ModelElementType modelElementType;

    private String description;

    public MethodDescriptor(Class<?> declaringClass, String name, Class<?> returnType, Type genericReturnType, Class<?>[] parameterTypes, boolean isStatic, boolean hintable) {
        this.declaringClass = declaringClass;
        this.name = name;
        this.returnType = returnType;
        this.genericReturnType = genericReturnType;
        this.parameterTypes = parameterTypes;
        this.isStatic = isStatic;
        this.hintable = hintable;
    }

    public MethodDescriptor(Class<?> declaringClass, String name, Class<?> returnType, Type genericReturnType, Class<?>[] parameterTypes, boolean isStatic, boolean hintable, ModelElementType modelElementType) {
        this.declaringClass = declaringClass;
        this.name = name;
        this.returnType = returnType;
        this.genericReturnType = genericReturnType;
        this.parameterTypes = parameterTypes;
        this.isStatic = isStatic;
        this.hintable = hintable;
        this.modelElementType = modelElementType;
    }

    public String getConvertedMethodName(String methodName) {
        return methodName;
    }

    public String getConvertedMethodName(String methodName, boolean withGenerics) {
        return methodName;
    }

    public String convertMethodParams(String paramsString) {
        return paramsString;
    }

    public String convertMethodParams(String paramsString, boolean withGenerics) {
        return paramsString;
    }

    public boolean equalsName(String methodName) {
        return getName().equals(methodName);
    }

    public boolean matches(String methodName, Class<?>[] paramClasses) {
        // name matches
        if (!getName().equals(methodName)) {
            return false;
        }
        // nof arguments matches
        Class<?>[] foundParamClasses = getParameterTypes();
        if (foundParamClasses.length != paramClasses.length) {
            return false;
        }
        // each argument's class matches
        for (int i = 0; i < paramClasses.length; i++) {
            if (!ReflectionUtils.isAssignablFrom(foundParamClasses[i], paramClasses[i]) && !BindingParser.NullType.class.isAssignableFrom(paramClasses[i])) {
                return false;
            }
        }
        return true;
    }

    public String getMethodAccessFormat() {
        return ".%s(";
    }

    @Override
    public String toString() {
        return StringUtils.firstLetterToUpper(getDisplayName()) + (parameterTypes.length == 0 ? "" :
                " " + Arrays.stream(parameterTypes).map(VariableType::getTypeName).collect(Collectors.joining(", ", "(",")"))) +
                (ReflectionUtils.isAssignablFrom(Void.class, getReturnType()) ? "" : " : " + VariableType.getTypeName(getGenericReturnType()));
    }

    public boolean parametersMatch(List<VariableType> variableTypes) {
        if (getParameterTypes().length == variableTypes.size()) {
            for (int i = 0; i < variableTypes.size(); i++) {
                if (variableTypes.get(i) == null || getParameterTypes()[i] == null ||
                        (!Objects.equals(BindingParser.NullType.class, variableTypes.get(i).getResolvedType()) && !VariableType.of(getParameterTypes()[i]).getTypeName().equals(variableTypes.get(i).getTypeName()) &&
                                (getGenericParameterTypes() == null || !getGenericParameterTypes()[i].getTypeName().equals(variableTypes.get(i).getTypeName())))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    protected String getDisplayName() {
        return getName();
    }
}