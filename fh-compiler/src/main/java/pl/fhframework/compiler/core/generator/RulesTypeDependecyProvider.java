package pl.fhframework.compiler.core.generator;

import pl.fhframework.compiler.core.dynamic.IClassInfo;
import pl.fhframework.compiler.core.rules.DynamicRuleMetadata;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.utils.ModelUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.rules.BusinessRule;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-06-23.
 */
public class RulesTypeDependecyProvider extends RulesTypeProvider {
    private DependenciesContext dependenciesContext;

    private ModelUtils typeUtils;

    public RulesTypeDependecyProvider(DependenciesContext dependenciesContext) {
        this.dependenciesContext = dependenciesContext;
        this.typeUtils = new ModelUtils(dependenciesContext);
    }

    @Override
    protected UseCaseModelUtils getModelUtils() {
        return typeUtils;
    }

    @Override
    protected DynamicRuleMetadata getMetadata(DynamicClassName className) {
        return (DynamicRuleMetadata) dependenciesContext.resolve(className).getMetadata();
    }

    @Override
    protected List<IClassInfo> listClasses() {
        return dependenciesContext.listDependencies().stream().map(dependenciesContext::resolve).filter(
                dependencyResolution -> dependencyResolution.isDynamicClass() && dependencyResolution.getMetadata() instanceof DynamicRuleMetadata ||
                        !dependencyResolution.isDynamicClass() && dependencyResolution.getReadyClass().isAnnotationPresent(BusinessRule.class)).
                map(dependencyResolution -> new ClassInfo(dependencyResolution.isDynamicClass(), DynamicClassName.forClassName(dependencyResolution.getFullClassName()), dependencyResolution.getFullClassName())).collect(Collectors.toList());
    }

    @Override
    protected Class getStaticClass(DynamicClassName className) {
        return dependenciesContext.resolve(className).getReadyClass();
    }


}
