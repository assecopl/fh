package pl.fhframework.compiler.core.generator;

import pl.fhframework.compiler.core.services.DynamicServiceMetadata;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.utils.ModelUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.compiler.core.dynamic.IClassInfo;
import pl.fhframework.core.services.FhService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-06-23.
 */
public class FhServicesTypeDependencyProvider extends FhServicesTypeProvider {
    private DependenciesContext dependenciesContext;

    private ModelUtils typeUtils;

    public FhServicesTypeDependencyProvider(DependenciesContext dependenciesContext) {
        this.dependenciesContext = dependenciesContext;
        this.typeUtils = new ModelUtils(dependenciesContext);
    }

    @Override
    protected DynamicServiceMetadata getServiceMetadata(String typeName) {
        return (DynamicServiceMetadata) dependenciesContext.listDependencies().stream().filter(dynamicClassName -> Objects.equals(dynamicClassName.toFullClassName(), typeName)).map(dependenciesContext::resolve).findFirst().get().getMetadata();
    }

    @Override
    protected List<IClassInfo> listServices() {
        return dependenciesContext.listDependencies().stream().map(dependenciesContext::resolve).filter(
                dependencyResolution -> dependencyResolution.isDynamicClass() && dependencyResolution.getMetadata() instanceof DynamicServiceMetadata ||
                        !dependencyResolution.isDynamicClass() && dependencyResolution.getReadyClass().isAnnotationPresent(FhService.class)).
                map(dependencyResolution -> new ClassInfo(dependencyResolution.isDynamicClass(), DynamicClassName.forClassName(dependencyResolution.getFullClassName()), dependencyResolution.getFullClassName())).collect(Collectors.toList());
    }

    @Override
    protected Class getStaticClass(DynamicClassName className) {
        return dependenciesContext.resolve(className).getReadyClass();
    }

    @Override
    protected String getFullClassName(IClassInfo classInfo) {
        return ((ClassInfo)classInfo).getFullClassName();
    }

    @Override
    protected UseCaseModelUtils getModelUtils() {
        return typeUtils;
    }
}
