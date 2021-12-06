package pl.fhframework.compiler.core.generator;

import pl.fhframework.compiler.core.model.DynamicModelMetadata;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.utils.ModelUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.compiler.core.dynamic.IClassInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-06-23.
 */
public class EnumTypeDependencyProvider extends EnumsTypeProvider {
    private DependenciesContext dependenciesContext;

    private ModelUtils typeUtils;

    public EnumTypeDependencyProvider(DependenciesContext dependenciesContext) {
        this.dependenciesContext = dependenciesContext;
        this.typeUtils = new ModelUtils(dependenciesContext);
    }

    @Override
    public List<IClassInfo> listClasses() {
        return dependenciesContext.listDependencies().stream().map(dependenciesContext::resolve).filter(
                dependencyResolution -> dependencyResolution.isDynamicClass() && dependencyResolution.getMetadata() instanceof DynamicModelMetadata &&
                        ((DynamicModelMetadata)dependencyResolution.getMetadata()).getDynamicClass().isEnumeration() ||
                        !dependencyResolution.isDynamicClass() && Enum.class.isAssignableFrom(dependencyResolution.getReadyClass())).
                map(dependencyResolution -> new ClassInfo(dependencyResolution.isDynamicClass(), DynamicClassName.forClassName(dependencyResolution.getFullClassName()), dependencyResolution.getFullClassName())).collect(Collectors.toList());
    }

    @Override
    protected UseCaseModelUtils getModelUtils() {
        return typeUtils;
    }

    @Override
    protected DynamicModelMetadata getMetadata(DynamicClassName className) {
        return (DynamicModelMetadata) dependenciesContext.resolve(className).getMetadata();
    }

    @Override
    protected Class getStaticClass(ClassInfo classInfo) {
        return dependenciesContext.resolve(classInfo.getClassName()).getReadyClass();
    }
}
