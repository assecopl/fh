package pl.fhframework.compiler.forms;

import pl.fhframework.compiler.core.generator.ITypeProvider;
import pl.fhframework.compiler.core.generator.MethodDescriptor;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.model.forms.Model;
import pl.fhframework.model.forms.PageModel;
import pl.fhframework.model.forms.Property;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal form type provider implementation for binding parser
 */
public class InternalFormModelTypeProvider implements ITypeProvider {

    private List<MethodDescriptor> cachedMethods;

    private Model modelDefinition;

    private DependenciesContext dependenciesContext;

    public InternalFormModelTypeProvider(Model modelDefinition, DependenciesContext dependenciesContext) {
        this.modelDefinition = modelDefinition;
        this.dependenciesContext = dependenciesContext;
    }

    @Override
    public Type getSupportedType() {
        return FormsManager.FORM_INTERNAL_MODEL_TYPE;
    }

    @Override
    public List<MethodDescriptor> getMethods(Type ofType) {
        if (cachedMethods == null) {
            List<MethodDescriptor> methodDescriptors = new ArrayList<>();
            if (modelDefinition != null) {
                for (Property property : modelDefinition.getProperties()) {
                    Class<?> returnClass = getTypeForName(property.getType());
                    Type returnType = returnClass;
                    if (property.getMultiplicity() == Property.PropertyMultiplicity.MULTIPLE) {
                        returnType = ReflectionUtils.createCollectionType(List.class, ReflectionUtils.mapPrimitiveToWrapper(returnClass));
                        returnClass = List.class;
                    } else if (property.getMultiplicity() == Property.PropertyMultiplicity.MULTIPLE_PAGEABLE) {
                        returnType = ReflectionUtils.createParametrizedType(PageModel.class, ReflectionUtils.mapPrimitiveToWrapper(returnClass));
                        returnClass = PageModel.class;
                    }
                    String getterName = ReflectionUtils.getGetterName(property.getName(), returnClass);
                    methodDescriptors.add(new MethodDescriptor(FormsManager.FORM_INTERNAL_MODEL_TYPE, getterName, returnClass, returnType, new Class<?>[0], false, true, ModelElementType.BUSINESS_PROPERTY));
                    String setterName = ReflectionUtils.getSetterName(property.getName());
                    methodDescriptors.add(new MethodDescriptor(FormsManager.FORM_INTERNAL_MODEL_TYPE, setterName, void.class, void.class, new Class<?>[] {returnClass}, false, true));
                }
            }
            cachedMethods = methodDescriptors;
        }
        return cachedMethods;
    }

    @Override
    public String toTypeLiteral() {
        return FormsManager.FORM_INTERNAL_MODEL_CLASS_NAME;
    }

    private Class<?> getTypeForName(String fullClassName) {
        DynamicClassName dynamicClassName = DynamicClassName.forClassName(fullClassName);
        if (dependenciesContext.contains(dynamicClassName)) {
            return dependenciesContext.resolve(dynamicClassName).getReadyClass();
        } else {
            return ReflectionUtils.getClassForName(fullClassName);
        }
    }
}
