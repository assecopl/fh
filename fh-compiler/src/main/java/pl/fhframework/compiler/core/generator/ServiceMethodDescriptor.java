package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.services.DynamicFhServiceManager;
import pl.fhframework.compiler.core.services.DynamicServiceMetadata;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ServiceMethodDescriptor extends MethodDescriptor {
    private DynamicServiceMetadata metadata;

    private boolean serviceStatic;

    private String comment;

    private List<String> categories = new ArrayList<>();

    public ServiceMethodDescriptor() {
    }

    public ServiceMethodDescriptor(Class<?> declaringClass, String name, Class<?> returnType, Type genericReturnType, Class<?>[] parameterTypes, boolean isServiceStatic, boolean hintable) {
        super(declaringClass, name, returnType, genericReturnType, parameterTypes, false, hintable);
        setDescription(toString());
        this.serviceStatic = isServiceStatic;
    }

    public ServiceMethodDescriptor(Class<?> declaringClass, String name, Class<?> returnType, Type genericReturnType, Class<?>[] parameterTypes, boolean isServiceStatic, boolean hintable, ModelElementType modelElementType) {
        super(declaringClass, name, returnType, genericReturnType, parameterTypes, false, hintable, modelElementType);
        setDescription(toString());
        this.serviceStatic = isServiceStatic;
    }

    @Override
    public String getConvertedMethodName(String methodName) {
        return getConvertedMethodName(methodName, true);
    }

    @Override
    public String getConvertedMethodName(String methodName, boolean withGenerics) {
        if (!withGenerics) {
            return String.format("getService(\"%s\")", methodName);
        }
        if (ReflectionUtils.instanceOf(ReflectionUtils.getRawClass(getGenericReturnType()), DynamicFhServiceManager.SERVICE_HINT_TYPE)) {
            String serviceType = ((ReflectionUtils.SimpleParametrizedType) getGenericReturnType()).getParamClass().getTypeName();
            return String.format("<%s>getService(\"%s\")", serviceType, methodName);
        }
        return String.format("<%s>getService(\"%s\")", AbstractJavaCodeGenerator.toTypeLiteral(ReflectionUtils.mapPrimitiveToWrapper(getGenericReturnType())), methodName);
    }

    public String convertMethodParams(String paramsString) {
        return convertMethodParams(paramsString, true);
    }

    public String convertMethodParams(String paramsString, boolean withGenerics) {
        return paramsString;
    }

    /*@Override
    public boolean equalsName(String methodName) {
        int dotLastIdx = methodName.lastIndexOf('.');
        return super.equalsName(methodName.substring(0, dotLastIdx));
    }*/

    @Override
    public boolean matches(String methodName, Class<?>[] paramClasses) {
        //int dotLastIdx = methodName.lastIndexOf('.');

        return getName().equals("get" + StringUtils.firstLetterToUpper(methodName));
    }

    @Override
    public String getName() {
        return "get" + StringUtils.firstLetterToUpper(super.getName());
    }

    @Override
    public String toString() {
        return StringUtils.firstLetterToUpper(getServiceName());
    }

    public String getServiceName() {
        return StringUtils.firstLetterToUpper(super.getName());
    }
}