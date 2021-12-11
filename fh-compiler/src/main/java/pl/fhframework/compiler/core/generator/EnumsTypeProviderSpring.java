package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import pl.fhframework.compiler.core.model.DynamicModelMetadata;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.compiler.core.dynamic.IClassInfo;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.rules.builtin.CastUtils;

import java.lang.reflect.Type;

/**
 * Created by pawel.ruta on 2017-06-23.
 */
@Component
public class EnumsTypeProviderSpring extends EnumsTypeProviderBase {
    public static final Class<?> ENUM_HINT_TYPE = IEnumHintTypeSpring.class;

    private static interface IEnumHintTypeSpring extends ICollapsePropertiesToMethodName {}

    @Override
    public Type getSupportedType() {
        return ENUM_HINT_TYPE;
    }

    @Override
    protected DescriptorConstructor getEnumDescriptorConstructor() {
        return EnumDescriptorSpring::new;
    }

    @Override
    protected DynamicDescriptorConstructor getDynamicEnumDescriptorConstructor() {
        return EnumDescriptorSpring::new;
    }

    @Override
    protected DescriptorConstructor getValueOfDescriptorConstructor() {
        return ValueOfDescriptorSpring::new;
    }

    @Override
    protected DynamicDescriptorConstructor getDynamicValueOfDescriptorConstructor() {
        return ValueOfDescriptorSpring::new;
    }

    @Getter
    @Setter
    protected static class EnumDescriptorSpring extends EnumDescriptor {

        public EnumDescriptorSpring(IClassInfo classInfo, Class<?> declaringClass, String name, Class<?> returnType, Type genericReturnType, Class<?>[] parameterTypes, boolean isStatic, boolean hintable, ModelElementType modelElementType, UseCaseModelUtils typeUtils) {
            super(classInfo, declaringClass, name, returnType, genericReturnType, parameterTypes, isStatic, hintable, modelElementType, typeUtils);
        }

        public EnumDescriptorSpring(IClassInfo iClassInfo, Class<?> declaringClass, String name, boolean isStatic, boolean hintable, ModelElementType modelElementType, DynamicModelMetadata metadata, UseCaseModelUtils typeUtils) {
            super(iClassInfo, declaringClass, name, isStatic, hintable, modelElementType, metadata, typeUtils);
        }

        @Override
        public String getConvertedMethodName(String methodName, boolean withGenerics) {
            return String.format("T(%s)", AbstractJavaCodeGenerator.toTypeLiteral(getReturnType()));
        }
    }

    @Getter
    @Setter
    protected static class ValueOfDescriptorSpring extends ValueOfDescriptor {

        public ValueOfDescriptorSpring(IClassInfo classInfo, Class<?> declaringClass, String name, Class<?> returnType, Type genericReturnType, Class<?>[] parameterTypes, boolean isStatic, boolean hintable, ModelElementType modelElementType, UseCaseModelUtils typeUtils) {
            super(classInfo, declaringClass, name, returnType, genericReturnType, parameterTypes, isStatic, hintable, modelElementType, typeUtils);
        }

        public ValueOfDescriptorSpring(IClassInfo classInfo, Class<?> declaringClass, String name, boolean isStatic, boolean hintable, ModelElementType modelElementType, DynamicModelMetadata metadata, UseCaseModelUtils typeUtils) {
            super(classInfo, declaringClass, name, isStatic, hintable, modelElementType, metadata, typeUtils);
        }

        @Override
        public String getConvertedMethodName(String methodName, boolean withGenerics) {
            return String.format("T(%s).asList(T(%s).values()",
                    AbstractJavaCodeGenerator.toTypeLiteral(CastUtils.class),
                    AbstractJavaCodeGenerator.toTypeLiteral(ReflectionUtils.getGenericTypeInGenericType(getGenericReturnType(), 0)));
        }
    }
}
